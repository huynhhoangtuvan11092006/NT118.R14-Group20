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
import com.example.ielts.data.remote.dto.AiRoadmapRequest
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

data class WeeklyDayStudyItem(
    val dayLabel: String,       // "T2".."CN"
    val dateStr: String,        // "yyyy-MM-dd"
    val minutes: Int,           // Số phút học thực tế trong ngày
    val isToday: Boolean,
    val metDailyGoal: Boolean
)

data class WeeklyStudySummary(
    val days: List<WeeklyDayStudyItem>,
    val totalMinutesWeek: Int,
    val totalBadgeLabel: String,
    val dailyGoalMinutes: Int,
    val goalNoteLabel: String,
    val summaryRightLabel: String
)

data class PersonalizedRoadmapData(
    val currentBand: Double,
    val targetBand: Double,
    val bandGain: Double,
    val countdownLabel: String,
    val overallProgressPercent: Int,
    val completedLessonsCount: Int,
    val totalLessonsCount: Int,
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

    // Danh sách 5 bài học thực tế cho từng Chặng (Tổng lộ trình chuẩn: 15 bài học trên app)
    private val PHASE_1_READING_IDS = listOf("cam19-test1", "cam18-test1")
    private val PHASE_1_LISTENING_IDS = listOf("cam19-listening-test1")
    private val PHASE_1_WRITING_IDS = listOf("cam18-test1-task1")
    private const val PHASE_1_VOCAB_TOPIC = VocabularyDataSource.TOPIC_ENVIRONMENT

    private val PHASE_2_READING_IDS = listOf("cam19-test2", "cam18-test2")
    private val PHASE_2_LISTENING_IDS = listOf("cam19-listening-test2")
    private val PHASE_2_WRITING_IDS = listOf("cam18-test1-task2")
    private const val PHASE_2_VOCAB_TOPIC = VocabularyDataSource.TOPIC_EDUCATION

    private val PHASE_3_READING_IDS = listOf("cam19-test3")
    private val PHASE_3_LISTENING_IDS = listOf("cam19-listening-test3")
    private val PHASE_3_WRITING_IDS = listOf("cam18-test2-task1", "cam18-test2-task2")
    private const val PHASE_3_VOCAB_TOPIC = VocabularyDataSource.TOPIC_TECHNOLOGY

    /**
     * Xóa toàn bộ dữ liệu test mẫu cũ (nếu có) để đưa ứng dụng về đúng trạng thái thực tế của học viên.
     */
    fun clearDemoProgressIfNeeded(context: Context) {
        val prefs = context.getSharedPreferences(PREF_ROADMAP, Context.MODE_PRIVATE)
        if (!prefs.getBoolean("real_progress_migrated_v5", false)) {
            prefs.edit()
                .remove(KEY_AI_CACHE_JSON)
                .putBoolean("real_progress_migrated_v4", true)
                .putBoolean("real_progress_migrated_v5", true)
                .apply()
        }
    }

    fun clearAiCache(context: Context) {
        context.getSharedPreferences(PREF_ROADMAP, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_AI_CACHE_JSON)
            .apply()
    }

    private fun countTopicLearned(context: Context, topicId: String): Int {
        val pool = VocabularyDataSource.resolveScope(VocabularyDataSource.SCOPE_TOPIC, topicId)
        val stats = VocabularyProgressManager.summaryStats(context, pool)
        return stats.learning + stats.mastered
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

        val streakData = StreakManager.getStreakData(context)
        val streakDays = streakData.currentStreak
        val hasAnyRealStudy = readingRecords.isNotEmpty() ||
            listeningRecords.isNotEmpty() ||
            writingRecords.isNotEmpty() ||
            vocabLearnedTotal > 0 ||
            streakDays > 0

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

        // 2. Tính số bài hoàn thành thực tế cho từng Chặng (Mỗi chặng đúng 5 bài cụ thể trên app)
        val p1Done = PHASE_1_READING_IDS.count { readingRecords.containsKey(it) } +
            PHASE_1_LISTENING_IDS.count { listeningRecords.containsKey(it) } +
            PHASE_1_WRITING_IDS.count { writingRecords.containsKey(it) } +
            if (countTopicLearned(context, PHASE_1_VOCAB_TOPIC) >= 15) 1 else 0

        val p2Done = PHASE_2_READING_IDS.count { readingRecords.containsKey(it) } +
            PHASE_2_LISTENING_IDS.count { listeningRecords.containsKey(it) } +
            PHASE_2_WRITING_IDS.count { writingRecords.containsKey(it) } +
            if (countTopicLearned(context, PHASE_2_VOCAB_TOPIC) >= 15) 1 else 0

        val p3Done = PHASE_3_READING_IDS.count { readingRecords.containsKey(it) } +
            PHASE_3_LISTENING_IDS.count { listeningRecords.containsKey(it) } +
            PHASE_3_WRITING_IDS.count { writingRecords.containsKey(it) } +
            if (countTopicLearned(context, PHASE_3_VOCAB_TOPIC) >= 15) 1 else 0

        val completedLessonsTotal = (p1Done + p2Done + p3Done).coerceIn(0, 15)
        val totalRoadmapLessons = 15
        val overallProgressPercent = ((completedLessonsTotal * 100.0) / totalRoadmapLessons)
            .roundToInt()
            .coerceIn(0, 100)

        // 3. Kiểm tra cache AI theo cặp Band hiện tại -> Mục tiêu
        val focusSkills = userPrefs.getStringSet("focus_skills", setOf("Writing", "Speaking")) ?: setOf("Writing", "Speaking")
        val pairKey = String.format(Locale.US, "%.1f_%.1f_%s", currentBand, targetBand, focusSkills.sorted().joinToString("-"))
        val aiCache = getAiCache(context)?.takeIf { it.targetPairKey == pairKey }

        // 4. Chia 3 mốc Band cho 3 giai đoạn
        val step = (targetBand - currentBand) / 3.0
        val b1End = roundHalfBand(currentBand + step)
        val b2End = roundHalfBand(currentBand + step * 2)

        val range1 = String.format(Locale.US, "%.1f - %.1f", currentBand, b1End)
        val range2 = String.format(Locale.US, "%.1f - %.1f", b1End, b2End)
        val range3 = String.format(Locale.US, "%.1f - %.1f+", b2End, targetBand)

        // 5. Chọn Nhiệm vụ "Hôm nay" khớp 100% với đề thi thực tế đang có trên app
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
            val priorityIds = PHASE_1_READING_IDS + PHASE_2_READING_IDS + PHASE_3_READING_IDS
            val nextId = priorityIds.firstOrNull { !readingRecords.containsKey(it) }
            allReadingTests.firstOrNull { it.id == nextId }
                ?: allReadingTests.firstOrNull { !readingRecords.containsKey(it.id) }
                ?: allReadingTests.first()
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
            val priorityWritingIds = PHASE_1_WRITING_IDS + PHASE_2_WRITING_IDS + PHASE_3_WRITING_IDS
            val nextWId = priorityWritingIds.firstOrNull { !writingRecords.containsKey(it) }
            allWritingTests.firstOrNull { it.id == nextWId }
                ?: allWritingTests.firstOrNull { !writingRecords.containsKey(it.id) }
                ?: allWritingTests.first()
        }
        val isWritingTaskDone = writingDoneToday != null
        val writingSubtitle = if (writingDoneToday != null) {
            String.format(Locale.US, "Đã chấm AI hôm nay • Band %.1f", writingDoneToday.overallBand)
        } else {
            "${targetWritingTest.topic} • ${targetWritingTest.durationMinutes} phút (AI chấm)"
        }

        // Task 3: Vocabulary Topic thực tế trên app (ưu tiên chủ đề đang học dở để hiển thị đúng số từ vừa học)
        val targetVocabTopic = allVocabTopics.firstOrNull { topic ->
            val learned = countTopicLearned(context, topic.id)
            learned in 1..14
        } ?: allVocabTopics.firstOrNull { topic ->
            countTopicLearned(context, topic.id) < 15
        } ?: allVocabTopics.first()

        val topicPool = VocabularyDataSource.resolveScope(VocabularyDataSource.SCOPE_TOPIC, targetVocabTopic.id)
        val topicLearned = countTopicLearned(context, targetVocabTopic.id)
        val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(java.util.Date())
        val todayWordsStudied = VocabularyProgressManager.getDailyCountsMap(context)[todayIso] ?: 0
        val effectiveVocabLearned = max(topicLearned, todayWordsStudied)
        val isVocabTaskDone = effectiveVocabLearned >= 15
        val vocabSubtitle = if (effectiveVocabLearned > 0) {
            "Đã học $effectiveVocabLearned/15 từ mục tiêu • Chủ đề ${targetVocabTopic.titleVi}"
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

        // 6. Ba Chặng mục tiêu (Mỗi chặng đúng 5 bài học cụ thể trên app)
        val p1Percent = (p1Done * 100) / 5
        val p1Status = if (p1Done >= 5) RoadmapPhaseStatus.COMPLETED else RoadmapPhaseStatus.IN_PROGRESS

        val p2Percent = (p2Done * 100) / 5
        val p2Status = when {
            p1Done < 5 -> RoadmapPhaseStatus.LOCKED
            p2Done >= 5 -> RoadmapPhaseStatus.COMPLETED
            else -> RoadmapPhaseStatus.IN_PROGRESS
        }

        val p3Percent = (p3Done * 100) / 5
        val p3Status = when {
            p1Done < 5 || p2Done < 5 -> RoadmapPhaseStatus.LOCKED
            p3Done >= 5 -> RoadmapPhaseStatus.COMPLETED
            else -> RoadmapPhaseStatus.IN_PROGRESS
        }

        val defaultP1Desc = "5 bài: Reading Cam 19 Test 1, Listening Cam 19 Test 1, Reading Cam 18 Test 1, Writing Task 1 (Urban Population) & Từ vựng Environment."
        val defaultP2Desc = "5 bài: Reading Cam 19 Test 2, Listening Cam 19 Test 2, Reading Cam 18 Test 2, Writing Task 2 (Aim of Science) & Từ vựng Education."
        val defaultP3Desc = "5 bài: Reading Cam 19 Test 3, Listening Cam 19 Test 3, Writing Task 1 (US Income), Writing Task 2 (University) & Từ vựng Technology."

        val phases = listOf(
            RoadmapPhaseItem(
                phaseNumber = 1,
                title = "Chặng 1: ${cleanPhaseTitle(aiCache?.phase1Title, "Nền tảng")} ($range1)",
                bandRange = range1,
                description = aiCache?.phase1Desc?.takeIf { it.isNotBlank() } ?: defaultP1Desc,
                completedLessons = p1Done,
                totalLessons = 5,
                progressPercent = p1Percent,
                status = p1Status
            ),
            RoadmapPhaseItem(
                phaseNumber = 2,
                title = "Chặng 2: ${cleanPhaseTitle(aiCache?.phase2Title, "Bứt phá kỹ năng")} ($range2)",
                bandRange = range2,
                description = aiCache?.phase2Desc?.takeIf { it.isNotBlank() } ?: defaultP2Desc,
                completedLessons = p2Done,
                totalLessons = 5,
                progressPercent = p2Percent,
                status = p2Status
            ),
            RoadmapPhaseItem(
                phaseNumber = 3,
                title = "Chặng 3: ${cleanPhaseTitle(aiCache?.phase3Title, "Đỉnh cao")} ($range3)",
                bandRange = range3,
                description = aiCache?.phase3Desc?.takeIf { it.isNotBlank() } ?: defaultP3Desc,
                completedLessons = p3Done,
                totalLessons = 5,
                progressPercent = p3Percent,
                status = p3Status
            )
        )

        val studyGoal = userPrefs.getString("study_goal", "Du học & Học bổng") ?: "Du học & Học bổng"
        val defaultAdvice = if (!hasAnyRealStudy) {
            String.format(
                Locale.US,
                "Bạn đang ở vạch xuất phát (0/15 bài đã làm). Để tăng +%.1f Band (từ %.1f lên %.1f) cho mục tiêu %s, hãy bắt đầu Chặng 1 (5 bài) ngay hôm nay với %s và %s.",
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
                "Bạn đã hoàn thành %d/15 bài học (%s đã học). Để tăng +%.1f Band (từ %.1f lên %.1f), hãy tiếp tục hoàn thành %s và ôn từ vựng chủ đề %s.",
                completedLessonsTotal,
                studyTimeLabel,
                bandGain,
                currentBand,
                targetBand,
                targetWritingTest.title,
                targetVocabTopic.title
            )
        }

        val rawAdvice = aiCache?.strategicAdvice?.takeIf { it.isNotBlank() } ?: defaultAdvice
        val displayAdvice = rawAdvice.replace(Regex("(?i)gemini\\s*ai|ai\\s*gemini|gemini"), "AI")

        return PersonalizedRoadmapData(
            currentBand = currentBand,
            targetBand = targetBand,
            bandGain = bandGain,
            countdownLabel = countdownLabel,
            overallProgressPercent = overallProgressPercent,
            completedLessonsCount = completedLessonsTotal,
            totalLessonsCount = totalRoadmapLessons,
            studyTimeLabel = studyTimeLabel,
            streakDays = streakDays,
            aiStrategicAdvice = displayAdvice,
            aiModelBadge = "AI ✨",
            dailyTasks = dailyTasks,
            phases = phases,
            hasAiCache = aiCache != null,
            hasAnyRealStudy = hasAnyRealStudy
        )
    }

    /**
     * Tính toán thống kê thời gian học thực tế của 7 ngày trong tuần hiện tại (T2 -> CN)
     * dựa trên lịch sử làm bài Reading, Listening, Writing và Từ vựng SRS.
     */
    fun getWeeklyStudySummary(context: Context): WeeklyStudySummary {
        clearDemoProgressIfNeeded(context)
        val userPrefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val dailyGoalMinutes = userPrefs.getInt("daily_minutes", 30).coerceAtLeast(15)

        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = fmt.format(System.currentTimeMillis())

        val readingRecords = ReadingStatsManager.getRecords(context).values
        val listeningRecords = ListeningStatsManager.getRecords(context).values
        val writingRecords = WritingStatsManager.getRecords(context).values
        val vocabDailyCounts = VocabularyProgressManager.getDailyCountsMap(context)

        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

        val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        val dayItems = mutableListOf<WeeklyDayStudyItem>()

        for (i in 0..6) {
            val dStr = fmt.format(cal.time)
            val rMin = readingRecords.count { fmt.format(it.completedAtMillis) == dStr } * 60
            val lMin = listeningRecords.count { fmt.format(it.completedAtMillis) == dStr } * 35
            val wMin = writingRecords
                .filter { fmt.format(it.completedAtMillis) == dStr }
                .sumOf { max(15, it.timeSpentSeconds / 60) }
            val vMin = (vocabDailyCounts[dStr] ?: 0) * 2

            val totalDayMin = rMin + lMin + wMin + vMin
            dayItems.add(
                WeeklyDayStudyItem(
                    dayLabel = labels[i],
                    dateStr = dStr,
                    minutes = totalDayMin,
                    isToday = (dStr == todayStr),
                    metDailyGoal = totalDayMin >= dailyGoalMinutes
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val totalWeekMin = dayItems.sumOf { it.minutes }
        val totalBadge = when {
            totalWeekMin == 0 -> "Tổng: 0 phút"
            totalWeekMin < 60 -> "Tổng: $totalWeekMin phút"
            totalWeekMin % 60 == 0 -> "Tổng: ${totalWeekMin / 60} giờ"
            else -> String.format(Locale.US, "Tổng: %.1f giờ", totalWeekMin / 60.0)
        }

        val activeDays = dayItems.count { it.minutes > 0 }
        val metGoalDays = dayItems.count { it.metDailyGoal }
        val summaryRight = when {
            totalWeekMin == 0 -> "Chưa có giờ học tuần này"
            metGoalDays > 0 -> "Đạt mục tiêu $metGoalDays/7 ngày"
            else -> "Đã học $activeDays/7 ngày tuần này"
        }

        return WeeklyStudySummary(
            days = dayItems,
            totalMinutesWeek = totalWeekMin,
            totalBadgeLabel = totalBadge,
            dailyGoalMinutes = dailyGoalMinutes,
            goalNoteLabel = "Mục tiêu ngày (≥ ${dailyGoalMinutes}p)",
            summaryRightLabel = summaryRight
        )
    }

    /**
     * Gọi thực tế AI trên Backend để phân tích hồ sơ học viên
     * và sinh lời khuyên Cố vấn chiến lược + 3 Chặng mục tiêu (5 bài/chặng).
     */
    suspend fun generatePersonalizedRoadmapWithAi(context: Context): Result<PersonalizedRoadmapData> {
        return try {
            val currentData = buildRoadmapData(context)
            val userPrefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
            val focusSkills = (userPrefs.getStringSet("focus_skills", setOf("Writing", "Speaking"))
                ?: setOf("Writing", "Speaking")).toList()
            val studyGoal = userPrefs.getString("study_goal", "Du học & Học bổng") ?: "Du học & Học bổng"
            val dailyMinutes = userPrefs.getInt("study_minutes", 30)

            val rawToken = userPrefs.getString(Constants.KEY_AUTH_TOKEN, null)
            val bearerToken = if (!rawToken.isNullOrBlank()) "Bearer $rawToken" else ""

            val completedTotal = currentData.phases.sumOf { it.completedLessons }
            val pairKey = String.format(
                Locale.US,
                "%.1f_%.1f_%s",
                currentData.currentBand,
                currentData.targetBand,
                focusSkills.sorted().joinToString("-")
            )

            // 1. Ưu tiên gọi Endpoint chuyên biệt /api/v1/ai/evaluate/roadmap
            try {
                val roadmapResp = ApiClient.apiService.generateRoadmap(
                    token = bearerToken,
                    request = AiRoadmapRequest(
                        currentBand = currentData.currentBand,
                        targetBand = currentData.targetBand,
                        studyGoal = studyGoal,
                        dailyMinutes = dailyMinutes,
                        countdownLabel = currentData.countdownLabel,
                        focusSkills = focusSkills,
                        completedLessons = completedTotal,
                        totalLessons = 15
                    )
                )
                if (roadmapResp.isSuccessful && roadmapResp.body() != null) {
                    val body = roadmapResp.body()!!
                    val cache = AiRoadmapCache(
                        targetPairKey = pairKey,
                        strategicAdvice = body.strategicAdvice.trim(),
                        phase1Title = body.phase1Title.trim().ifEmpty { "Nền tảng" },
                        phase1Desc = ensureFiveLessonsPrefix(body.phase1Desc, 1),
                        phase2Title = body.phase2Title.trim().ifEmpty { "Bứt phá kỹ năng" },
                        phase2Desc = ensureFiveLessonsPrefix(body.phase2Desc, 2),
                        phase3Title = body.phase3Title.trim().ifEmpty { "Đỉnh cao" },
                        phase3Desc = ensureFiveLessonsPrefix(body.phase3Desc, 3),
                        aiModel = "AI Cá nhân hóa ✨",
                        updatedAtMillis = System.currentTimeMillis()
                    )
                    saveAiCache(context, cache)
                    return Result.success(buildRoadmapData(context))
                }
            } catch (_: Exception) {
                // Nếu server đang redeploy, chuyển sang nhánh fallback bên dưới
            }

            // 2. Fallback qua /api/v1/ai/evaluate/writing nếu endpoint mới đang cập nhật
            val promptTopic = """
                Vai trò: Chuyên gia lập lộ trình IELTS (IELTS Academic Study Planner).
                Hãy phân tích hồ sơ học viên bên dưới và đề xuất chiến lược học tập cá nhân hóa bằng TIẾNG VIỆT:
                - Band hiện tại: ${currentData.currentBand} -> Band mục tiêu: ${currentData.targetBand} (Cần tăng +${String.format(Locale.US, "%.1f", currentData.bandGain)} Band)
                - Mục tiêu: $studyGoal | Cam kết: $dailyMinutes phút/ngày | Kỹ năng ưu tiên: ${focusSkills.joinToString(", ")}
                - Tiến độ hiện tại: Đã hoàn thành $completedTotal/15 bài học trên ứng dụng.
                Trong overallFeedback và improvementPlan (3 phần tử), hãy nêu rõ:
                1. Nhận xét chiến lược ngắn gọn (2-3 câu) bằng tiếng Việt về trọng tâm kỹ năng cần bứt phá từ ${currentData.currentBand} lên ${currentData.targetBand}.
                2. Gợi ý cụ thể nội dung trọng tâm cho 3 giai đoạn (mỗi chặng đúng 5 bài trên app).
            """.trimIndent()

            val learnerProfileEssay = """
                I am currently at IELTS Band ${currentData.currentBand} and aiming for Target Band ${currentData.targetBand} (${currentData.countdownLabel}).
                My current learning record includes $completedTotal out of 15 lessons completed on the BandUp app, committing $dailyMinutes minutes per day and focusing on ${focusSkills.joinToString(" and ")}.
                To bridge the +${String.format(Locale.US, "%.1f", currentData.bandGain)} band gap effectively, I am following a three-phase roadmap of 5 lessons per phase using Cambridge IELTS 18-19 Reading, Listening, Writing, and SRS Vocabulary.
                Please evaluate my learning profile and provide a tailored Vietnamese strategic study plan and specific skill milestones for my journey from Band ${currentData.currentBand} to Band ${currentData.targetBand}.
            """.trimIndent()

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

                val p1Desc = ensureFiveLessonsPrefix(improvements.getOrNull(0) ?: "", 1)
                val p2Desc = ensureFiveLessonsPrefix(improvements.getOrNull(1) ?: "", 2)
                val p3Desc = ensureFiveLessonsPrefix(improvements.getOrNull(2) ?: "", 3)

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

    /**
     * Đảm bảo mô tả của từng Chặng luôn liệt kê rõ đúng 5 bài học thực tế trong chặng đó
     * kèm theo định hướng chiến lược từ AI.
     */
    private fun ensureFiveLessonsPrefix(aiDesc: String, phase: Int): String {
        val coreLessons = when (phase) {
            1 -> "5 bài: Reading Cam 19 Test 1, Listening Cam 19 Test 1, Reading Cam 18 Test 1, Writing Task 1 (Urban Population) & Từ vựng Environment."
            2 -> "5 bài: Reading Cam 19 Test 2, Listening Cam 19 Test 2, Reading Cam 18 Test 2, Writing Task 2 (Aim of Science) & Từ vựng Education."
            else -> "5 bài: Reading Cam 19 Test 3, Listening Cam 19 Test 3, Writing Task 1 (US Income), Writing Task 2 (University) & Từ vựng Technology."
        }
        val cleaned = cleanPhaseDescription(aiDesc)
        return if (cleaned.length >= 15 && cleaned.contains("Cam", ignoreCase = true)) {
            if (cleaned.contains("5 bài", ignoreCase = true)) cleaned else "5 bài: $cleaned"
        } else {
            coreLessons
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
                "Để bứt phá từ Band %.1f lên %.1f (%s), AI đề xuất bạn khởi động ngay Chặng 1 (5 bài) với đề Cambridge IELTS 19 Test 1, luyện viết Task 1 Line Graph (Urban Population) và học 15 từ vựng Environment.",
                currentBand,
                targetBand,
                countdownLabel
            )
        }
    }

    private fun cleanPhaseTitle(raw: String?, fallback: String): String {
        if (raw.isNullOrBlank()) return fallback
        val cleaned = raw.trim()
            .replace(Regex("^Chặng\\s*\\d+\\s*:\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("\\s*\\([^)]*\\)\\s*$"), "")
            .trim()
        return cleaned.ifEmpty { fallback }
    }

    private fun cleanPhaseDescription(raw: String): String {
        var cleaned = raw.trim()
            .replace(Regex("^Chặng\\s*\\d+[^:]*:\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("^Giai đoạn\\s*\\d+[^:]*:\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("^5\\s*bài\\s*:\\s*(Hoàn thành\\s*5\\s*bài\\s*:\\s*)?", RegexOption.IGNORE_CASE), "")
            .replace(Regex("^Hoàn thành\\s*5\\s*bài\\s*(học\\s*)?(gồm\\s*)?:\\s*", RegexOption.IGNORE_CASE), "")
            .trim()
        if (cleaned.length > 220) {
            val sub = cleaned.take(220)
            val lastSpace = sub.lastIndexOf(' ')
            cleaned = if (lastSpace > 80) sub.substring(0, lastSpace).trimEnd(',', '.', ';') + "." else "$sub."
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
