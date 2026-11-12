package com.example.ielts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.LogoutRequest
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.practice.PracticeHubActivity
import com.example.ielts.ui.reading.ReadingListActivity
import com.example.ielts.ui.streak.StreakDetailBottomSheet
import com.example.ielts.utils.Constants

class MainActivity : AppCompatActivity() {

    private lateinit var tvGreetingName: TextView
    private lateinit var tvUserInitial: TextView
    private lateinit var tvTargetBandTitle: TextView
    private lateinit var tvRemainingBand: TextView
    private lateinit var tvExamDateForecast: TextView
    private lateinit var progressRadialBand: ProgressBar
    private lateinit var progressRoadmapLinear: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        loadUserData()
        setupListeners()
        com.example.ielts.ui.reminder.StudyReminderScheduler.scheduleDailyReminder(this)
    }

    private fun initViews() {
        tvGreetingName = findViewById(R.id.tvGreetingName)
        tvUserInitial = findViewById(R.id.tvUserInitial)
        tvTargetBandTitle = findViewById(R.id.tvTargetBandTitle)
        tvRemainingBand = findViewById(R.id.tvRemainingBand)
        tvExamDateForecast = findViewById(R.id.tvExamDateForecast)
        progressRadialBand = findViewById(R.id.progressRadialBand)
        progressRoadmapLinear = findViewById(R.id.progressRoadmapLinear)
    }

    private fun loadUserData() {
        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val cachedName = prefs.getString(Constants.KEY_USER_NAME, "Học viên") ?: "Học viên"
        val cachedBand = prefs.getFloat(Constants.KEY_TARGET_BAND, 7.0f)
        val cachedExamDate = prefs.getString("exam_date", "20/11/2026") ?: "20/11/2026"
        val cachedStreak = prefs.getInt("streak_days", 1)

        // Hiển thị ngay từ bộ nhớ đệm
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
                        val streak = user.streakDays ?: cachedStreak

                        var examDateFormatted = cachedExamDate
                        user.expectedExamDate?.let { isoDate ->
                            try {
                                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                                val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                val d = inputFormat.parse(isoDate.substring(0, 10))
                                if (d != null) {
                                    examDateFormatted = outputFormat.format(d)
                                }
                            } catch (_: Exception) {}
                        }

                        // Lưu lại vào SharedPreferences
                        prefs.edit().apply {
                            putString(Constants.KEY_USER_NAME, realName)
                            putFloat(Constants.KEY_TARGET_BAND, realBand.toFloat())
                            putString("exam_date", examDateFormatted)
                            putInt("streak_days", streak)
                            apply()
                        }

                        applyUserToUI(realName, realBand, examDateFormatted, streak)
                    }
                } catch (_: Exception) {
                    // Nếu lỗi kết nối, giữ nguyên dữ liệu từ cache
                }
            }
        }
    }

    private fun applyUserToUI(fullName: String, targetBand: Double, examDate: String, streak: Int) {
        val shortName = fullName.split(" ").lastOrNull()?.ifEmpty { fullName } ?: fullName
        tvGreetingName.text = "Chào $shortName! 👋"
        tvUserInitial.text = shortName.firstOrNull()?.uppercase() ?: "U"

        // Mục tiêu band
        tvTargetBandTitle.text = "MỤC TIÊU %.1f".format(targetBand)
        val currentBand = 5.5
        val diff = (targetBand - currentBand).coerceAtLeast(0.0)
        tvRemainingBand.text = if (diff > 0) "Còn %.1f Band nữa!".format(diff) else "Đã đạt mục tiêu!"

        // Ngày thi
        tvExamDateForecast.text = "Dự kiến: $examDate"

        // Radial gauge & linear progress
        val progressPercent = ((currentBand / targetBand) * 100).toInt().coerceIn(10, 100)
        progressRadialBand.progress = progressPercent
        progressRoadmapLinear.progress = progressPercent
        findViewById<TextView>(R.id.tvProgressPercent)?.text = "$progressPercent% hoàn thành"

        findViewById<View>(R.id.btnStreak)?.setOnClickListener {
            StreakDetailBottomSheet.newInstance().show(supportFragmentManager, StreakDetailBottomSheet.TAG)
        }
    }

    private fun updateStreakBadge() {
        val streakData = com.example.ielts.data.local.StreakManager.getStreakData(this)
        val tvStreak = findViewById<TextView>(R.id.tvStreakCount)
        val btnStreak = findViewById<View>(R.id.btnStreak)
        tvStreak?.text = "${streakData.currentStreak} ngày"

        if (streakData.isActiveToday) {
            btnStreak?.setBackgroundResource(R.drawable.bg_badge_orange)
            tvStreak?.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.badge_orange_text))
        } else {
            btnStreak?.setBackgroundResource(R.drawable.bg_pill_soft_gray)
            tvStreak?.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_secondary))
        }
    }

    private fun setupListeners() {
        // AI Tutor banner button
        findViewById<View>(R.id.btnAskAi)?.setOnClickListener {
            Toast.makeText(this, "AI Tutor: Đang chuẩn bị chấm bài Writing...", Toast.LENGTH_SHORT).show()
        }

        // Streak button ➔ Mở BottomSheet chi tiết chuỗi
        findViewById<View>(R.id.btnStreak)?.setOnClickListener {
            StreakDetailBottomSheet.newInstance().show(supportFragmentManager, StreakDetailBottomSheet.TAG)
        }

        // Notification button -> Mở Cài đặt Nhắc nhở học tập (WorkManager)
        findViewById<View>(R.id.btnNotification)?.setOnClickListener {
            val intent = Intent(this, com.example.ielts.ui.reminder.StudyReminderActivity::class.java)
            startActivity(intent)
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

                        // Xóa sạch Token và trạng thái đăng nhập
                        prefs.edit().clear().apply()
                        getSharedPreferences("ielts_login_preferences", MODE_PRIVATE).edit().putBoolean("IS_LOGGED_IN", false).apply()

                        Toast.makeText(this@MainActivity, "Đã đăng xuất tài khoản thành công!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@MainActivity, com.example.ielts.ui.auth.WelcomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                }
                .setNeutralButton("Hủy", null)
                .show()
        }

        // Card Reading & Button Start Reading
        val openReadingList = View.OnClickListener {
            startActivity(Intent(this, ReadingListActivity::class.java))
        }
        findViewById<View>(R.id.btnStartReading)?.setOnClickListener(openReadingList)
        findViewById<View>(R.id.cardReading)?.setOnClickListener(openReadingList)

        // Card Từ vựng
        findViewById<View>(R.id.btnStartVocab)?.setOnClickListener {
            Toast.makeText(this, "Bắt đầu học 15 từ vựng mới...", Toast.LENGTH_SHORT).show()
        }

        // Xem tất cả kế hoạch
        findViewById<View>(R.id.btnViewAllPlan)?.setOnClickListener {
            startActivity(Intent(this, PracticeHubActivity::class.java))
        }

        // Speaking Part 2 simulator banner
        findViewById<View>(R.id.cardSpeakingPractice)?.setOnClickListener {
            Toast.makeText(this, "Khởi chạy phòng luyện nói Speaking Part 2 Simulator với AI...", Toast.LENGTH_SHORT).show()
        }

        // Bottom Navigation Tabs
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            // Đang ở Trang chủ
        }

        findViewById<View>(R.id.navRoadmap)?.setOnClickListener {
            Toast.makeText(this, "Tab Lộ trình học chi tiết", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.navPractice)?.setOnClickListener {
            startActivity(Intent(this, PracticeHubActivity::class.java))
        }

        findViewById<View>(R.id.navVocab)?.setOnClickListener {
            startActivity(Intent(this, com.example.ielts.ui.vocabulary.VocabularyHubActivity::class.java))
        }

        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Cập nhật lại dữ liệu mỗi khi quay về Dashboard
        loadUserData()
        updateStreakBadge()
    }
}