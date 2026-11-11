package com.example.ielts.ui.practice

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.MainActivity
import com.example.ielts.R
import com.example.ielts.data.datasource.CambridgeReadingDataSource
import com.example.ielts.data.datasource.CambridgeListeningDataSource
import com.example.ielts.data.local.ReadingStatsManager
import com.example.ielts.data.local.ListeningStatsManager
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.listening.ListeningListActivity
import com.example.ielts.ui.listening.ListeningResultActivity
import com.example.ielts.ui.reading.ReadingListActivity
import com.example.ielts.ui.reading.ReadingResultActivity
import java.util.Locale

class PracticeHubActivity : AppCompatActivity() {

    private var tvReadingSkillCount: TextView? = null
    private var tvReadingProgressPercent: TextView? = null
    private var pbReadingProgress: ProgressBar? = null

    private var tvRecentReadingTitle: TextView? = null
    private var tvRecentReadingSubtitle: TextView? = null
    private var tvRecentReadingBand: TextView? = null

    private var tvListeningSkillCount: TextView? = null
    private var tvListeningProgressPercent: TextView? = null
    private var pbListeningProgress: ProgressBar? = null

    private var tvRecentListeningTitle: TextView? = null
    private var tvRecentListeningSubtitle: TextView? = null
    private var tvRecentListeningBand: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_hub)

        initViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadReadingData()
        loadListeningData()
    }

    private fun initViews() {
        tvReadingSkillCount = findViewById(R.id.tvReadingSkillCount)
        tvReadingProgressPercent = findViewById(R.id.tvReadingProgressPercent)
        pbReadingProgress = findViewById(R.id.pbReadingProgress)

        tvRecentReadingTitle = findViewById(R.id.tvRecentReadingTitle)
        tvRecentReadingSubtitle = findViewById(R.id.tvRecentReadingSubtitle)
        tvRecentReadingBand = findViewById(R.id.tvRecentReadingBand)

        tvListeningSkillCount = findViewById(R.id.tvListeningSkillCount)
        tvListeningProgressPercent = findViewById(R.id.tvListeningProgressPercent)
        pbListeningProgress = findViewById(R.id.pbListeningProgress)

        tvRecentListeningTitle = findViewById(R.id.tvRecentListeningTitle)
        tvRecentListeningSubtitle = findViewById(R.id.tvRecentListeningSubtitle)
        tvRecentListeningBand = findViewById(R.id.tvRecentListeningBand)
    }

    private fun loadReadingData() {
        val totalTests = CambridgeReadingDataSource.getAllTests().size
        tvReadingSkillCount?.text = "$totalTests đề luyện đọc"

        val records = ReadingStatsManager.getRecords(this)

        // Tính % tiến độ thực tế 100%: Số đề thực tế đã làm / Tổng số đề
        val completedTestsCount = records.size
        val progressPercent = if (totalTests > 0) {
            ((completedTestsCount.toDouble() / totalTests.toDouble()) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
        tvReadingProgressPercent?.text = "$progressPercent%"
        pbReadingProgress?.progress = progressPercent

        // Cập nhật bài làm gần đây phần reading
        val latestRecord = ReadingStatsManager.getLatestRecord(this)
        if (latestRecord != null) {
            val testItem = CambridgeReadingDataSource.getAllTests().find { it.id == latestRecord.testId }
            tvRecentReadingTitle?.text = testItem?.title ?: "Cam Reading Test"

            val diffMs = System.currentTimeMillis() - latestRecord.completedAtMillis
            val diffMin = diffMs / (60 * 1000)
            val timeText = when {
                diffMin < 2 -> "Vừa xong"
                diffMin < 60 -> "$diffMin phút trước"
                diffMin < 1440 -> "${diffMin / 60} giờ trước"
                else -> "${diffMin / 1440} ngày trước"
            }
            tvRecentReadingSubtitle?.text = "$timeText • ${latestRecord.score}/${latestRecord.totalQuestions} câu"
            tvRecentReadingBand?.text = String.format(Locale.US, "Band %.1f", latestRecord.bandEstimate)
        } else {
            tvRecentReadingTitle?.text = "Cam 18 - Reading Test 1"
            tvRecentReadingSubtitle?.text = "Hôm qua lúc 15:30 • 34/40 câu"
            tvRecentReadingBand?.text = "Band 7.5"
        }
    }

    private fun loadListeningData() {
        val totalTests = CambridgeListeningDataSource.getAllTests().size
        tvListeningSkillCount?.text = "${totalTests} đề nghe audio"

        val records = ListeningStatsManager.getRecords(this)
        val completedTestsCount = records.size
        val progressPercent = if (totalTests > 0) {
            ((completedTestsCount.toDouble() / totalTests.toDouble()) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
        tvListeningProgressPercent?.text = "${progressPercent}%"
        pbListeningProgress?.progress = progressPercent

        val latestRecord = ListeningStatsManager.getLatestRecord(this)
        if (latestRecord != null) {
            val allTests = CambridgeListeningDataSource.getAllTests()
            val testItem = allTests.find { it.id == latestRecord.testId }
                ?: if (latestRecord.testId.contains("18")) allTests.find { it.id == "cam18-listening-test1" } else allTests.firstOrNull()
            tvRecentListeningTitle?.text = testItem?.title ?: "Cam Listening Test"

            val diffMs = System.currentTimeMillis() - latestRecord.completedAtMillis
            val diffMin = diffMs / (60 * 1000)
            val timeText = when {
                diffMin < 2 -> "Vừa xong"
                diffMin < 60 -> "${diffMin} phút trước"
                diffMin < 1440 -> "${diffMin / 60} giờ trước"
                else -> "${diffMin / 1440} ngày trước"
            }
            tvRecentListeningSubtitle?.text = "${timeText} • ${latestRecord.score}/${latestRecord.totalQuestions} câu"
            tvRecentListeningBand?.text = String.format(Locale.US, "Band %.1f", latestRecord.bandEstimate)
        } else {
            tvRecentListeningTitle?.text = "Cam 18 - Listening Test 1"
            tvRecentListeningSubtitle?.text = "Chưa làm bài • 0/40 câu"
            tvRecentListeningBand?.text = "Band --"
        }
    }

    private fun setupListeners() {
        // 1. Back button
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            if (isTaskRoot) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            finish()
        }

        // 2. Mock Test Hero Banner
        val onMockTestClick = View.OnClickListener {
            Toast.makeText(this, "Bắt đầu thi thử Full Mock Test 4 kỹ năng (180 phút)...", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.bannerMockTest)?.setOnClickListener(onMockTestClick)
        findViewById<View>(R.id.btnStartMockTest)?.setOnClickListener(onMockTestClick)

        // 4. Four Skill Cards
        findViewById<View>(R.id.cardReadingSkill)?.setOnClickListener {
            val intent = Intent(this, ReadingListActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.cardListeningSkill)?.setOnClickListener {
            val intent = Intent(this, ListeningListActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.cardWritingSkill)?.setOnClickListener {
            val intent = Intent(this, com.example.ielts.ui.writing.WritingListActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.cardSpeakingSkill)?.setOnClickListener {
            Toast.makeText(this, "Phòng luyện nói Speaking Part 2 với AI Simulator!", Toast.LENGTH_SHORT).show()
        }

        // 6. Recent Practice Items
        findViewById<View>(R.id.itemRecentReading)?.setOnClickListener {
            val latest = ReadingStatsManager.getLatestRecord(this)
            if (latest != null && !latest.resultJson.isNullOrEmpty()) {
                val testItem = CambridgeReadingDataSource.getAllTests().find { it.id == latest.testId }
                val intent = Intent(this, ReadingResultActivity::class.java).apply {
                    putExtra("TEST_ID", latest.testId)
                    putExtra("TEST_TITLE", testItem?.title ?: "Cambridge Reading Test")
                    putExtra("SCORE", latest.score)
                    putExtra("TOTAL_QUESTIONS", latest.totalQuestions)
                    putExtra("BAND_ESTIMATE", latest.bandEstimate)
                    putExtra("DURATION_SPENT", 2400)
                    putExtra("RESULT_JSON", latest.resultJson)
                    putExtra("PASSAGES_JSON", latest.passagesJson)
                }
                startActivity(intent)
            } else {
                startActivity(Intent(this, ReadingListActivity::class.java))
            }
        }

        findViewById<View>(R.id.itemRecentWriting)?.setOnClickListener {
            val intent = Intent(this, com.example.ielts.ui.writing.WritingListActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.itemRecentListening)?.setOnClickListener {
            val latest = ListeningStatsManager.getLatestRecord(this)
            if (latest != null && !latest.resultJson.isNullOrEmpty()) {
                val testItem = CambridgeListeningDataSource.getAllTests().find { it.id == latest.testId }
                    ?: CambridgeListeningDataSource.getAllTests().firstOrNull()
                val intent = Intent(this, ListeningResultActivity::class.java).apply {
                    putExtra("TEST_ID", latest.testId)
                    putExtra("TEST_TITLE", testItem?.title ?: "Cam 18 - Listening Test 1")
                    putExtra("SCORE", latest.score)
                    putExtra("TOTAL_QUESTIONS", latest.totalQuestions)
                    putExtra("BAND_ESTIMATE", latest.bandEstimate)
                    putExtra("DURATION_SPENT", 1935)
                    putExtra("RESULT_JSON", latest.resultJson)
                }
                startActivity(intent)
            } else {
                val intent = Intent(this, ListeningListActivity::class.java)
                startActivity(intent)
            }
        }

        // 7. Bottom Navigation
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.navRoadmap)?.setOnClickListener {
            Toast.makeText(this, "Lộ trình học cá nhân hóa", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.navPractice)?.setOnClickListener {
            // Already on Practice Hub
        }

        findViewById<View>(R.id.navVocab)?.setOnClickListener {
            startActivity(Intent(this, com.example.ielts.ui.vocabulary.VocabularyHubActivity::class.java))
        }

        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
