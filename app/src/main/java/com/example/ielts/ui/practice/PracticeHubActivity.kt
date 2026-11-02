package com.example.ielts.ui.practice

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.MainActivity
import com.example.ielts.R
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.reading.ReadingListActivity

class PracticeHubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_hub)

        setupListeners()
    }

    private fun setupListeners() {
        // 1. Back button
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
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
            Toast.makeText(this, "Kho đề thi Listening Cambridge 18-19 sắp ra mắt!", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.cardWritingSkill)?.setOnClickListener {
            Toast.makeText(this, "Luyện viết Task 1 & Task 2 với AI chấm điểm tức thì!", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.cardSpeakingSkill)?.setOnClickListener {
            Toast.makeText(this, "Phòng luyện nói Speaking Part 2 với AI Simulator!", Toast.LENGTH_SHORT).show()
        }

        // 5. Weak Question Types
        findViewById<View>(R.id.btnViewAllWeakTypes)?.setOnClickListener {
            Toast.makeText(this, "Phân tích chi tiết 14 dạng câu hỏi cần cải thiện", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.cardWeakTFNG)?.setOnClickListener {
            val intent = Intent(this, ReadingListActivity::class.java)
            intent.putExtra("FILTER_TYPE", "TFNG")
            startActivity(intent)
        }

        findViewById<View>(R.id.cardWeakMatching)?.setOnClickListener {
            val intent = Intent(this, ReadingListActivity::class.java)
            intent.putExtra("FILTER_TYPE", "MATCHING")
            startActivity(intent)
        }

        // 6. Recent Practice Items
        findViewById<View>(R.id.itemRecentReading)?.setOnClickListener {
            startActivity(Intent(this, ReadingListActivity::class.java))
        }

        findViewById<View>(R.id.itemRecentWriting)?.setOnClickListener {
            Toast.makeText(this, "Xem lại bài viết Task 2 gần nhất (Band 6.5)", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.itemRecentListening)?.setOnClickListener {
            Toast.makeText(this, "Luyện lại Section 3 Cambridge 18 Listening", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Kho Flashcard & Từ vựng IELTS", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
