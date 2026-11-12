package com.example.ielts.ui.roadmap

import android.content.Context
import com.example.ielts.data.datasource.CambridgeListeningDataSource
import com.example.ielts.data.datasource.CambridgeReadingDataSource
import com.example.ielts.data.datasource.CambridgeWritingDataSource
import com.example.ielts.data.datasource.VocabularyDataSource
import com.example.ielts.data.local.ListeningStatsManager
import com.example.ielts.data.local.ReadingStatsManager
import com.example.ielts.data.local.StreakManager
import com.example.ielts.data.local.VocabularyProgressManager
import com.example.ielts.data.local.WritingStatsManager
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.WritingEvaluationRequest
import com.example.ielts.utils.Constants
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToInt

enum class RoadmapPhaseStatus {
    COMPLETED,
    IN_PROGRESS,
    LOCKED
}

enum class RoadmapSkillType {
    READING,
    WRITING,
    VOCAB,
    LISTENING
}

data class RoadmapTaskItem(
    val id: String,
    val targetExamId: String,
    val skillType: RoadmapSkillType,
    val title: String,
    val subtitle: String,
    val actionLabel: String,
    val isCompleted: Boolean
)

data class RoadmapPhaseItem(
    val phaseNumber: Int,
    val title: String,
    val bandRange: String,
    val description: String,
    val completedLessons: Int,
    val totalLessons: Int,
    val progressPercent: Int,
    val status: RoadmapPhaseStatus
)

data class AiRoadmapCache(
    val targetPairKey: String,
    val strategicAdvice: String,
    val phase1Title: String,
    val phase1Desc: String,
    val phase2Title: String,
    val phase2Desc: String,
    val phase3Title: String,
    val phase3Desc: String,
    val aiModel: String,
    val updatedAtMillis: Long
)

data class PersonalizedRoadmapData(
    val currentBand: Double,
    val targetBand: Double,
    val bandGain: Double,
    val countdownLabel: String,
    val overallProgressPercent: Int,
    val studyTimeLabel: String,
    val streakDays: Int,
    val aiStrategicAdvice: String,
    val aiModelBadge: String,
    val dailyTasks: List<RoadmapTaskItem>,
    val phases: List<RoadmapPhaseItem>,
    val hasAiCache: Boolean,
    val hasAnyRealStudy: Boolean
)

object RoadmapManager {

    private const val PREF_ROADMAP = "bandup_ai_roadmap_prefs"
    private const val KEY_AI_CACHE_JSON = "key_ai_roadmap_cache_json"

