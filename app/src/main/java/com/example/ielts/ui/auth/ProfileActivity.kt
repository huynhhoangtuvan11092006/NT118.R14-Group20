package com.example.ielts.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.ielts.MainActivity
import com.example.ielts.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var edtProfileName: EditText
    private lateinit var edtProfilePhone: EditText
    private lateinit var tvAiPlanSummary: TextView

    // Trình độ (Single-choice)
    private var selectedLevel = "B2"
    private lateinit var cardLevelA2: LinearLayout
    private lateinit var cardLevelB1: LinearLayout
    private lateinit var cardLevelB2: LinearLayout
    private lateinit var cardLevelC1: LinearLayout

    private lateinit var tvTitleA2: TextView
    private lateinit var tvTitleB1: TextView
    private lateinit var tvTitleB2: TextView
    private lateinit var tvTitleC1: TextView

    private lateinit var tvBadgeA2: TextView
    private lateinit var tvBadgeB1: TextView
    private lateinit var tvBadgeB2: TextView
    private lateinit var tvBadgeC1: TextView

    private lateinit var ivCheckA2: ImageView
    private lateinit var ivCheckB1: ImageView
    private lateinit var ivCheckB2: ImageView
    private lateinit var ivCheckC1: ImageView

    // Kỹ năng trọng tâm (Multi-choice)
    private val selectedSkills = mutableSetOf("Writing", "Speaking")
    private lateinit var chipListening: LinearLayout
    private lateinit var chipReading: LinearLayout
    private lateinit var chipWriting: LinearLayout
    private lateinit var chipSpeaking: LinearLayout

    private lateinit var ivIconListening: ImageView
    private lateinit var ivIconReading: ImageView
    private lateinit var ivIconWriting: ImageView
    private lateinit var ivIconSpeaking: ImageView

    private lateinit var tvTitleListening: TextView
    private lateinit var tvTitleReading: TextView
    private lateinit var tvTitleWriting: TextView
    private lateinit var tvTitleSpeaking: TextView

    private lateinit var ivCheckListening: ImageView
    private lateinit var ivCheckReading: ImageView
    private lateinit var ivCheckWriting: ImageView
    private lateinit var ivCheckSpeaking: ImageView

    // Mục tiêu học tập (Single-choice)
    private var selectedGoal = "Du học & Học bổng"
    private lateinit var cardGoalStudy: LinearLayout
    private lateinit var cardGoalWork: LinearLayout
    private lateinit var cardGoalUni: LinearLayout
    private lateinit var cardGoalMigrate: LinearLayout

    private lateinit var tvTitleGoalStudy: TextView
    private lateinit var tvTitleGoalWork: TextView
    private lateinit var tvTitleGoalUni: TextView
    private lateinit var tvTitleGoalMigrate: TextView

    private lateinit var ivCheckGoalStudy: ImageView
    private lateinit var ivCheckGoalWork: ImageView
    private lateinit var ivCheckGoalUni: ImageView
    private lateinit var ivCheckGoalMigrate: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        edtProfileName = findViewById(R.id.edtProfileName)
        edtProfilePhone = findViewById(R.id.edtProfilePhone)
        tvAiPlanSummary = findViewById(R.id.tvAiPlanSummary)

        initViews()
        setupAiPlanSummary()
        setupListeners()
    }

    private fun initViews() {
        // Level
        cardLevelA2 = findViewById(R.id.cardLevelA2)
        cardLevelB1 = findViewById(R.id.cardLevelB1)
        cardLevelB2 = findViewById(R.id.cardLevelB2)
        cardLevelC1 = findViewById(R.id.cardLevelC1)

        tvTitleA2 = findViewById(R.id.tvTitleA2)
        tvTitleB1 = findViewById(R.id.tvTitleB1)
        tvTitleB2 = findViewById(R.id.tvTitleB2)
        tvTitleC1 = findViewById(R.id.tvTitleC1)

        tvBadgeA2 = findViewById(R.id.tvBadgeA2)
        tvBadgeB1 = findViewById(R.id.tvBadgeB1)
        tvBadgeB2 = findViewById(R.id.tvBadgeB2)
        tvBadgeC1 = findViewById(R.id.tvBadgeC1)

        ivCheckA2 = findViewById(R.id.ivCheckA2)
        ivCheckB1 = findViewById(R.id.ivCheckB1)
        ivCheckB2 = findViewById(R.id.ivCheckB2)
        ivCheckC1 = findViewById(R.id.ivCheckC1)

        // Skills
        chipListening = findViewById(R.id.chipListening)
        chipReading = findViewById(R.id.chipReading)
        chipWriting = findViewById(R.id.chipWriting)
        chipSpeaking = findViewById(R.id.chipSpeaking)

        ivIconListening = findViewById(R.id.ivIconListening)
        ivIconReading = findViewById(R.id.ivIconReading)
        ivIconWriting = findViewById(R.id.ivIconWriting)
        ivIconSpeaking = findViewById(R.id.ivIconSpeaking)

        tvTitleListening = findViewById(R.id.tvTitleListening)
        tvTitleReading = findViewById(R.id.tvTitleReading)
        tvTitleWriting = findViewById(R.id.tvTitleWriting)
        tvTitleSpeaking = findViewById(R.id.tvTitleSpeaking)

        ivCheckListening = findViewById(R.id.ivCheckListening)
        ivCheckReading = findViewById(R.id.ivCheckReading)
        ivCheckWriting = findViewById(R.id.ivCheckWriting)
        ivCheckSpeaking = findViewById(R.id.ivCheckSpeaking)

        // Goals
        cardGoalStudy = findViewById(R.id.cardGoalStudy)
        cardGoalWork = findViewById(R.id.cardGoalWork)
        cardGoalUni = findViewById(R.id.cardGoalUni)
        cardGoalMigrate = findViewById(R.id.cardGoalMigrate)

        tvTitleGoalStudy = findViewById(R.id.tvTitleGoalStudy)
        tvTitleGoalWork = findViewById(R.id.tvTitleGoalWork)
        tvTitleGoalUni = findViewById(R.id.tvTitleGoalUni)
        tvTitleGoalMigrate = findViewById(R.id.tvTitleGoalMigrate)

        ivCheckGoalStudy = findViewById(R.id.ivCheckGoalStudy)
        ivCheckGoalWork = findViewById(R.id.ivCheckGoalWork)
        ivCheckGoalUni = findViewById(R.id.ivCheckGoalUni)
        ivCheckGoalMigrate = findViewById(R.id.ivCheckGoalMigrate)
    }

    private fun setupAiPlanSummary() {
        val prefs = getSharedPreferences("ielts_user_prefs", MODE_PRIVATE)
        val band = intent.getDoubleExtra("TARGET_BAND", prefs.getFloat("target_band", 7.0f).toDouble())
        val studyTime = intent.getStringExtra("STUDY_TIME") ?: prefs.getString("study_time", "30 phút/ngày")
        val examDate = intent.getStringExtra("EXAM_DATE") ?: prefs.getString("exam_date", "20/11/2026")

        tvAiPlanSummary.text = "Mục tiêu Band %.1f • %s • Dự kiến thi %s".format(band, studyTime, examDate)
    }

    private fun setupListeners() {
        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnChangeAvatar).setOnClickListener {
            Toast.makeText(this, "Tính năng tải ảnh đại diện từ thư viện", Toast.LENGTH_SHORT).show()
        }

        // Level Click listeners
        cardLevelA2.setOnClickListener { selectLevel("A2") }
        cardLevelB1.setOnClickListener { selectLevel("B1") }
        cardLevelB2.setOnClickListener { selectLevel("B2") }
        cardLevelC1.setOnClickListener { selectLevel("C1") }

        // Skill Toggle listeners
        chipListening.setOnClickListener { toggleSkill("Listening") }
        chipReading.setOnClickListener { toggleSkill("Reading") }
        chipWriting.setOnClickListener { toggleSkill("Writing") }
        chipSpeaking.setOnClickListener { toggleSkill("Speaking") }

        // Goal Click listeners
        cardGoalStudy.setOnClickListener { selectGoal("Du học & Học bổng") }
        cardGoalWork.setOnClickListener { selectGoal("Đi làm & Thăng tiến") }
        cardGoalUni.setOnClickListener { selectGoal("Xét tuyển Đại học") }
        cardGoalMigrate.setOnClickListener { selectGoal("Định cư nước ngoài") }

        // Nút CTA: Bắt đầu hành trình học ngay ➔ Dashboard
        findViewById<AppCompatButton>(R.id.btnStartLearning).setOnClickListener {
            val name = edtProfileName.text.toString().trim().ifEmpty { "Nguyễn Văn Quân" }
            val phone = edtProfilePhone.text.toString().trim()

            // Lưu hồ sơ vào SharedPreferences
            getSharedPreferences("ielts_user_prefs", MODE_PRIVATE).edit().apply {
                putString("user_name", name)
                putString("user_phone", phone)
                putString("current_level", selectedLevel)
                putStringSet("focus_skills", selectedSkills)
                putString("study_goal", selectedGoal)
                putBoolean("is_logged_in", true)
                apply()
            }
            getSharedPreferences("ielts_login_preferences", MODE_PRIVATE).edit().putBoolean("IS_LOGGED_IN", true).apply()

            Toast.makeText(
                this,
                "Chào mừng $name đến với lộ trình IELTS cá nhân hóa!",
                Toast.LENGTH_SHORT
            ).show()

            // Chuyển thẳng sang Dashboard (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun selectLevel(level: String) {
        selectedLevel = level
        val primaryColor = ContextCompat.getColor(this, R.color.primary_blue)
        val textPrimaryColor = ContextCompat.getColor(this, R.color.text_primary)
        val textSecondaryColor = ContextCompat.getColor(this, R.color.text_secondary)
        val whiteColor = ContextCompat.getColor(this, R.color.white)

        // Reset all 4 levels
        listOf(cardLevelA2, cardLevelB1, cardLevelB2, cardLevelC1).forEach {
            it.setBackgroundResource(R.drawable.bg_card_level_unselected)
        }
        listOf(tvTitleA2, tvTitleB1, tvTitleB2, tvTitleC1).forEach {
            it.setTextColor(textPrimaryColor)
        }
        listOf(tvBadgeA2, tvBadgeB1, tvBadgeB2, tvBadgeC1).forEach {
            it.setBackgroundResource(R.drawable.bg_circle_button)
            it.setTextColor(textSecondaryColor)
        }
        listOf(ivCheckA2, ivCheckB1, ivCheckB2, ivCheckC1).forEach {
            it.setImageResource(R.drawable.ic_radio_custom_unchecked)
            it.imageTintList = null
        }

        // Apply active level
        when (level) {
            "A2" -> {
                cardLevelA2.setBackgroundResource(R.drawable.bg_card_level_selected)
                tvTitleA2.setTextColor(primaryColor)
                tvBadgeA2.setBackgroundResource(R.drawable.bg_avatar_nq)
                tvBadgeA2.setTextColor(whiteColor)
                ivCheckA2.setImageResource(R.drawable.ic_check_circle)
                ivCheckA2.setColorFilter(primaryColor)
            }
            "B1" -> {
                cardLevelB1.setBackgroundResource(R.drawable.bg_card_level_selected)
                tvTitleB1.setTextColor(primaryColor)
                tvBadgeB1.setBackgroundResource(R.drawable.bg_avatar_nq)
                tvBadgeB1.setTextColor(whiteColor)
                ivCheckB1.setImageResource(R.drawable.ic_check_circle)
                ivCheckB1.setColorFilter(primaryColor)
            }
            "B2" -> {
                cardLevelB2.setBackgroundResource(R.drawable.bg_card_level_selected)
                tvTitleB2.setTextColor(primaryColor)
                tvBadgeB2.setBackgroundResource(R.drawable.bg_avatar_nq)
                tvBadgeB2.setTextColor(whiteColor)
                ivCheckB2.setImageResource(R.drawable.ic_check_circle)
                ivCheckB2.setColorFilter(primaryColor)
            }
            "C1" -> {
                cardLevelC1.setBackgroundResource(R.drawable.bg_card_level_selected)
                tvTitleC1.setTextColor(primaryColor)
                tvBadgeC1.setBackgroundResource(R.drawable.bg_avatar_nq)
                tvBadgeC1.setTextColor(whiteColor)
                ivCheckC1.setImageResource(R.drawable.ic_check_circle)
                ivCheckC1.setColorFilter(primaryColor)
            }
        }
    }

    private fun toggleSkill(skill: String) {
        if (selectedSkills.contains(skill)) {
            if (selectedSkills.size > 1) { // Giữ tối thiểu 1 kỹ năng
                selectedSkills.remove(skill)
            } else {
                Toast.makeText(this, "Hãy chọn ít nhất 1 kỹ năng trọng tâm", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            selectedSkills.add(skill)
        }
        updateSkillViews()
    }

    private fun updateSkillViews() {
        updateSingleSkillView(
            selectedSkills.contains("Listening"),
            chipListening, ivIconListening, tvTitleListening, ivCheckListening
        )
        updateSingleSkillView(
            selectedSkills.contains("Reading"),
            chipReading, ivIconReading, tvTitleReading, ivCheckReading
        )
        updateSingleSkillView(
            selectedSkills.contains("Writing"),
            chipWriting, ivIconWriting, tvTitleWriting, ivCheckWriting
        )
        updateSingleSkillView(
            selectedSkills.contains("Speaking"),
            chipSpeaking, ivIconSpeaking, tvTitleSpeaking, ivCheckSpeaking
        )
    }

    private fun updateSingleSkillView(
        isSelected: Boolean,
        container: LinearLayout,
        icon: ImageView,
        title: TextView,
        check: ImageView
    ) {
        val primaryColor = ContextCompat.getColor(this, R.color.primary_blue)
        val textPrimaryColor = ContextCompat.getColor(this, R.color.text_primary)
        val textSecondaryColor = ContextCompat.getColor(this, R.color.text_secondary)
        val whiteColor = ContextCompat.getColor(this, R.color.white)

        if (isSelected) {
            container.setBackgroundResource(R.drawable.bg_skill_selected)
            container.elevation = 4f
            icon.setColorFilter(whiteColor)
            title.setTextColor(whiteColor)
            check.setImageResource(R.drawable.ic_check_small)
            check.setColorFilter(whiteColor)
        } else {
            container.setBackgroundResource(R.drawable.bg_skill_unselected)
            container.elevation = 0f
            icon.setColorFilter(textSecondaryColor)
            title.setTextColor(textPrimaryColor)
            check.setImageResource(R.drawable.ic_add)
            check.setColorFilter(textSecondaryColor)
        }
    }

    private fun selectGoal(goal: String) {
        selectedGoal = goal
        val primaryColor = ContextCompat.getColor(this, R.color.primary_blue)
        val textPrimaryColor = ContextCompat.getColor(this, R.color.text_primary)

        listOf(cardGoalStudy, cardGoalWork, cardGoalUni, cardGoalMigrate).forEach {
            it.setBackgroundResource(R.drawable.bg_card_level_unselected)
        }
        listOf(tvTitleGoalStudy, tvTitleGoalWork, tvTitleGoalUni, tvTitleGoalMigrate).forEach {
            it.setTextColor(textPrimaryColor)
        }
        listOf(ivCheckGoalStudy, ivCheckGoalWork, ivCheckGoalUni, ivCheckGoalMigrate).forEach {
            it.setImageResource(R.drawable.ic_radio_custom_unchecked)
            it.imageTintList = null
        }

        when (goal) {
            "Du học & Học bổng" -> {
                cardGoalStudy.setBackgroundResource(R.drawable.bg_card_level_selected)
                tvTitleGoalStudy.setTextColor(primaryColor)
                ivCheckGoalStudy.setImageResource(R.drawable.ic_check_circle)
                ivCheckGoalStudy.setColorFilter(primaryColor)
            }
            "Đi làm & Thăng tiến" -> {
                cardGoalWork.setBackgroundResource(R.drawable.bg_card_level_selected)
                tvTitleGoalWork.setTextColor(primaryColor)
                ivCheckGoalWork.setImageResource(R.drawable.ic_check_circle)
                ivCheckGoalWork.setColorFilter(primaryColor)
            }
            "Xét tuyển Đại học" -> {
                cardGoalUni.setBackgroundResource(R.drawable.bg_card_level_selected)
                tvTitleGoalUni.setTextColor(primaryColor)
                ivCheckGoalUni.setImageResource(R.drawable.ic_check_circle)
                ivCheckGoalUni.setColorFilter(primaryColor)
            }
            "Định cư nước ngoài" -> {
                cardGoalMigrate.setBackgroundResource(R.drawable.bg_card_level_selected)
                tvTitleGoalMigrate.setTextColor(primaryColor)
                ivCheckGoalMigrate.setImageResource(R.drawable.ic_check_circle)
                ivCheckGoalMigrate.setColorFilter(primaryColor)
            }
        }
    }
}
