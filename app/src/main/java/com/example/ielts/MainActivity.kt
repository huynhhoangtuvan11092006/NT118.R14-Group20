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
import com.example.ielts.ui.auth.ProfileActivity

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
        val prefs = getSharedPreferences("ielts_user_prefs", MODE_PRIVATE)
        val fullName = prefs.getString("user_name", "Nguyễn Văn Quân") ?: "Nguyễn Văn Quân"
        val targetBand = prefs.getFloat("target_band", 7.0f)
        val examDate = prefs.getString("exam_date", "20/11/2026") ?: "20/11/2026"

        // Lấy tên gọi ngắn (từ cuối cùng)
        val shortName = fullName.split(" ").lastOrNull()?.ifEmpty { "Quân" } ?: "Quân"
        tvGreetingName.text = "Chào $shortName! 👋"
        tvUserInitial.text = shortName.firstOrNull()?.uppercase() ?: "Q"

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
    }

    private fun setupListeners() {
        // AI Tutor banner button
        findViewById<View>(R.id.btnAskAi)?.setOnClickListener {
            Toast.makeText(this, "AI Tutor: Đang chuẩn bị chấm bài Writing...", Toast.LENGTH_SHORT).show()
        }

        // Streak button
        findViewById<View>(R.id.btnStreak)?.setOnClickListener {
            Toast.makeText(this, "Chuỗi học liên tục: 5 ngày 🔥", Toast.LENGTH_SHORT).show()
        }

        // Notification button
        findViewById<View>(R.id.btnNotification)?.setOnClickListener {
            Toast.makeText(this, "Không có thông báo mới", Toast.LENGTH_SHORT).show()
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
                    getSharedPreferences("ielts_login_preferences", MODE_PRIVATE).edit().putBoolean("IS_LOGGED_IN", false).apply()
                    getSharedPreferences("ielts_user_prefs", MODE_PRIVATE).edit().putBoolean("is_logged_in", false).apply()
                    val intent = Intent(this, com.example.ielts.ui.auth.WelcomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
                .setNeutralButton("Hủy", null)
                .show()
        }

        // Card Reading
        findViewById<View>(R.id.btnStartReading)?.setOnClickListener {
            Toast.makeText(this, "Bắt đầu luyện Reading Đề 1 (20 phút)...", Toast.LENGTH_SHORT).show()
        }

        // Card Từ vựng
        findViewById<View>(R.id.btnStartVocab)?.setOnClickListener {
            Toast.makeText(this, "Bắt đầu học 15 từ vựng mới...", Toast.LENGTH_SHORT).show()
        }

        // Xem tất cả kế hoạch
        findViewById<View>(R.id.btnViewAllPlan)?.setOnClickListener {
            Toast.makeText(this, "Mở danh sách bài học trong ngày...", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Tab Luyện thi Cambridge 19", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.navVocab)?.setOnClickListener {
            Toast.makeText(this, "Tab Flashcard & Từ vựng IELTS", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Cập nhật lại dữ liệu mỗi khi quay về Dashboard
        loadUserData()
    }
}