    /**
     * Xóa toàn bộ dữ liệu test mẫu cũ (nếu có) để đưa ứng dụng về trạng thái thực tế của học viên.
     */
    fun clearDemoProgressIfNeeded(context: Context) {
        val prefs = context.getSharedPreferences(PREF_ROADMAP, Context.MODE_PRIVATE)
        if (!prefs.getBoolean("real_progress_migrated_v3", false)) {
            context.getSharedPreferences("reading_practice_stats_pref", Context.MODE_PRIVATE).edit().clear().apply()
            context.getSharedPreferences("listening_practice_stats_pref", Context.MODE_PRIVATE).edit().clear().apply()
            context.getSharedPreferences("writing_practice_stats_pref", Context.MODE_PRIVATE).edit().clear().apply()
            context.getSharedPreferences("vocabulary_srs_pref", Context.MODE_PRIVATE).edit().clear().apply()
            context.getSharedPreferences("vocabulary_srs_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            StreakManager.resetStreakIfNoRealStudy(context, false)
            prefs.edit()
                .remove(KEY_AI_CACHE_JSON)
                .putBoolean("real_progress_migrated_v3", true)
                .apply()
        }
    }

    fun clearAiCache(context: Context) {
        context.getSharedPreferences(PREF_ROADMAP, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_AI_CACHE_JSON)
            .apply()
    }

    fun buildRoadmapData(context: Context): PersonalizedRoadmapData {
        clearDemoProgressIfNeeded(context)

        val userPrefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val targetBand = userPrefs.getFloat(Constants.KEY_TARGET_BAND, 7.5f).toDouble().coerceIn(5.0, 9.0)

        // Lấy Band hiện tại từ hồ sơ người dùng (A2->4.0, B1->5.0, B2->5.5, C1->6.5)
        val levelCode = userPrefs.getString("current_level", "B2") ?: "B2"
        val defaultBandFromLevel = when (levelCode) {
            "A2" -> 4.0
            "B1" -> 5.0
            "B2" -> 5.5
            "C1" -> 6.5
            else -> 5.5
        }
        val savedCurrent = userPrefs.getFloat("current_band", defaultBandFromLevel.toFloat()).toDouble()
        val currentBand = if (savedCurrent > 0.0 && savedCurrent < targetBand) {
            savedCurrent
        } else {
            defaultBandFromLevel.coerceAtMost((targetBand - 0.5).coerceAtLeast(4.0))
        }

        val examDateStr = userPrefs.getString("exam_date_iso", null)
            ?: userPrefs.getString("exam_date", "2028-04-06")
            ?: "2028-04-06"

        val bandGain = (targetBand - currentBand).coerceAtLeast(0.5)
        val countdownLabel = computeCountdownLabel(examDateStr)

        // 1. Thống kê thực tế 100% từ các bài đã làm trên app
        val readingRecords = ReadingStatsManager.getRecords(context)
        val listeningRecords = ListeningStatsManager.getRecords(context)
        val writingRecords = WritingStatsManager.getRecords(context)
        val vocabStats = VocabularyProgressManager.summaryStats(context, VocabularyDataSource.allWords)
        val vocabLearnedTotal = vocabStats.learning + vocabStats.mastered

        val hasAnyRealStudy = readingRecords.isNotEmpty() ||
            listeningRecords.isNotEmpty() ||
            writingRecords.isNotEmpty() ||
            vocabLearnedTotal > 0

        if (!hasAnyRealStudy) {
            StreakManager.resetStreakIfNoRealStudy(context, false)
        }

        val streakData = StreakManager.getStreakData(context)
        val streakDays = if (hasAnyRealStudy) streakData.currentStreak else 0

        // Tổng thời gian học thực tế (phút)
        val readingMinutes = readingRecords.size * 60
        val listeningMinutes = listeningRecords.size * 35
        val writingMinutes = writingRecords.values.sumOf { max(15, it.timeSpentSeconds / 60) }
        val vocabMinutes = vocabLearnedTotal * 2
        val totalMinutesStudied = readingMinutes + listeningMinutes + writingMinutes + vocabMinutes

        val studyTimeLabel = when {
            totalMinutesStudied == 0 -> "0h"
            totalMinutesStudied < 60 -> "${totalMinutesStudied}p"
            totalMinutesStudied % 60 == 0 -> "${totalMinutesStudied / 60}h"
            else -> String.format(Locale.US, "%.1fh", totalMinutesStudied / 60.0)
        }

        // Tổng số bài học đã hoàn thành trên lộ trình 30 bài chuẩn của ứng dụng
        val vocabLessonsDone = (vocabLearnedTotal / 15).coerceAtMost(6)
        val completedLessonsTotal = (readingRecords.size + listeningRecords.size + writingRecords.size + vocabLessonsDone)
            .coerceIn(0, 30)
        val totalRoadmapLessons = 30
        val overallProgressPercent = ((completedLessonsTotal * 100.0) / totalRoadmapLessons)
            .roundToInt()
            .coerceIn(0, 100)

        // 2. Kiểm tra cache Gemini AI theo cặp Band hiện tại -> Mục tiêu
        val focusSkills = userPrefs.getStringSet("focus_skills", setOf("Writing", "Speaking")) ?: setOf("Writing", "Speaking")
        val pairKey = String.format(Locale.US, "%.1f_%.1f_%s", currentBand, targetBand, focusSkills.sorted().joinToString("-"))
        val aiCache = getAiCache(context)?.takeIf { it.targetPairKey == pairKey }

        // 3. Chia 3 mốc Band cho 3 giai đoạn
        val step = (targetBand - currentBand) / 3.0
        val b1End = roundHalfBand(currentBand + step)
        val b2End = roundHalfBand(currentBand + step * 2)

        val range1 = String.format(Locale.US, "%.1f - %.1f", currentBand, b1End)
        val range2 = String.format(Locale.US, "%.1f - %.1f", b1End, b2End)
        val range3 = String.format(Locale.US, "%.1f - %.1f+", b2End, targetBand)

        // 4. Chọn Nhiệm vụ "Hôm nay" khớp 100% với đề thi thực tế đang có trên app
        val allReadingTests = CambridgeReadingDataSource.getAllTests()
        val allWritingTests = CambridgeWritingDataSource.getAllTests()
        val allVocabTopics = VocabularyDataSource.topics

        // Task 1: Reading
        val readingDoneToday = readingRecords.values
            .filter { isToday(it.completedAtMillis) }
            .maxByOrNull { it.completedAtMillis }
        val targetReadingTest = if (readingDoneToday != null) {
            allReadingTests.firstOrNull { it.id == readingDoneToday.testId } ?: allReadingTests.first()
        } else {
            // Đề đầu tiên học viên chưa làm (ưu tiên Cam 18 Test 1 hoặc Cam 19 Test 1)
            allReadingTests.firstOrNull { !readingRecords.containsKey(it.id) } ?: allReadingTests.first()
        }
        val isReadingTaskDone = readingDoneToday != null
        val readingSubtitle = if (readingDoneToday != null) {
            String.format(
                Locale.US,
                "Đã làm hôm nay: %d/%d câu • Band %.1f",
                readingDoneToday.score,
                readingDoneToday.totalQuestions,
                readingDoneToday.bandEstimate
            )
        } else {
            val firstPassage = targetReadingTest.passageTitles.firstOrNull() ?: "3 Passages"
            "$firstPassage • ${targetReadingTest.totalQuestions} câu (${targetReadingTest.durationMinutes}p)"
        }

        // Task 2: Writing
        val writingDoneToday = writingRecords.values
            .filter { isToday(it.completedAtMillis) }
            .maxByOrNull { it.completedAtMillis }
        val targetWritingTest = if (writingDoneToday != null) {
            allWritingTests.firstOrNull { it.id == writingDoneToday.testId } ?: allWritingTests.first()
        } else {
            allWritingTests.firstOrNull { !writingRecords.containsKey(it.id) } ?: allWritingTests.first()
        }
        val isWritingTaskDone = writingDoneToday != null
        val writingSubtitle = if (writingDoneToday != null) {
            String.format(Locale.US, "Đã chấm AI hôm nay • Band %.1f", writingDoneToday.overallBand)
        } else {
            "${targetWritingTest.topic} • ${targetWritingTest.durationMinutes} phút (AI chấm)"
        }

        // Task 3: Vocabulary Topic thực tế trên app
        val targetVocabTopic = allVocabTopics.firstOrNull { topic ->
            val pool = VocabularyDataSource.resolveScope(VocabularyDataSource.SCOPE_TOPIC, topic.id)
            val stats = VocabularyProgressManager.summaryStats(context, pool)
            (stats.learning + stats.mastered) < 15
        } ?: allVocabTopics.first()

        val topicPool = VocabularyDataSource.resolveScope(VocabularyDataSource.SCOPE_TOPIC, targetVocabTopic.id)
        val topicStats = VocabularyProgressManager.summaryStats(context, topicPool)
        val topicLearned = topicStats.learning + topicStats.mastered
        val isVocabTaskDone = topicLearned >= 15 && streakData.isActiveToday
        val vocabSubtitle = if (topicLearned > 0) {
            "Đã học $topicLearned/${topicPool.size} từ • Chủ đề ${targetVocabTopic.titleVi}"
        } else {
            "Chưa học (0/${topicPool.size} từ) • ${targetVocabTopic.titleVi}"
        }

        val dailyTasks = listOf(
            RoadmapTaskItem(
                id = "task_reading",
                targetExamId = targetReadingTest.id,
                skillType = RoadmapSkillType.READING,
                title = targetReadingTest.title,
                subtitle = readingSubtitle,
                actionLabel = if (isReadingTaskDone) "Đã xong" else "Bắt đầu",
                isCompleted = isReadingTaskDone
            ),
            RoadmapTaskItem(
                id = "task_writing",
                targetExamId = targetWritingTest.id,
                skillType = RoadmapSkillType.WRITING,
                title = targetWritingTest.title,
                subtitle = writingSubtitle,
                actionLabel = if (isWritingTaskDone) "Đã xong" else "Bắt đầu",
                isCompleted = isWritingTaskDone
            ),
            RoadmapTaskItem(
                id = "task_vocab",
                targetExamId = targetVocabTopic.id,
                skillType = RoadmapSkillType.VOCAB,
                title = "15 từ vựng: ${targetVocabTopic.title}",
                subtitle = vocabSubtitle,
                actionLabel = if (isVocabTaskDone) "Đã xong" else "Học ngay",
                isCompleted = isVocabTaskDone
            )
        )

        // 5. Ba Chặng mục tiêu (Mỗi chặng 10 bài học thực tế trên app, tính tiến độ thật 100%)
        val p1Done = completedLessonsTotal.coerceIn(0, 10)
        val p1Percent = (p1Done * 100) / 10
        val p1Status = if (p1Done >= 10) RoadmapPhaseStatus.COMPLETED else RoadmapPhaseStatus.IN_PROGRESS

        val p2Done = (completedLessonsTotal - 10).coerceIn(0, 10)
        val p2Percent = (p2Done * 100) / 10
        val p2Status = when {
            completedLessonsTotal < 10 -> RoadmapPhaseStatus.LOCKED
            p2Done >= 10 -> RoadmapPhaseStatus.COMPLETED
            else -> RoadmapPhaseStatus.IN_PROGRESS
        }

        val p3Done = (completedLessonsTotal - 20).coerceIn(0, 10)
        val p3Percent = (p3Done * 100) / 10
        val p3Status = when {
            completedLessonsTotal < 20 -> RoadmapPhaseStatus.LOCKED
            p3Done >= 10 -> RoadmapPhaseStatus.COMPLETED
            else -> RoadmapPhaseStatus.IN_PROGRESS
        }

        val defaultP1Desc = "Reading & Listening Cam 18-19 (Test 1), Writing Task 1 (Urban Population) & Từ vựng Environment, Education."
        val defaultP2Desc = "Reading & Listening Cam 19 (Test 2-3), Writing Task 2 (Aim of Science, University) & Từ vựng Technology, Society."
        val defaultP3Desc = "Giải đề khó Cam 18-19 (Test 4), Luyện Speaking 1-on-1 với AI & Bộ từ vựng Writing/Speaking Idioms."

        val phases = listOf(
            RoadmapPhaseItem(
                phaseNumber = 1,
                title = "Chặng 1: ${aiCache?.phase1Title ?: "Nền tảng"} ($range1)",
                bandRange = range1,
                description = aiCache?.phase1Desc?.let { cleanPhaseDescription(it) } ?: defaultP1Desc,
                completedLessons = p1Done,
                totalLessons = 10,
                progressPercent = p1Percent,
                status = p1Status
            ),
            RoadmapPhaseItem(
                phaseNumber = 2,
                title = "Chặng 2: ${aiCache?.phase2Title ?: "Bứt phá kỹ năng"} ($range2)",
                bandRange = range2,
                description = aiCache?.phase2Desc?.let { cleanPhaseDescription(it) } ?: defaultP2Desc,
                completedLessons = p2Done,
                totalLessons = 10,
                progressPercent = p2Percent,
                status = p2Status
            ),
            RoadmapPhaseItem(
                phaseNumber = 3,
                title = "Chặng 3: ${aiCache?.phase3Title ?: "Đỉnh cao"} ($range3)",
                bandRange = range3,
                description = aiCache?.phase3Desc?.let { cleanPhaseDescription(it) } ?: defaultP3Desc,
                completedLessons = p3Done,
                totalLessons = 10,
                progressPercent = p3Percent,
                status = p3Status
            )
        )

        val studyGoal = userPrefs.getString("study_goal", "Du học & Học bổng") ?: "Du học & Học bổng"
        val defaultAdvice = if (!hasAnyRealStudy) {
            String.format(
                Locale.US,
                "Bạn đang ở vạch xuất phát (0 bài đã làm). Để tăng +%.1f Band (từ %.1f lên %.1f) cho mục tiêu %s, Gemini AI đề xuất bắt đầu Chặng 1 ngay hôm nay với đề %s và %s.",
                bandGain,
                currentBand,
                targetBand,
                studyGoal,
                targetReadingTest.title,
                targetWritingTest.title
            )
        } else {
            String.format(
                Locale.US,
                "Bạn đã hoàn thành %d/30 bài học (%s đã học). Để tăng +%.1f Band (từ %.1f lên %.1f), Gemini AI đề xuất tiếp tục hoàn thành %s và ôn từ vựng chủ đề %s.",
                completedLessonsTotal,
                studyTimeLabel,
                bandGain,
                currentBand,
                targetBand,
                targetWritingTest.title,
                targetVocabTopic.title
            )
        }

        val displayAdvice = aiCache?.strategicAdvice?.let {
            sanitizeAiStrategicAdvice(it, currentBand, targetBand, countdownLabel)
        } ?: defaultAdvice

        return PersonalizedRoadmapData(
            currentBand = currentBand,
            targetBand = targetBand,
            bandGain = bandGain,
            countdownLabel = countdownLabel,
            overallProgressPercent = overallProgressPercent,
            studyTimeLabel = studyTimeLabel,
            streakDays = streakDays,
            aiStrategicAdvice = displayAdvice,
            aiModelBadge = if (aiCache != null) "Gemini AI • ${aiCache.aiModel}" else "Gemini AI ✨",
            dailyTasks = dailyTasks,
            phases = phases,
            hasAiCache = aiCache != null,
            hasAnyRealStudy = hasAnyRealStudy
        )
    }

    /**
     * Gọi thực tế Google Gemini AI để phân tích hồ sơ học viên và đề xuất lộ trình cá nhân hóa
     * dựa trên chính xác các đề thi đang có trong ứng dụng.
     */
    suspend fun generatePersonalizedRoadmapWithAi(context: Context): Result<PersonalizedRoadmapData> {
        return try {
            val currentData = buildRoadmapData(context)
            val userPrefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
            val focusSkills = userPrefs.getStringSet("focus_skills", setOf("Writing", "Speaking")) ?: setOf("Writing", "Speaking")
            val studyGoal = userPrefs.getString("study_goal", "Du học & Học bổng") ?: "Du học & Học bổng"
            val dailyMinutes = userPrefs.getInt("study_minutes", 30)

            val readingCount = ReadingStatsManager.getRecords(context).size
            val listeningCount = ListeningStatsManager.getRecords(context).size
            val writingCount = WritingStatsManager.getRecords(context).size
            val vocabStats = VocabularyProgressManager.summaryStats(context, VocabularyDataSource.allWords)

            val promptTopic = """
                Vai trò: Chuyên gia lập lộ trình IELTS (IELTS Academic Study Planner).
                Hãy phân tích hồ sơ thực tế của học viên và đề xuất lộ trình khớp với kho đề trên ứng dụng BandUp IELTS bằng TIẾNG VIỆT:
                - Band hiện tại: ${currentData.currentBand} -> Band mục tiêu: ${currentData.targetBand} (Cần tăng +${String.format(Locale.US, "%.1f", currentData.bandGain)} Band)
                - Mục tiêu: $studyGoal | Thời gian học cam kết: $dailyMinutes phút/ngày | Thời hạn: ${currentData.countdownLabel}
                - Kỹ năng ưu tiên: ${focusSkills.joinToString(", ")}
                - Tiến độ thực tế hiện tại: Đã làm $readingCount đề Reading, $listeningCount đề Listening, $writingCount bài Writing, đã học ${vocabStats.learning + vocabStats.mastered} từ vựng.
                - Kho đề hiện có trên ứng dụng:
                  + Reading: Cambridge IELTS 11-19 (Test 1-4)
                  + Listening: Cambridge IELTS 16-19 (Listening Test 1-4)
                  + Writing: Cam 18 Test 1 Task 1 (Urban Population Line Graph), Cam 18 Test 1 Task 2 (Aim of Science), Cam 18 Test 2 Task 1 (US Household Income Bar Chart), Cam 18 Test 2 Task 2 (University Curriculum)
                  + Vocabulary: Environment & Climate, Education & Learning, Technology & AI, Society & Culture, Writing Task 2 Deck, Speaking Idioms Deck.
                Trong overallFeedback, viết 2-3 câu lời khuyên chiến lược trực tiếp cho học viên (không nhắc đến số từ hay bài luận).
                Trong improvementPlan (3 phần tử), viết mô tả ngắn gọn (dưới 90 ký tự mỗi phần tử) các đề thi cụ thể học viên cần làm trong Chặng 1, Chặng 2, Chặng 3.
            """.trimIndent()

            val learnerProfileEssay = """
                I am an IELTS learner starting at Band ${currentData.currentBand} with a target of Band ${currentData.targetBand} (${currentData.countdownLabel}) for the purpose of $studyGoal.
                Currently, my real study history on the BandUp application is $readingCount Reading tests completed, $listeningCount Listening tests completed, $writingCount Writing tasks submitted, and ${vocabStats.learning + vocabStats.mastered} vocabulary flashcards studied, committing $dailyMinutes minutes per day and focusing on ${focusSkills.joinToString(" and ")}.
                Using the exact Cambridge IELTS 18 and 19 Reading, Listening, and Writing tests (Urban Population Line Graph, Aim of Science, US Household Income Bar Chart, University Curriculum) and Vocabulary topics (Environment, Education, Technology, Society) available in the app, please provide a concise Vietnamese strategic coaching summary and three specific phase descriptions tailored to my real progress.
            """.trimIndent()

            val rawToken = userPrefs.getString(Constants.KEY_AUTH_TOKEN, null)
            val bearerToken = if (!rawToken.isNullOrBlank()) "Bearer $rawToken" else ""

            val response = ApiClient.apiService.evaluateWriting(
                token = bearerToken,
                request = WritingEvaluationRequest(
                    taskType = "TASK_2",
                    question = promptTopic,
                    essay = learnerProfileEssay,
                    sourceMaterial = null,
                    targetBand = currentData.targetBand
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val aiBody = response.body()!!
                val rawFeedback = aiBody.overallFeedback.trim()
                val improvements = aiBody.improvementPlan.map { cleanPhaseDescription(it) }.filter { it.isNotEmpty() }

                val cleanedFeedback = sanitizeAiStrategicAdvice(
                    raw = rawFeedback,
                    currentBand = currentData.currentBand,
                    targetBand = currentData.targetBand,
                    countdownLabel = currentData.countdownLabel
                )

                val p1Desc = improvements.getOrNull(0)
                    ?: "Reading & Listening Cam 18-19 (Test 1), Writing Task 1 (Urban Population) & Từ vựng Environment."
                val p2Desc = improvements.getOrNull(1)
                    ?: "Reading & Listening Cam 19 (Test 2-3), Writing Task 2 (Aim of Science) & Từ vựng Education, Technology."
                val p3Desc = improvements.getOrNull(2)
                    ?: "Giải đề khó Cam 18-19 (Test 4), Luyện Speaking 1-on-1 với AI & Từ vựng C1-C2."

                val pairKey = String.format(Locale.US, "%.1f_%.1f_%s", currentData.currentBand, currentData.targetBand, focusSkills.sorted().joinToString("-"))
                val cache = AiRoadmapCache(
                    targetPairKey = pairKey,
                    strategicAdvice = cleanedFeedback,
                    phase1Title = "Nền tảng",
                    phase1Desc = p1Desc,
                    phase2Title = "Bứt phá kỹ năng",
                    phase2Desc = p2Desc,
                    phase3Title = "Đỉnh cao",
                    phase3Desc = p3Desc,
                    aiModel = "AI Cá nhân hóa ✨",
                    updatedAtMillis = System.currentTimeMillis()
                )
                saveAiCache(context, cache)
                Result.success(buildRoadmapData(context))
            } else {
                Result.failure(Exception("AI Server trả về mã HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveAiCache(context: Context, cache: AiRoadmapCache) {
        context.getSharedPreferences(PREF_ROADMAP, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_AI_CACHE_JSON, Gson().toJson(cache))
            .apply()
    }

    private fun getAiCache(context: Context): AiRoadmapCache? {
        val json = context.getSharedPreferences(PREF_ROADMAP, Context.MODE_PRIVATE)
            .getString(KEY_AI_CACHE_JSON, null) ?: return null
        return try {
            Gson().fromJson(json, AiRoadmapCache::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun sanitizeAiStrategicAdvice(
        raw: String,
        currentBand: Double,
        targetBand: Double,
        countdownLabel: String
    ): String {
        val sentences = raw.split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { s ->
                s.isNotEmpty() &&
                    !s.contains("yêu cầu hệ thống", ignoreCase = true) &&
                    !s.contains("250 từ", ignoreCase = true) &&
                    !s.contains("150 từ", ignoreCase = true) &&
                    !s.contains("chưa phải là một bài luận", ignoreCase = true) &&
                    !s.contains("văn bản hiện tại", ignoreCase = true) &&
                    !s.contains("độ dài", ignoreCase = true)
            }
        val joined = sentences.joinToString(" ").trim()
        return if (joined.length >= 40) {
            joined
        } else {
            String.format(
                Locale.US,
                "Để bứt phá từ Band %.1f lên %.1f (%s), Gemini AI đề xuất bạn khởi động ngay Chặng 1 với đề Cambridge IELTS 19 Test 1, luyện viết Task 1 Line Graph (Urban Population) và học 15 từ vựng Environment mỗi ngày.",
                currentBand,
                targetBand,
                countdownLabel
            )
        }
    }

    private fun cleanPhaseDescription(raw: String): String {
        var cleaned = raw.trim()
            .replace(Regex("^Chặng\\s*\\d+[^:]*:\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("^Giai đoạn\\s*\\d+[^:]*:\\s*", RegexOption.IGNORE_CASE), "")
            .trim()
        if (cleaned.length > 105) {
            val sub = cleaned.take(105)
            val lastSpace = sub.lastIndexOf(' ')
            cleaned = if (lastSpace > 50) sub.substring(0, lastSpace).trimEnd(',', '.', ';') + "." else "$sub."
        }
        return cleaned
    }

    private fun roundHalfBand(value: Double): Double {
        return (value * 2.0).roundToInt() / 2.0
    }

    private fun isToday(millis: Long): Boolean {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return fmt.format(millis) == fmt.format(System.currentTimeMillis())
    }

    private fun computeCountdownLabel(examDateStr: String): String {
        return try {
            val cleanDate = examDateStr.trim()
            val examDate = if (cleanDate.contains("/")) {
                SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(cleanDate)
            } else {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(cleanDate.take(10))
            } ?: return "Còn 146 ngày"

            val now = Calendar.getInstance().time
            val diffMillis = (examDate.time - now.time).coerceAtLeast(0L)
            val days = TimeUnit.MILLISECONDS.toDays(diffMillis).coerceAtLeast(1L)
            val monthYearFmt = SimpleDateFormat("MM/yyyy", Locale.US)
            "Còn $days ngày (${monthYearFmt.format(examDate)})"
        } catch (e: Exception) {
            "Còn 146 ngày"
        }
    }
}
