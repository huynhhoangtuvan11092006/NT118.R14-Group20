package com.example.ielts

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.ielts.data.datasource.CambridgeReadingDataSource
import com.example.ielts.data.datasource.CambridgeWritingDataSource
import com.example.ielts.data.datasource.VocabularyDataSource
import com.example.ielts.data.local.StreakManager
import com.example.ielts.data.local.VocabularyProgressManager
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.LogoutRequest
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.auth.WelcomeActivity
import com.example.ielts.ui.practice.PracticeHubActivity
import com.example.ielts.ui.reading.ReadingListActivity
import com.example.ielts.ui.reading.ReadingPracticeActivity
import com.example.ielts.ui.reminder.StudyReminderActivity
import com.example.ielts.ui.reminder.StudyReminderScheduler
import com.example.ielts.ui.roadmap.PersonalizedRoadmapData
import com.example.ielts.ui.roadmap.RoadmapActivity
import com.example.ielts.ui.roadmap.RoadmapManager
import com.example.ielts.ui.streak.StreakDetailBottomSheet
import com.example.ielts.ui.vocabulary.FlashcardActivity
import com.example.ielts.ui.vocabulary.VocabularyHubActivity
import com.example.ielts.ui.writing.WritingEditorActivity
import com.example.ielts.ui.writing.WritingListActivity
import com.example.ielts.utils.Constants
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var tvGreetingTime: TextView
    private lateinit var tvGreetingName: TextView
    private lateinit var tvUserInitial: TextView

    // Card Tiến độ Band điểm
    private lateinit var tvCurrentBandValue: TextView
    private lateinit var tvTargetBandTitle: TextView
    private lateinit var tvRemainingBand: TextView
    private lateinit var tvExamDateForecast: TextView
    private lateinit var progressRadialBand: ProgressBar
    private lateinit var progressRoadmapLinear: ProgressBar
    private lateinit var tvProgressPercent: TextView

    // Kế hoạch hôm nay
    private lateinit var tvTodayPlanProgressBadge: TextView
    private lateinit var tvPlanReadingTitle: TextView
    private lateinit var tvPlanReadingSubtitle: TextView
    private lateinit var btnStartReading: AppCompatButton
    private lateinit var tvPlanVocabTitle: TextView
    private lateinit var tvPlanVocabSubtitle: TextView
    private lateinit var btnStartVocab: AppCompatButton
    private lateinit var tvPlanWritingTitle: TextView
    private lateinit var tvPlanWritingSubtitle: TextView
    private lateinit var btnStartWriting: AppCompatButton

    // Thời gian học tuần này
    private lateinit var tvWeeklyTotalTime: TextView
    private lateinit var tvWeeklyGoalNote: TextView
    private lateinit var tvWeeklyCompareNote: TextView

    private var currentRoadmapData: PersonalizedRoadmapData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        setContentView(R.layout.activity_main)

        initViews()
        loadUserData()
        setupListeners()
        StudyReminderScheduler.scheduleDailyReminder(this)

        if (intent.getBooleanExtra("OPEN_ROADMAP_AFTER_PROFILE", false)) {
            intent.removeExtra("OPEN_ROADMAP_AFTER_PROFILE")
            val roadmapIntent = Intent(this, RoadmapActivity::class.java).apply {
                putExtra("EXTRA_FORCE_AI_REFRESH", true)
            }
            startActivity(roadmapIntent)
        }
    }

    private fun initViews() {
        tvGreetingTime = findViewById(R.id.tvGreetingTime)
        tvGreetingName = findViewById(R.id.tvGreetingName)
        tvUserInitial = findViewById(R.id.tvUserInitial)

        tvCurrentBandValue = findViewById(R.id.tvCurrentBandValue)
        tvTargetBandTitle = findViewById(R.id.tvTargetBandTitle)
        tvRemainingBand = findViewById(R.id.tvRemainingBand)
        tvExamDateForecast = findViewById(R.id.tvExamDateForecast)
        progressRadialBand = findViewById(R.id.progressRadialBand)
        progressRoadmapLinear = findViewById(R.id.progressRoadmapLinear)
        tvProgressPercent = findViewById(R.id.tvProgressPercent)

        tvTodayPlanProgressBadge = findViewById(R.id.tvTodayPlanProgressBadge)
        tvPlanReadingTitle = findViewById(R.id.tvPlanReadingTitle)
        tvPlanReadingSubtitle = findViewById(R.id.tvPlanReadingSubtitle)
        btnStartReading = findViewById(R.id.btnStartReading)
        tvPlanVocabTitle = findViewById(R.id.tvPlanVocabTitle)
        tvPlanVocabSubtitle = findViewById(R.id.tvPlanVocabSubtitle)
        btnStartVocab = findViewById(R.id.btnStartVocab)
        tvPlanWritingTitle = findViewById(R.id.tvPlanWritingTitle)
        tvPlanWritingSubtitle = findViewById(R.id.tvPlanWritingSubtitle)
        btnStartWriting = findViewById(R.id.btnStartWriting)

        tvWeeklyTotalTime = findViewById(R.id.tvWeeklyTotalTime)
        tvWeeklyGoalNote = findViewById(R.id.tvWeeklyGoalNote)
        tvWeeklyCompareNote = findViewById(R.id.tvWeeklyCompareNote)
    }

    private fun loadUserData() {
        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val cachedName = prefs.getString(Constants.KEY_USER_NAME, "Học viên") ?: "Học viên"
        val cachedBand = prefs.getFloat(Constants.KEY_TARGET_BAND, 7.5f)
        val cachedExamDate = prefs.getString("exam_date", "06/04/2028") ?: "06/04/2028"
        val cachedStreak = prefs.getInt("streak_days", 0)

        // Hiển thị ngay từ dữ liệu thực tế trên máy
        applyUserToUI(cachedName, cachedBand.toDouble(), cachedExamDate, cachedStreak)

        // Đồng bộ dữ liệu mới nhất từ Backend PostgreSQL qua GET /api/v1/users/me
        val token = prefs.getString(Constants.KEY_AUTH_TOKEN, null)
        if (!token.isNullOrEmpty()) {
            val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getMe(authHeader)
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!.user
                        val realName = user.fullName
                        val realBand = user.targetBand?.toDouble() ?: cachedBand.toDouble()
                        val streak = user.streakDays ?: 0

                        var examDateFormatted = cachedExamDate
                        user.expectedExamDate?.let { isoDate ->
                            try {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val d = inputFormat.parse(isoDate.substring(0, 10))
                                if (d != null) {
                                    examDateFormatted = outputFormat.format(d)
                                }
                            } catch (_: Exception) {}
                        }

                        prefs.edit().apply {
                            putString(Constants.KEY_USER_NAME, realName)
                            putFloat(Constants.KEY_TARGET_BAND, realBand.toFloat())
                            putString("exam_date", examDateFormatted)
                            if (user.expectedExamDate != null) {
                                putString("exam_date_iso", user.expectedExamDate.take(10))
                            }
                            apply()
                        }

                        applyUserToUI(realName, realBand, examDateFormatted, streak)
                        updateStreakBadge()
                    }
                } catch (_: Exception) {
                    // Nếu lỗi kết nối, giữ nguyên dữ liệu thực tế trên máy
                }
            }
        }
    }

    private fun applyUserToUI(fullName: String, targetBand: Double, examDate: String, streak: Int) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        tvGreetingTime.text = when (hour) {
            in 5..10 -> "Chào buổi sáng,"
            in 11..13 -> "Chào buổi trưa,"
            in 14..17 -> "Chào buổi chiều,"
            else -> "Chào buổi tối,"
        }

        val shortName = fullName.split(" ").lastOrNull()?.ifEmpty { fullName } ?: fullName
        tvGreetingName.text = "Chào $shortName! 👋"
        tvUserInitial.text = shortName.firstOrNull()?.uppercase() ?: "U"

        // 1. Dữ liệu thực tế từ RoadmapManager (Tiến độ Band điểm + Lộ trình tổng thể)
        val roadmapData = RoadmapManager.buildRoadmapData(this)
        currentRoadmapData = roadmapData

        val currentBand = roadmapData.currentBand
        val effectiveTarget = roadmapData.targetBand
        val diff = (effectiveTarget - currentBand).coerceAtLeast(0.0)

        tvCurrentBandValue.text = String.format(Locale.US, "%.1f", currentBand)
        tvTargetBandTitle.text = String.format(Locale.US, "MỤC TIÊU %.1f", effectiveTarget)
        tvRemainingBand.text = if (diff > 0) {
            String.format(Locale.US, "Còn +%.1f Band nữa!", diff)
        } else {
            "Đã đạt mục tiêu!"
        }
        val cleanCountdown = roadmapData.countdownLabel.substringBefore(" (").trim()
        tvExamDateForecast.text = "Dự kiến: $examDate ($cleanCountdown)"

        val progressPercent = roadmapData.overallProgressPercent
        progressRadialBand.progress = progressPercent
        progressRoadmapLinear.progress = progressPercent
        tvProgressPercent.text = "$progressPercent% hoàn thành (${roadmapData.completedLessonsCount}/${roadmapData.totalLessonsCount} bài)"

        // 2. Kế hoạch hôm nay (Đồng bộ 3 nhiệm vụ thực tế từ RoadmapManager)
        bindTodayPlan(roadmapData)

        // 3. Thời gian học tuần này (Biểu đồ cột 7 ngày T2 -> CN thực tế)
        bindWeeklyStudyChart()
    }

    private fun bindTodayPlan(roadmapData: PersonalizedRoadmapData) {
        val tasks = roadmapData.dailyTasks
        val completedCount = tasks.count { it.isCompleted }
        tvTodayPlanProgressBadge.text = "$completedCount/${tasks.size} hoàn thành"

        // Card 1: Reading
        tasks.getOrNull(0)?.let { readingTask ->
            val shortExamTitle = readingTask.title
                .replace("Cambridge IELTS ", "Cam ")
                .replace("Listening ", "Lis ")
            tvPlanReadingTitle.text = "Reading"
            tvPlanReadingSubtitle.text = if (readingTask.isCompleted) {
                "✓ $shortExamTitle (Đã xong)"
            } else {
                "$shortExamTitle • 60p"
            }
            btnStartReading.text = if (readingTask.isCompleted) "✓ Đã xong" else "▶ Bắt đầu"
        }

        // Card 2: Từ vựng
        tasks.getOrNull(2)?.let { vocabTask ->
            val topicId = vocabTask.targetExamId
            val topicPool = VocabularyDataSource.resolveScope(VocabularyDataSource.SCOPE_TOPIC, topicId)
            val topicStats = VocabularyProgressManager.summaryStats(this, topicPool)
            val learnedInTopic = topicStats.learning + topicStats.mastered
            val topicName = vocabTask.title.substringAfter(": ").ifEmpty { "Academic" }

            tvPlanVocabTitle.text = "Từ vựng"
            tvPlanVocabSubtitle.text = if (vocabTask.isCompleted) {
                "✓ $topicName ($learnedInTopic/15 từ)"
            } else {
                "$topicName • $learnedInTopic/15 từ"
            }
            btnStartVocab.text = if (vocabTask.isCompleted) "✓ Đã xong" else "▶ Học ngay"
        }

        // Card 3: Writing
        tasks.getOrNull(1)?.let { writingTask ->
            tvPlanWritingTitle.text = "Writing: ${writingTask.title}"
            tvPlanWritingSubtitle.text = writingTask.subtitle
            btnStartWriting.text = if (writingTask.isCompleted) "✓ Đã xong" else "▶ Viết bài"
        }
    }

    private fun bindWeeklyStudyChart() {
        val summary = RoadmapManager.getWeeklyStudySummary(this)
        tvWeeklyTotalTime.text = summary.totalBadgeLabel
        tvWeeklyGoalNote.text = summary.goalNoteLabel
        tvWeeklyCompareNote.text = summary.summaryRightLabel

        val valIds = intArrayOf(
            R.id.tvBarVal1, R.id.tvBarVal2, R.id.tvBarVal3,
            R.id.tvBarVal4, R.id.tvBarVal5, R.id.tvBarVal6, R.id.tvBarVal7
        )
        val barIds = intArrayOf(
            R.id.viewBar1, R.id.viewBar2, R.id.viewBar3,
            R.id.viewBar4, R.id.viewBar5, R.id.viewBar6, R.id.viewBar7
        )
        val dayIds = intArrayOf(
            R.id.tvBarDay1, R.id.tvBarDay2, R.id.tvBarDay3,
            R.id.tvBarDay4, R.id.tvBarDay5, R.id.tvBarDay6, R.id.tvBarDay7
        )

        val maxMinInWeek = summary.days.maxOfOrNull { it.minutes } ?: 0
        val referenceMax = maxOf(summary.dailyGoalMinutes, maxMinInWeek, 30)
        val density = resources.displayMetrics.density

        for (i in 0 until minOf(7, summary.days.size)) {
            val dayItem = summary.days[i]
            val tvVal = findViewById<TextView>(valIds[i])
            val viewBar = findViewById<View>(barIds[i])
            val tvDay = findViewById<TextView>(dayIds[i])

            tvDay?.text = dayItem.dayLabel

            // Nhãn số phút trên đầu cột: hiển thị khi có phút học > 0 hoặc là ngày hôm nay
            if (dayItem.minutes > 0 || dayItem.isToday) {
                tvVal?.visibility = View.VISIBLE
                tvVal?.text = "${dayItem.minutes}p"
            } else {
                tvVal?.visibility = View.GONE
            }

            // Chiều cao cột (dp): 10dp nếu 0 phút, từ 18dp -> 78dp theo tỷ lệ phút học thực tế
            val barHeightDp = if (dayItem.minutes <= 0) {
                10
            } else {
                (18 + ((dayItem.minutes.toFloat() / referenceMax.toFloat()) * 60f).toInt()).coerceIn(18, 78)
            }
            viewBar?.layoutParams = viewBar?.layoutParams?.apply {
                height = (barHeightDp * density).toInt()
            }

            // Tô sáng cột hôm nay hoặc cột có giờ học
            if (dayItem.isToday || dayItem.minutes > 0) {
                viewBar?.setBackgroundResource(R.drawable.bg_bar_active)
            } else {
                viewBar?.setBackgroundResource(R.drawable.bg_bar_inactive)
            }

            if (dayItem.isToday) {
                tvDay?.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
                tvDay?.setTypeface(null, Typeface.BOLD)
            } else {
                tvDay?.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
                tvDay?.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private fun updateStreakBadge() {
        val streakData = StreakManager.getStreakData(this)
        val tvStreak = findViewById<TextView>(R.id.tvStreakCount)
        val btnStreak = findViewById<View>(R.id.btnStreak)
        tvStreak?.text = "${streakData.currentStreak} ngày"

        if (streakData.isActiveToday) {
            btnStreak?.setBackgroundResource(R.drawable.bg_badge_orange)
            tvStreak?.setTextColor(ContextCompat.getColor(this, R.color.badge_orange_text))
        } else {
            btnStreak?.setBackgroundResource(R.drawable.bg_pill_soft_gray)
            tvStreak?.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }
    }

    private fun setupListeners() {
        // Streak button ➔ Mở BottomSheet chi tiết chuỗi
        findViewById<View>(R.id.btnStreak)?.setOnClickListener {
            StreakDetailBottomSheet.newInstance().show(supportFragmentManager, StreakDetailBottomSheet.TAG)
        }

        // Notification button -> Mở Cài đặt Nhắc nhở học tập
        findViewById<View>(R.id.btnNotification)?.setOnClickListener {
            startActivity(Intent(this, StudyReminderActivity::class.java))
        }

        // Card Tiến độ Band điểm -> Mở Lộ trình cá nhân
        findViewById<View>(R.id.cardBandProgress)?.setOnClickListener {
            startActivity(Intent(this, RoadmapActivity::class.java))
        }

        // Top avatar button ➔ Mở menu Tài khoản / Đăng xuất
        findViewById<View>(R.id.btnTopAvatar)?.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Tài khoản người dùng 👤")
                .setMessage("Bạn muốn chỉnh sửa hồ sơ học tập hay đăng xuất tài khoản?")
                .setPositiveButton("Hồ sơ học tập") { _, _ ->
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                .setNegativeButton("Đăng xuất") { _, _ ->
                    val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                    val refreshToken = prefs.getString(Constants.KEY_REFRESH_TOKEN, null)

                    lifecycleScope.launch {
                        if (!refreshToken.isNullOrEmpty()) {
                            try {
                                ApiClient.apiService.logout(LogoutRequest(refreshToken))
                            } catch (_: Exception) {}
                        }

                        prefs.edit().clear().apply()
                        getSharedPreferences("ielts_login_preferences", MODE_PRIVATE)
                            .edit()
                            .putBoolean("IS_LOGGED_IN", false)
                            .apply()

                        Toast.makeText(this@MainActivity, "Đã đăng xuất tài khoản thành công!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@MainActivity, WelcomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }
                .setNeutralButton("Hủy", null)
                .show()
        }

        // Card 1: Reading -> Mở trực tiếp đề Reading hôm nay trong Lộ trình
        val openTodayReading = View.OnClickListener {
            val task = currentRoadmapData?.dailyTasks?.getOrNull(0)
            val testItem = task?.let { t ->
                CambridgeReadingDataSource.getAllTests().firstOrNull { it.id == t.targetExamId }
            }
            if (testItem != null) {
                val intent = Intent(this, ReadingPracticeActivity::class.java).apply {
                    putExtra("TEST_ID", testItem.id)
                    putExtra("TEST_TITLE", testItem.title)
                    putExtra("TEST_DURATION", testItem.durationMinutes)
                    putExtra("TOTAL_QUESTIONS", testItem.totalQuestions)
                }
                startActivity(intent)
            } else {
                startActivity(Intent(this, ReadingListActivity::class.java))
            }
        }
        findViewById<View>(R.id.cardReading)?.setOnClickListener(openTodayReading)
        btnStartReading.setOnClickListener(openTodayReading)

        // Card 2: Từ vựng -> Mở trực tiếp chủ đề Flashcard hôm nay trong Lộ trình
        val openTodayVocab = View.OnClickListener {
            val task = currentRoadmapData?.dailyTasks?.getOrNull(2)
            val topicId = task?.targetExamId ?: VocabularyDataSource.TOPIC_ENVIRONMENT
            FlashcardActivity.start(
                context = this,
                scopeType = VocabularyDataSource.SCOPE_TOPIC,
                scopeId = topicId,
                limit = 15
            )
        }
        findViewById<View>(R.id.cardVocab)?.setOnClickListener(openTodayVocab)
        btnStartVocab.setOnClickListener(openTodayVocab)

        // Card 3: Writing -> Mở trực tiếp đề Writing hôm nay trong Lộ trình
        val openTodayWriting = View.OnClickListener {
            val task = currentRoadmapData?.dailyTasks?.getOrNull(1)
            val writingItem = task?.let { CambridgeWritingDataSource.getTestById(it.targetExamId) }
            if (writingItem != null) {
                val intent = Intent(this, WritingEditorActivity::class.java).apply {
                    putExtra("EXTRA_TEST_ID", writingItem.id)
                    putExtra("EXTRA_TASK_TYPE", writingItem.taskType)
                    putExtra("EXTRA_TITLE", writingItem.title)
                    putExtra("EXTRA_PROMPT", writingItem.prompt)
                    putExtra("EXTRA_MIN_WORDS", writingItem.minWords)
                    putExtra("EXTRA_DURATION_MINUTES", writingItem.durationMinutes)
                    putExtra("EXTRA_SOURCE_MATERIAL", writingItem.sourceMaterial)
                    writingItem.imageResId?.let { putExtra("EXTRA_IMAGE_RES_ID", it) }
                }
                startActivity(intent)
            } else {
                startActivity(Intent(this, WritingListActivity::class.java))
            }
        }
        findViewById<View>(R.id.cardWriting)?.setOnClickListener(openTodayWriting)
        btnStartWriting.setOnClickListener(openTodayWriting)

        // Xem tất cả kế hoạch -> Mở Lộ trình cá nhân
        findViewById<View>(R.id.btnViewAllPlan)?.setOnClickListener {
            startActivity(Intent(this, RoadmapActivity::class.java))
        }

        // Bottom Navigation Tabs
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            // Đang ở Trang chủ
        }

        findViewById<View>(R.id.navRoadmap)?.setOnClickListener {
            startActivity(Intent(this, RoadmapActivity::class.java))
        }

        findViewById<View>(R.id.navPractice)?.setOnClickListener {
            startActivity(Intent(this, PracticeHubActivity::class.java))
        }

        findViewById<View>(R.id.navVocab)?.setOnClickListener {
            startActivity(Intent(this, VocabularyHubActivity::class.java))
        }

        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
        updateStreakBadge()
    }
}
