package com.example.ielts.ui.roadmap

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ielts.MainActivity
import com.example.ielts.R
import com.example.ielts.data.local.StreakManager
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.auth.TargetBandActivity
import com.example.ielts.ui.practice.PracticeHubActivity
import com.example.ielts.ui.reading.ReadingListActivity
import com.example.ielts.ui.vocabulary.VocabularyHubActivity
import com.example.ielts.ui.writing.WritingListActivity
import com.example.ielts.utils.Constants
import kotlinx.coroutines.launch
import java.util.Locale

class RoadmapActivity : AppCompatActivity() {

    private lateinit var btnBackRoadmap: ImageButton
    private lateinit var btnAiOptimize: FrameLayout
    private lateinit var layoutAiStatusToast: LinearLayout
    private lateinit var tvAiToastMessage: TextView

    // Overview Card
    private lateinit var tvDaysCountdownBadge: TextView
    private lateinit var tvCurrentBandValue: TextView
    private lateinit var tvBandGainBadge: TextView
    private lateinit var pbOverallRoadmap: ProgressBar
    private lateinit var tvOverallPercent: TextView
    private lateinit var tvTargetBandValue: TextView
    private lateinit var boxTargetBand: LinearLayout
    private lateinit var tvStudyHoursValue: TextView
    private lateinit var tvStreakDaysValue: TextView

    // AI Advisor Card
    private lateinit var cardAiAdvisor: LinearLayout
    private lateinit var tvAiModelBadge: TextView
    private lateinit var tvAiStrategicAdvice: TextView

    // Today's Tasks
    private lateinit var tvTodayCompletedRatio: TextView
    private lateinit var rowTaskReading: LinearLayout
    private lateinit var flTask1IconBg: FrameLayout
    private lateinit var ivTask1Icon: ImageView
    private lateinit var tvTask1Title: TextView
    private lateinit var tvTask1Subtitle: TextView
    private lateinit var btnTask1Action: TextView

    private lateinit var rowTaskWriting: LinearLayout
    private lateinit var flTask2IconBg: FrameLayout
    private lateinit var ivTask2Icon: ImageView
    private lateinit var tvTask2Title: TextView
    private lateinit var tvTask2Subtitle: TextView
    private lateinit var btnTask2Action: TextView

    private lateinit var rowTaskVocab: LinearLayout
    private lateinit var flTask3IconBg: FrameLayout
    private lateinit var ivTask3Icon: ImageView
    private lateinit var tvTask3Title: TextView
    private lateinit var tvTask3Subtitle: TextView
    private lateinit var btnTask3Action: TextView

    // Milestone Phases
    private lateinit var tvPhase1Title: TextView
    private lateinit var tvPhase1Desc: TextView
    private lateinit var tvPhase1Status: TextView

    private lateinit var tvPhase2Tag: TextView
    private lateinit var tvPhase2Count: TextView
    private lateinit var tvPhase2Title: TextView
    private lateinit var tvPhase2Desc: TextView
    private lateinit var pbPhase2: ProgressBar
    private lateinit var tvPhase2Percent: TextView
    private lateinit var btnPhase2Continue: LinearLayout

    private lateinit var tvPhase3Title: TextView
    private lateinit var tvPhase3Desc: TextView
    private lateinit var tvPhase3Status: TextView

