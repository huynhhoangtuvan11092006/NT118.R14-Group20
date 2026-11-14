package com.example.ielts.data.local

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.SyncProgressRequest
import com.example.ielts.data.remote.dto.UpdateProfileRequest
import com.example.ielts.data.remote.dto.UserDto
import com.example.ielts.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.math.max

data class CloudLearningSnapshot(
    val streakDates: Set<String> = emptySet(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String? = null,
    val readingRecords: Map<String, SavedTestRecord> = emptyMap(),
    val listeningRecords: Map<String, SavedListeningTestRecord> = emptyMap(),
    val writingRecords: Map<String, SavedWritingRecord> = emptyMap(),
    val vocabProgress: Map<String, WordProgress> = emptyMap(),
    val vocabDailyCounts: Map<String, Int> = emptyMap(),
    val vocabTotalXp: Int = 0,
    val currentLevel: String? = null,
    val focusSkills: List<String>? = null,
    val studyGoal: String? = null,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

object CloudSyncManager {
    private const val TAG = "CloudSyncManager"
    private const val SYNC_URL_PREFIX = "https://sync.bandup.app/v1?d="
    private const val KEY_LAST_SYNCED_EMAIL = "last_synced_user_email"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()

    /**
     * Xuất toàn bộ trạng thái học tập cục bộ thành snapshot để đồng bộ lên Cloud.
     * Nếu [compact] = true (dành cho URL nhúng trong hồ sơ), loại bỏ chuỗi JSON chi tiết lớn
     * nhưng giữ nguyên 100% điểm số, thời gian học, danh sách bài đã làm, từ vựng SRS và Streak.
     */
    fun exportLocalSnapshot(context: Context, compact: Boolean = false): CloudLearningSnapshot {
        val appCtx = context.applicationContext
        val readingMap = ReadingStatsManager.getRecords(appCtx)
        val listeningMap = ListeningStatsManager.getRecords(appCtx)
        val writingMap = WritingStatsManager.getRecords(appCtx)
        val vocabMap = VocabularyProgressManager.getAll(appCtx)
        val vocabDaily = VocabularyProgressManager.getDailyCountsMap(appCtx)
        val vocabXp = appCtx.getSharedPreferences("vocabulary_srs_pref", Context.MODE_PRIVATE)
            .getInt("key_vocab_total_xp", 0)
        val streakData = StreakManager.getStreakData(appCtx)
        val completedDates = appCtx.getSharedPreferences("streak_pref", Context.MODE_PRIVATE)
            .getStringSet("completed_dates", emptySet())?.toSet() ?: emptySet()

        val userPrefs = appCtx.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val currentLevel = userPrefs.getString("current_level", null)
        val focusSkills = userPrefs.getStringSet("focus_skills", null)?.toList()
        val studyGoal = userPrefs.getString("study_goal", null)

        val effectiveReading = if (compact) {
            readingMap.mapValues { (_, v) -> v.copy(resultJson = null, passagesJson = null) }
        } else {
            readingMap
        }
        val effectiveListening = if (compact) {
            listeningMap.mapValues { (_, v) -> v.copy(resultJson = null, sectionsJson = null) }
        } else {
            listeningMap
        }
        val effectiveWriting = if (compact) {
            writingMap.mapValues { (_, v) -> v.copy(resultJson = null) }
        } else {
            writingMap
        }

        return CloudLearningSnapshot(
            streakDates = completedDates,
            currentStreak = streakData.currentStreak,
            longestStreak = streakData.longestStreak,
            lastActiveDate = streakData.lastActiveDate,
            readingRecords = effectiveReading,
            listeningRecords = effectiveListening,
            writingRecords = effectiveWriting,
            vocabProgress = vocabMap,
            vocabDailyCounts = vocabDaily,
            vocabTotalXp = vocabXp,
            currentLevel = currentLevel,
            focusSkills = focusSkills,
            studyGoal = studyGoal,
            updatedAtMillis = System.currentTimeMillis()
        )
    }

    /**
     * Nén GZIP + Base64URL để nhúng vào trường avatarUrl hợp lệ trên Cloud.
     */
    fun encodeSnapshotToSyncUrl(snapshot: CloudLearningSnapshot): String? {
        return try {
            val jsonBytes = gson.toJson(snapshot).toByteArray(Charsets.UTF_8)
            val bos = ByteArrayOutputStream()
            GZIPOutputStream(bos).use { it.write(jsonBytes) }
            val compressed = bos.toByteArray()
            val b64 = Base64.encodeToString(
                compressed,
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )
            "$SYNC_URL_PREFIX$b64"
        } catch (e: Exception) {
            Log.w(TAG, "Không thể nén snapshot: ${e.message}")
            null
        }
    }

    fun decodeSnapshotFromSyncUrl(url: String?): CloudLearningSnapshot? {
        if (url.isNullOrBlank() || !url.startsWith(SYNC_URL_PREFIX)) return null
        return try {
            val b64 = url.removePrefix(SYNC_URL_PREFIX).trim()
            val compressed = Base64.decode(b64, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val json = GZIPInputStream(ByteArrayInputStream(compressed)).bufferedReader(Charsets.UTF_8).use { it.readText() }
            gson.fromJson(json, CloudLearningSnapshot::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "Không thể giải mã sync URL: ${e.message}")
            null
        }
    }

    /**
     * Nếu một tài khoản email KHÁC đăng nhập vào cùng chiếc điện thoại này,
     * xóa dữ liệu học cục bộ của tài khoản trước đó để không bị trộn lẫn giữa 2 người dùng.
     */
    fun ensureUserIsolation(context: Context, email: String?) {
        if (email.isNullOrBlank()) return
        val appCtx = context.applicationContext
        val userPrefs = appCtx.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val normalizedNew = email.trim().lowercase()
        val previousEmail = userPrefs.getString(KEY_LAST_SYNCED_EMAIL, null)?.trim()?.lowercase()

        if (previousEmail != null && previousEmail.isNotEmpty() && previousEmail != normalizedNew) {
            appCtx.getSharedPreferences("reading_practice_stats_pref", Context.MODE_PRIVATE).edit().clear().commit()
            appCtx.getSharedPreferences("listening_practice_stats_pref", Context.MODE_PRIVATE).edit().clear().commit()
            appCtx.getSharedPreferences("writing_practice_stats_pref", Context.MODE_PRIVATE).edit().clear().commit()
            appCtx.getSharedPreferences("writing_practice_prefs", Context.MODE_PRIVATE).edit().clear().commit()
            appCtx.getSharedPreferences("writing_drafts_pref", Context.MODE_PRIVATE).edit().clear().commit()
            appCtx.getSharedPreferences("vocabulary_srs_pref", Context.MODE_PRIVATE).edit().clear().commit()
            appCtx.getSharedPreferences("streak_pref", Context.MODE_PRIVATE).edit().clear().commit()
            appCtx.getSharedPreferences("bandup_ai_roadmap_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        }

        userPrefs.edit().putString(KEY_LAST_SYNCED_EMAIL, normalizedNew).commit()
    }

    /**
     * Hợp nhất (Merge) dữ liệu từ Cloud về Local (giữ toàn bộ bài đã làm, từ vựng đã học và ngày Streak).
     */
    fun mergeCloudSnapshotIntoLocal(context: Context, cloud: CloudLearningSnapshot) {
        val appCtx = context.applicationContext

        // 1. Merge Reading records
        if (cloud.readingRecords.isNotEmpty()) {
            val readingPrefs = appCtx.getSharedPreferences("reading_practice_stats_pref", Context.MODE_PRIVATE)
            val merged = ReadingStatsManager.getRecords(appCtx).toMutableMap()
            for ((testId, cloudRec) in cloud.readingRecords) {
                val localRec = merged[testId]
                if (localRec == null || cloudRec.completedAtMillis > localRec.completedAtMillis) {
                    merged[testId] = cloudRec.copy(
                        resultJson = cloudRec.resultJson ?: localRec?.resultJson,
                        passagesJson = cloudRec.passagesJson ?: localRec?.passagesJson
                    )
                }
            }
            val latestBand = merged.values.maxByOrNull { it.completedAtMillis }?.bandEstimate?.toFloat() ?: 0f
            readingPrefs.edit()
                .putString("key_recorded_tests_map", gson.toJson(merged))
                .putFloat("key_recent_band", latestBand)
                .commit()
        }

        // 2. Merge Listening records
        if (cloud.listeningRecords.isNotEmpty()) {
            val listeningPrefs = appCtx.getSharedPreferences("listening_practice_stats_pref", Context.MODE_PRIVATE)
            val merged = ListeningStatsManager.getRecords(appCtx).toMutableMap()
            for ((testId, cloudRec) in cloud.listeningRecords) {
                val localRec = merged[testId]
                if (localRec == null || cloudRec.completedAtMillis > localRec.completedAtMillis) {
                    merged[testId] = cloudRec.copy(
                        resultJson = cloudRec.resultJson ?: localRec?.resultJson,
                        sectionsJson = cloudRec.sectionsJson ?: localRec?.sectionsJson
                    )
                }
            }
            val latestBand = merged.values.maxByOrNull { it.completedAtMillis }?.bandEstimate?.toFloat() ?: 0f
            listeningPrefs.edit()
                .putString("key_listening_recorded_tests_map", gson.toJson(merged))
                .putFloat("key_listening_recent_band", latestBand)
                .commit()
        }

        // 3. Merge Writing records
        if (cloud.writingRecords.isNotEmpty()) {
            val writingPrefs = appCtx.getSharedPreferences("writing_practice_stats_pref", Context.MODE_PRIVATE)
            val merged = WritingStatsManager.getRecords(appCtx).toMutableMap()
            for ((testId, cloudRec) in cloud.writingRecords) {
                val localRec = merged[testId]
                if (localRec == null || cloudRec.completedAtMillis > localRec.completedAtMillis) {
                    merged[testId] = cloudRec.copy(
                        resultJson = cloudRec.resultJson ?: localRec?.resultJson
                    )
                }
            }
            val latestBand = merged.values.maxByOrNull { it.completedAtMillis }?.overallBand?.toFloat() ?: 0f
            writingPrefs.edit()
                .putString("key_recorded_writing_map", gson.toJson(merged))
                .putFloat("key_recent_band", latestBand)
                .commit()
        }

        // 4. Merge Vocabulary SRS progress & daily counts
        if (cloud.vocabProgress.isNotEmpty() || cloud.vocabDailyCounts.isNotEmpty()) {
            val vocabPrefs = appCtx.getSharedPreferences("vocabulary_srs_pref", Context.MODE_PRIVATE)
            val mergedVocab = VocabularyProgressManager.getAll(appCtx).toMutableMap()
            for ((wordId, cloudWp) in cloud.vocabProgress) {
                val localWp = mergedVocab[wordId]
                if (localWp == null ||
                    (cloudWp.seen && !localWp.seen) ||
                    cloudWp.level > localWp.level ||
                    cloudWp.nextReviewMillis > localWp.nextReviewMillis
                ) {
                    mergedVocab[wordId] = cloudWp
                }
            }

            val dailyType = object : TypeToken<Map<String, Int>>() {}.type
            val rawLocalDaily = vocabPrefs.getString("key_vocab_daily_counts", null)
            val mergedDaily = (if (rawLocalDaily != null) {
                try {
                    gson.fromJson<Map<String, Int>>(rawLocalDaily, dailyType) ?: emptyMap()
                } catch (_: Exception) {
                    emptyMap()
                }
            } else {
                emptyMap()
            }).toMutableMap()

            for ((dateStr, cloudCount) in cloud.vocabDailyCounts) {
                val localCount = mergedDaily[dateStr] ?: 0
                if (cloudCount > localCount) {
                    mergedDaily[dateStr] = cloudCount
                }
            }

            val localXp = vocabPrefs.getInt("key_vocab_total_xp", 0)
            vocabPrefs.edit()
                .putString("key_vocab_progress_map", gson.toJson(mergedVocab))
                .putString("key_vocab_daily_counts", gson.toJson(mergedDaily))
                .putInt("key_vocab_total_xp", max(localXp, cloud.vocabTotalXp))
                .commit()
        }

        // 5. Merge Streak data
        val streakPrefs = appCtx.getSharedPreferences("streak_pref", Context.MODE_PRIVATE)
        val localDates = streakPrefs.getStringSet("completed_dates", emptySet())?.toMutableSet() ?: mutableSetOf()
        val hadCloudStreak = cloud.streakDates.isNotEmpty() || cloud.currentStreak > 0
        if (hadCloudStreak) {
            localDates.addAll(cloud.streakDates)
            val localCur = streakPrefs.getInt("current_streak", 0)
            val localLong = streakPrefs.getInt("longest_streak", 0)
            val latestDate = listOfNotNull(
                streakPrefs.getString("last_active_date", null),
                cloud.lastActiveDate,
                localDates.maxOrNull()
            ).maxOrNull()

            val mergedCur = max(localCur, cloud.currentStreak)
            val mergedLong = maxOf(localLong, cloud.longestStreak, mergedCur)

            streakPrefs.edit()
                .putStringSet("completed_dates", HashSet(localDates))
                .putInt("current_streak", mergedCur)
                .putInt("longest_streak", mergedLong)
                .apply {
                    if (latestDate != null) putString("last_active_date", latestDate)
                }
                .commit()
        }

        // 6. Merge User study preferences
        val userPrefs = appCtx.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val userEdit = userPrefs.edit()
        var changedUserPrefs = false
        if (!cloud.currentLevel.isNullOrBlank() && !userPrefs.contains("current_level")) {
            userEdit.putString("current_level", cloud.currentLevel)
            changedUserPrefs = true
        }
        if (!cloud.focusSkills.isNullOrEmpty() && !userPrefs.contains("focus_skills")) {
            userEdit.putStringSet("focus_skills", cloud.focusSkills.toSet())
            changedUserPrefs = true
        }
        if (!cloud.studyGoal.isNullOrBlank() && !userPrefs.contains("study_goal")) {
            userEdit.putString("study_goal", cloud.studyGoal)
            changedUserPrefs = true
        }
        if (changedUserPrefs) {
            userEdit.commit()
        }

        // Trigger StreakManager reconciliation so streak reflects all merged records
        StreakManager.getStreakData(appCtx)
    }

    /**
     * Đẩy toàn bộ tiến độ học tập hiện tại lên Cloud (chạy bất đồng bộ trên IO thread).
     */
    fun pushToCloudAsync(context: Context) {
        val appCtx = context.applicationContext
        val userPrefs = appCtx.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val token = userPrefs.getString(Constants.KEY_AUTH_TOKEN, null)
        if (token.isNullOrBlank()) return

        scope.launch {
            pushToCloudSuspend(appCtx, token)
        }
    }

    suspend fun pushToCloudSuspend(context: Context, rawToken: String? = null) = withContext(Dispatchers.IO) {
        val appCtx = context.applicationContext
        val userPrefs = appCtx.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val token = rawToken ?: userPrefs.getString(Constants.KEY_AUTH_TOKEN, null)
        if (token.isNullOrBlank()) return@withContext
        val bearer = if (token.startsWith("Bearer ")) token else "Bearer $token"

        try {
            val compactSnapshot = exportLocalSnapshot(appCtx, compact = true)
            val hasAnyData = compactSnapshot.currentStreak > 0 ||
                compactSnapshot.streakDates.isNotEmpty() ||
                compactSnapshot.readingRecords.isNotEmpty() ||
                compactSnapshot.listeningRecords.isNotEmpty() ||
                compactSnapshot.writingRecords.isNotEmpty() ||
                compactSnapshot.vocabProgress.isNotEmpty()

            if (!hasAnyData) {
                Log.d(TAG, "Bỏ qua đồng bộ lên Cloud vì chưa có dữ liệu học tập cục bộ")
                return@withContext
            }

            // Kênh 1 (Ưu tiên tốc độ tức thì): Nhúng gói GZIP đồng bộ vào avatarUrl của User Profile trên PostgreSQL
            val syncUrl = encodeSnapshotToSyncUrl(compactSnapshot)
            if (syncUrl != null && syncUrl.length <= 4096) {
                try {
                    val resp = ApiClient.apiService.updateProfile(
                        token = bearer,
                        request = UpdateProfileRequest(avatarUrl = syncUrl)
                    )
                    Log.d(TAG, "Đã đồng bộ tiến độ lên Cloud (Kênh 1): HTTP ${resp.code()}, len=${syncUrl.length}")
                } catch (e: Exception) {
                    Log.w(TAG, "Lỗi đồng bộ Kênh 1: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Đồng bộ lên Cloud tạm hoãn: ${e.message}")
        }
    }

    /**
     * Tải tiến độ học tập từ Cloud về máy và hợp nhất vào bộ nhớ cục bộ.
     */
    suspend fun pullFromCloudSuspend(context: Context, userDtoFromAuth: UserDto? = null) = withContext(Dispatchers.IO) {
        val appCtx = context.applicationContext
        val userPrefs = appCtx.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val token = userPrefs.getString(Constants.KEY_AUTH_TOKEN, null)
        if (token.isNullOrBlank()) return@withContext
        val bearer = if (token.startsWith("Bearer ")) token else "Bearer $token"

        val email = userDtoFromAuth?.email ?: userPrefs.getString(Constants.KEY_USER_EMAIL, null)
        ensureUserIsolation(appCtx, email)

        var mergedFromCloud = false

        // 1. Ưu tiên đọc tức thì từ userDtoFromAuth.avatarUrl (0ms, không tốn thêm HTTP request)
        decodeSnapshotFromSyncUrl(userDtoFromAuth?.avatarUrl)?.let { snapshot ->
            mergeCloudSnapshotIntoLocal(appCtx, snapshot)
            mergedFromCloud = true
        }

        // 2. Nếu chưa có userDtoFromAuth, kiểm tra từ /api/v1/users/me
        if (userDtoFromAuth == null) {
            try {
                val meResp = ApiClient.apiService.getMe(bearer)
                if (meResp.isSuccessful) {
                    val user = meResp.body()?.user
                    if (user != null) {
                        ensureUserIsolation(appCtx, user.email)
                        decodeSnapshotFromSyncUrl(user.avatarUrl)?.let { snapshot ->
                            mergeCloudSnapshotIntoLocal(appCtx, snapshot)
                            mergedFromCloud = true
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }

        // 3. Đẩy ngay trạng thái đã hợp nhất lên Cloud (cập nhật avatarUrl đồng bộ)
        pushToCloudSuspend(appCtx, bearer)
    }
}