    private var isAiGenerating = false
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roadmap)

        initViews()
        setupListeners()
        renderRoadmapUI()
        syncUserProfileAndCheckAi()
    }

    override fun onResume() {
        super.onResume()
        renderRoadmapUI()
    }

    private fun initViews() {
        btnBackRoadmap = findViewById(R.id.btnBackRoadmap)
        btnAiOptimize = findViewById(R.id.btnAiOptimize)
        layoutAiStatusToast = findViewById(R.id.layoutAiStatusToast)
        tvAiToastMessage = findViewById(R.id.tvAiToastMessage)

        tvDaysCountdownBadge = findViewById(R.id.tvDaysCountdownBadge)
        tvCurrentBandValue = findViewById(R.id.tvCurrentBandValue)
        tvBandGainBadge = findViewById(R.id.tvBandGainBadge)
        pbOverallRoadmap = findViewById(R.id.pbOverallRoadmap)
        tvOverallPercent = findViewById(R.id.tvOverallPercent)
        tvTargetBandValue = findViewById(R.id.tvTargetBandValue)
        boxTargetBand = findViewById(R.id.boxTargetBand)
        tvStudyHoursValue = findViewById(R.id.tvStudyHoursValue)
        tvStreakDaysValue = findViewById(R.id.tvStreakDaysValue)

        cardAiAdvisor = findViewById(R.id.cardAiAdvisor)
        tvAiModelBadge = findViewById(R.id.tvAiModelBadge)
        tvAiStrategicAdvice = findViewById(R.id.tvAiStrategicAdvice)

        tvTodayCompletedRatio = findViewById(R.id.tvTodayCompletedRatio)

        rowTaskReading = findViewById(R.id.rowTaskReading)
        flTask1IconBg = findViewById(R.id.flTask1IconBg)
        ivTask1Icon = findViewById(R.id.ivTask1Icon)
        tvTask1Title = findViewById(R.id.tvTask1Title)
        tvTask1Subtitle = findViewById(R.id.tvTask1Subtitle)
        btnTask1Action = findViewById(R.id.btnTask1Action)

        rowTaskWriting = findViewById(R.id.rowTaskWriting)
        flTask2IconBg = findViewById(R.id.flTask2IconBg)
        ivTask2Icon = findViewById(R.id.ivTask2Icon)
        tvTask2Title = findViewById(R.id.tvTask2Title)
        tvTask2Subtitle = findViewById(R.id.tvTask2Subtitle)
        btnTask2Action = findViewById(R.id.btnTask2Action)

        rowTaskVocab = findViewById(R.id.rowTaskVocab)
        flTask3IconBg = findViewById(R.id.flTask3IconBg)
        ivTask3Icon = findViewById(R.id.ivTask3Icon)
        tvTask3Title = findViewById(R.id.tvTask3Title)
        tvTask3Subtitle = findViewById(R.id.tvTask3Subtitle)
        btnTask3Action = findViewById(R.id.btnTask3Action)

        tvPhase1Title = findViewById(R.id.tvPhase1Title)
        tvPhase1Desc = findViewById(R.id.tvPhase1Desc)
        tvPhase1Status = findViewById(R.id.tvPhase1Status)

        tvPhase2Tag = findViewById(R.id.tvPhase2Tag)
        tvPhase2Count = findViewById(R.id.tvPhase2Count)
        tvPhase2Title = findViewById(R.id.tvPhase2Title)
        tvPhase2Desc = findViewById(R.id.tvPhase2Desc)
        pbPhase2 = findViewById(R.id.pbPhase2)
        tvPhase2Percent = findViewById(R.id.tvPhase2Percent)
        btnPhase2Continue = findViewById(R.id.btnPhase2Continue)

        tvPhase3Title = findViewById(R.id.tvPhase3Title)
        tvPhase3Desc = findViewById(R.id.tvPhase3Desc)
        tvPhase3Status = findViewById(R.id.tvPhase3Status)
    }

    private fun setupListeners() {
        btnBackRoadmap.setOnClickListener {
            finish()
        }

        // Bấm nút AI Sparkle hoặc thẻ Cố vấn AI để gọi Gemini AI tối ưu lộ trình
        val aiClickListener = View.OnClickListener {
            triggerAiRoadmapOptimization(forceRefresh = true)
        }
        btnAiOptimize.setOnClickListener(aiClickListener)
        cardAiAdvisor.setOnClickListener(aiClickListener)

        // Cho phép bấm vào ô Mục tiêu để điều chỉnh Target Band
        boxTargetBand.setOnClickListener {
            val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
            val intent = Intent(this, TargetBandActivity::class.java).apply {
                putExtra("IS_EDIT_MODE", true)
                putExtra("CURRENT_TARGET_BAND", prefs.getFloat("target_band", 8.5f))
                putExtra("CURRENT_EXAM_DATE", prefs.getString("exam_date", "2028-04-06"))
            }
            startActivity(intent)
        }

        // Sự kiện bấm vào 3 nhiệm vụ hôm nay
        val openReading = View.OnClickListener {
            startActivity(Intent(this, ReadingListActivity::class.java))
        }
        rowTaskReading.setOnClickListener(openReading)
        btnTask1Action.setOnClickListener(openReading)

        val openWriting = View.OnClickListener {
            startActivity(Intent(this, WritingListActivity::class.java))
        }
        rowTaskWriting.setOnClickListener(openWriting)
        btnTask2Action.setOnClickListener(openWriting)

        val openVocab = View.OnClickListener {
            startActivity(Intent(this, VocabularyHubActivity::class.java))
        }
        rowTaskVocab.setOnClickListener(openVocab)
        btnTask3Action.setOnClickListener(openVocab)

        // Nút "Tiếp tục bài học" ở Chặng 2
        btnPhase2Continue.setOnClickListener {
            startActivity(Intent(this, PracticeHubActivity::class.java))
        }

        // Bottom Navigation
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.navRoadmap)?.setOnClickListener {
            // Đang ở màn hình Lộ trình
        }

        findViewById<View>(R.id.navPractice)?.setOnClickListener {
            startActivity(Intent(this, PracticeHubActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navVocab)?.setOnClickListener {
            startActivity(Intent(this, VocabularyHubActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun renderRoadmapUI() {
        val data = RoadmapManager.buildRoadmapData(this)

        tvDaysCountdownBadge.text = data.countdownLabel
        tvCurrentBandValue.text = String.format(Locale.US, "%.1f", data.currentBand)
        tvTargetBandValue.text = String.format(Locale.US, "%.1f", data.targetBand)
        tvBandGainBadge.text = String.format(Locale.US, "+%.1f Band", data.bandGain)
        pbOverallRoadmap.progress = data.overallProgressPercent
        tvOverallPercent.text = "${data.overallProgressPercent}%"

        tvStudyHoursValue.text = "${data.studyHoursTotal}h"
        tvStreakDaysValue.text = "${data.streakDays} ngày"

        tvAiStrategicAdvice.text = data.aiStrategicAdvice
        tvAiModelBadge.text = data.aiModelBadge

        // Today tasks
        val completedCount = data.dailyTasks.count { it.isCompleted }
        tvTodayCompletedRatio.text = "$completedCount/${data.dailyTasks.size} hoàn thành"

        if (data.dailyTasks.size >= 3) {
            bindTask1(data.dailyTasks[0])
            bindTask2(data.dailyTasks[1])
            bindTask3(data.dailyTasks[2])
        }

        // Phases
        if (data.phases.size >= 3) {
            val p1 = data.phases[0]
            tvPhase1Title.text = p1.title
            tvPhase1Desc.text = p1.description
            tvPhase1Status.text = "${p1.progressPercent}%"

            val p2 = data.phases[1]
            tvPhase2Tag.text = "ĐANG DIỄN RA • CHẶNG ${p2.phaseNumber}"
            tvPhase2Count.text = "${p2.completedLessons}/${p2.totalLessons} bài"
            tvPhase2Title.text = p2.title
            tvPhase2Desc.text = p2.description
            pbPhase2.progress = p2.progressPercent
            tvPhase2Percent.text = "${p2.progressPercent}%"

            val p3 = data.phases[2]
            tvPhase3Title.text = p3.title
            tvPhase3Desc.text = p3.description
            tvPhase3Status.text = "Mở sau"
        }
    }

    private fun bindTask1(task: RoadmapTaskItem) {
        tvTask1Title.text = task.title
        tvTask1Subtitle.text = task.subtitle
        btnTask1Action.text = task.actionLabel

        if (task.isCompleted) {
            tvTask1Title.paintFlags = tvTask1Title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            tvTask1Title.setTextColor(Color.parseColor("#434655"))
            btnTask1Action.setBackgroundResource(0)
            btnTask1Action.setTextColor(Color.parseColor("#737686"))
        } else {
            tvTask1Title.paintFlags = tvTask1Title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            tvTask1Title.setTextColor(Color.parseColor("#131B2E"))
            btnTask1Action.setBackgroundResource(R.drawable.bg_roadmap_btn_primary)
            btnTask1Action.setTextColor(Color.WHITE)
        }
    }

    private fun bindTask2(task: RoadmapTaskItem) {
        tvTask2Title.text = task.title
        tvTask2Subtitle.text = task.subtitle
        btnTask2Action.text = task.actionLabel

        if (task.isCompleted) {
            tvTask2Title.paintFlags = tvTask2Title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            tvTask2Title.setTextColor(Color.parseColor("#434655"))
            btnTask2Action.setBackgroundResource(0)
            btnTask2Action.setTextColor(Color.parseColor("#737686"))
        } else {
            tvTask2Title.paintFlags = tvTask2Title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            tvTask2Title.setTextColor(Color.parseColor("#131B2E"))
            btnTask2Action.setBackgroundResource(R.drawable.bg_roadmap_btn_primary)
            btnTask2Action.setTextColor(Color.WHITE)
        }
    }

    private fun bindTask3(task: RoadmapTaskItem) {
        tvTask3Title.text = task.title
        tvTask3Subtitle.text = task.subtitle
        btnTask3Action.text = task.actionLabel

        if (task.isCompleted) {
            tvTask3Title.paintFlags = tvTask3Title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            tvTask3Title.setTextColor(Color.parseColor("#434655"))
            btnTask3Action.setBackgroundResource(0)
            btnTask3Action.setTextColor(Color.parseColor("#737686"))
        } else {
            tvTask3Title.paintFlags = tvTask3Title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            tvTask3Title.setTextColor(Color.parseColor("#131B2E"))
            btnTask3Action.setBackgroundResource(R.drawable.bg_roadmap_btn_tonal)
            btnTask3Action.setTextColor(Color.parseColor("#131B2E"))
        }
    }

    private fun syncUserProfileAndCheckAi() {
        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val token = prefs.getString(Constants.KEY_AUTH_TOKEN, null)

        lifecycleScope.launch {
            if (!token.isNullOrEmpty()) {
                try {
                    val response = ApiClient.apiService.getMe("Bearer $token")
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!.user
                        prefs.edit().apply {
                            user.targetBand?.let { putFloat("target_band", it.toFloat()) }
                            user.currentBand?.let { putFloat("current_band", it.toFloat()) }
                            user.expectedExamDate?.let { putString("exam_date", it) }
                            apply()
                        }
                        val cloudStreak = user.dailyStudyMinutes ?: user.streakDays ?: 0
                        if (cloudStreak > 0) {
                            StreakManager.syncFromCloud(this@RoadmapActivity, cloudStreak)
                        }
                        renderRoadmapUI()
                    }
                } catch (_: Exception) {
                }
            }

            // Nếu chưa có bản phân tích AI cho mục tiêu hiện tại, tự động gọi Gemini AI đề xuất
            val currentRoadmap = RoadmapManager.buildRoadmapData(this@RoadmapActivity)
            if (!currentRoadmap.hasAiCache) {
                triggerAiRoadmapOptimization(forceRefresh = false)
            }
        }
    }

    private fun triggerAiRoadmapOptimization(forceRefresh: Boolean) {
        if (isAiGenerating) return
        isAiGenerating = true

        showAiStatusToast("Gemini AI đang phân tích hồ sơ & thiết kế lộ trình...")

        lifecycleScope.launch {
            val result = RoadmapManager.generatePersonalizedRoadmapWithAi(this@RoadmapActivity)
            isAiGenerating = false

            result.onSuccess {
                renderRoadmapUI()
                showAiStatusToast("✨ Gemini AI đã tối ưu lộ trình cá nhân hóa cho bạn!", autoHideMs = 3200L)
            }.onFailure { err ->
                if (forceRefresh) {
                    Toast.makeText(
                        this@RoadmapActivity,
                        "Đã cập nhật lộ trình theo tiến độ làm bài mới nhất!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                hideAiStatusToast()
            }
        }
    }

    private fun showAiStatusToast(message: String, autoHideMs: Long = 0L) {
        mainHandler.removeCallbacksAndMessages(null)
        tvAiToastMessage.text = message
        layoutAiStatusToast.visibility = View.VISIBLE
        layoutAiStatusToast.alpha = 0f
        layoutAiStatusToast.animate().alpha(1f).setDuration(220).start()

        if (autoHideMs > 0L) {
            mainHandler.postDelayed({
                hideAiStatusToast()
            }, autoHideMs)
        }
    }

    private fun hideAiStatusToast() {
        layoutAiStatusToast.animate()
            .alpha(0f)
            .setDuration(220)
            .withEndAction {
                layoutAiStatusToast.visibility = View.GONE
            }
            .start()
    }
}
