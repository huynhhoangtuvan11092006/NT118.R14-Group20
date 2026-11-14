package com.example.ielts.ui.roadmap

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ielts.MainActivity
import com.example.ielts.R
import com.example.ielts.data.datasource.CambridgeReadingDataSource
import com.example.ielts.data.datasource.CambridgeWritingDataSource
import com.example.ielts.data.datasource.VocabularyDataSource
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.auth.TargetBandActivity
import com.example.ielts.ui.practice.PracticeHubActivity
import com.example.ielts.ui.reading.ReadingListActivity
import com.example.ielts.ui.reading.ReadingPracticeActivity
import com.example.ielts.ui.vocabulary.FlashcardActivity
import com.example.ielts.ui.vocabulary.VocabularyHubActivity
import com.example.ielts.ui.writing.WritingEditorActivity
import com.example.ielts.ui.writing.WritingListActivity
import com.example.ielts.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class RoadmapActivity : AppCompatActivity() {

    private lateinit var btnBackRoadmap: ImageButton
    private lateinit var btnAiOptimize: FrameLayout
    private lateinit var layoutAiStatusToast: LinearLayout
    private lateinit var tvAiToastMessage: TextView

    // Overview Card
    private lateinit var tvDaysCountdownBadge: TextView
    private lateinit var boxCurrentBand: LinearLayout
    private lateinit var tvCurrentBandValue: TextView
    private lateinit var tvBandGainBadge: TextView
    private lateinit var pbOverallRoadmap: ProgressBar
    private lateinit var tvOverallPercent: TextView
    private lateinit var boxTargetBand: LinearLayout
    private lateinit var tvTargetBandValue: TextView
    private lateinit var tvStudyHoursValue: TextView
    private lateinit var tvStreakDaysValue: TextView

    // AI Advisor Card
    private lateinit var cardAiAdvisor: LinearLayout
    private lateinit var tvAiAdvisorHeader: TextView
    private lateinit var tvAiModelBadge: TextView
    private lateinit var tvAiStrategicAdvice: TextView

    // Today Tasks
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
    private lateinit var cardPhase1: LinearLayout
    private lateinit var layoutPhase1ActiveHeader: LinearLayout
    private lateinit var tvPhase1Tag: TextView
    private lateinit var tvPhase1Count: TextView
    private lateinit var ivPhase1Icon: ImageView
    private lateinit var tvPhase1Title: TextView
    private lateinit var tvPhase1Desc: TextView
    private lateinit var tvPhase1Status: TextView
    private lateinit var layoutPhase1ActiveFooter: LinearLayout
    private lateinit var pbPhase1: ProgressBar
    private lateinit var tvPhase1Percent: TextView
    private lateinit var btnPhase1Continue: LinearLayout
    private lateinit var tvPhase1BtnLabel: TextView

    private lateinit var cardPhase2: LinearLayout
    private lateinit var layoutPhase2ActiveHeader: LinearLayout
    private lateinit var tvPhase2Tag: TextView
    private lateinit var tvPhase2Count: TextView
    private lateinit var ivPhase2Icon: ImageView
    private lateinit var tvPhase2Title: TextView
    private lateinit var tvPhase2Desc: TextView
    private lateinit var tvPhase2Status: TextView
    private lateinit var layoutPhase2ActiveFooter: LinearLayout
    private lateinit var pbPhase2: ProgressBar
    private lateinit var tvPhase2Percent: TextView
    private lateinit var btnPhase2Continue: LinearLayout

    private lateinit var tvPhase3Title: TextView
    private lateinit var tvPhase3Desc: TextView
    private lateinit var tvPhase3Status: TextView

    private var isAiGenerating = false
    private var currentRoadmapData: PersonalizedRoadmapData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        setContentView(R.layout.activity_roadmap)

        initViews()
        setupListeners()
        renderRoadmapUI()

        val forceRefresh = intent.getBooleanExtra("EXTRA_FORCE_AI_REFRESH", false)
        if (forceRefresh) {
            intent.removeExtra("EXTRA_FORCE_AI_REFRESH")
            triggerAiRoadmapOptimization(forceRefresh = true)
        } else {
            syncUserProfileAndCheckAi()
        }
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
        boxCurrentBand = findViewById(R.id.boxCurrentBand)
        tvCurrentBandValue = findViewById(R.id.tvCurrentBandValue)
        tvBandGainBadge = findViewById(R.id.tvBandGainBadge)
        pbOverallRoadmap = findViewById(R.id.pbOverallRoadmap)
        tvOverallPercent = findViewById(R.id.tvOverallPercent)
        boxTargetBand = findViewById(R.id.boxTargetBand)
        tvTargetBandValue = findViewById(R.id.tvTargetBandValue)
        tvStudyHoursValue = findViewById(R.id.tvStudyHoursValue)
        tvStreakDaysValue = findViewById(R.id.tvStreakDaysValue)

        cardAiAdvisor = findViewById(R.id.cardAiAdvisor)
        tvAiAdvisorHeader = findViewById(R.id.tvAiAdvisorHeader)
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

        cardPhase1 = findViewById(R.id.cardPhase1)
        layoutPhase1ActiveHeader = findViewById(R.id.layoutPhase1ActiveHeader)
        tvPhase1Tag = findViewById(R.id.tvPhase1Tag)
        tvPhase1Count = findViewById(R.id.tvPhase1Count)
        ivPhase1Icon = findViewById(R.id.ivPhase1Icon)
        tvPhase1Title = findViewById(R.id.tvPhase1Title)
        tvPhase1Desc = findViewById(R.id.tvPhase1Desc)
        tvPhase1Status = findViewById(R.id.tvPhase1Status)
        layoutPhase1ActiveFooter = findViewById(R.id.layoutPhase1ActiveFooter)
        pbPhase1 = findViewById(R.id.pbPhase1)
        tvPhase1Percent = findViewById(R.id.tvPhase1Percent)
        btnPhase1Continue = findViewById(R.id.btnPhase1Continue)
        tvPhase1BtnLabel = findViewById(R.id.tvPhase1BtnLabel)

        cardPhase2 = findViewById(R.id.cardPhase2)
        layoutPhase2ActiveHeader = findViewById(R.id.layoutPhase2ActiveHeader)
        tvPhase2Tag = findViewById(R.id.tvPhase2Tag)
        tvPhase2Count = findViewById(R.id.tvPhase2Count)
        ivPhase2Icon = findViewById(R.id.ivPhase2Icon)
        tvPhase2Title = findViewById(R.id.tvPhase2Title)
        tvPhase2Desc = findViewById(R.id.tvPhase2Desc)
        tvPhase2Status = findViewById(R.id.tvPhase2Status)
        layoutPhase2ActiveFooter = findViewById(R.id.layoutPhase2ActiveFooter)
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

        // Bấm nút AI Sparkle hoặc thẻ Cố vấn AI để gọi AI tối ưu lộ trình
        val aiClickListener = View.OnClickListener {
            triggerAiRoadmapOptimization(forceRefresh = true)
        }
        btnAiOptimize.setOnClickListener(aiClickListener)
        cardAiAdvisor.setOnClickListener(aiClickListener)

        // Cho phép bấm vào ô Hiện tại để mở Hồ sơ cá nhân (đổi Trình độ A2/B1/B2/C1 & Kỹ năng trọng tâm)
        boxCurrentBand.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Cho phép bấm vào ô Mục tiêu để điều chỉnh Target Band & Ngày thi
        boxTargetBand.setOnClickListener {
            val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
            val intent = Intent(this, TargetBandActivity::class.java).apply {
                putExtra("IS_EDIT_MODE", true)
                putExtra("CURRENT_TARGET_BAND", prefs.getFloat(Constants.KEY_TARGET_BAND, 7.5f))
                putExtra("CURRENT_EXAM_DATE", prefs.getString("exam_date", "2028-04-06"))
            }
            startActivity(intent)
        }

        // Sự kiện bấm vào 3 nhiệm vụ hôm nay -> Mở đúng đề thi thực tế đang được giao!
        val openReading = View.OnClickListener {
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
        rowTaskReading.setOnClickListener(openReading)
        btnTask1Action.setOnClickListener(openReading)

        val openWriting = View.OnClickListener {
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
        rowTaskWriting.setOnClickListener(openWriting)
        btnTask2Action.setOnClickListener(openWriting)

        val openVocab = View.OnClickListener {
            val task = currentRoadmapData?.dailyTasks?.getOrNull(2)
            val topicId = task?.targetExamId ?: VocabularyDataSource.TOPIC_ENVIRONMENT
            FlashcardActivity.start(
                context = this,
                scopeType = VocabularyDataSource.SCOPE_TOPIC,
                scopeId = topicId,
                limit = 15
            )
        }
        rowTaskVocab.setOnClickListener(openVocab)
        btnTask3Action.setOnClickListener(openVocab)

        // Nút "Bắt đầu / Tiếp tục bài học" ở Chặng 1 & Chặng 2
        val openNextLesson = View.OnClickListener {
            val tasks = currentRoadmapData?.dailyTasks.orEmpty()
            val firstUnfinished = tasks.firstOrNull { !it.isCompleted }
            when (firstUnfinished?.skillType) {
                RoadmapSkillType.READING -> openReading.onClick(it)
                RoadmapSkillType.WRITING -> openWriting.onClick(it)
                RoadmapSkillType.VOCAB -> openVocab.onClick(it)
                else -> startActivity(Intent(this, PracticeHubActivity::class.java))
            }
        }
        btnPhase1Continue.setOnClickListener(openNextLesson)
        btnPhase2Continue.setOnClickListener(openNextLesson)

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
        currentRoadmapData = data

        tvDaysCountdownBadge.text = data.countdownLabel
        tvCurrentBandValue.text = String.format(Locale.US, "%.1f", data.currentBand)
        tvTargetBandValue.text = String.format(Locale.US, "%.1f", data.targetBand)
        tvBandGainBadge.text = String.format(Locale.US, "+%.1f Band", data.bandGain)
        pbOverallRoadmap.progress = data.overallProgressPercent
        tvOverallPercent.text = "${data.overallProgressPercent}%"

        tvStudyHoursValue.text = data.studyTimeLabel
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
            bindPhase1(data.phases[0])
            bindPhase2(data.phases[1])
            bindPhase3(data.phases[2])
        }
    }

    private fun bindPhase1(p1: RoadmapPhaseItem) {
        tvPhase1Title.text = p1.title
        tvPhase1Desc.text = p1.description
        if (p1.status == RoadmapPhaseStatus.IN_PROGRESS) {
            cardPhase1.setBackgroundResource(R.drawable.bg_roadmap_phase_active)
            cardPhase1.elevation = 4f
            layoutPhase1ActiveHeader.visibility = View.VISIBLE
            layoutPhase1ActiveFooter.visibility = View.VISIBLE
            ivPhase1Icon.visibility = View.GONE
            tvPhase1Status.visibility = View.GONE

            tvPhase1Tag.text = "ĐANG DIỄN RA • CHẶNG 1"
            tvPhase1Count.text = "${p1.completedLessons}/${p1.totalLessons} bài"
            tvPhase1Title.textSize = 16.5f
            pbPhase1.progress = p1.progressPercent
            tvPhase1Percent.text = "${p1.progressPercent}%"
            tvPhase1BtnLabel.text = if (p1.completedLessons == 0) "Bắt đầu bài học đầu tiên" else "Tiếp tục bài học"
        } else {
            cardPhase1.setBackgroundResource(R.drawable.bg_roadmap_phase_done)
            cardPhase1.elevation = 0f
            layoutPhase1ActiveHeader.visibility = View.GONE
            layoutPhase1ActiveFooter.visibility = View.GONE
            ivPhase1Icon.visibility = View.VISIBLE
            tvPhase1Status.visibility = View.VISIBLE
            tvPhase1Title.textSize = 13.5f
            tvPhase1Status.text = "100%"
        }
    }

    private fun bindPhase2(p2: RoadmapPhaseItem) {
        tvPhase2Title.text = p2.title
        tvPhase2Desc.text = p2.description
        when (p2.status) {
            RoadmapPhaseStatus.IN_PROGRESS -> {
                cardPhase2.setBackgroundResource(R.drawable.bg_roadmap_phase_active)
                cardPhase2.elevation = 4f
                layoutPhase2ActiveHeader.visibility = View.VISIBLE
                layoutPhase2ActiveFooter.visibility = View.VISIBLE
                ivPhase2Icon.visibility = View.GONE
                tvPhase2Status.visibility = View.GONE

                tvPhase2Tag.text = "ĐANG DIỄN RA • CHẶNG 2"
                tvPhase2Count.text = "${p2.completedLessons}/${p2.totalLessons} bài"
                tvPhase2Title.setTextColor(Color.parseColor("#131B2E"))
                tvPhase2Title.textSize = 16.5f
                tvPhase2Desc.setTextColor(Color.parseColor("#434655"))
                pbPhase2.progress = p2.progressPercent
                tvPhase2Percent.text = "${p2.progressPercent}%"
            }
            RoadmapPhaseStatus.COMPLETED -> {
                cardPhase2.setBackgroundResource(R.drawable.bg_roadmap_phase_done)
                cardPhase2.elevation = 0f
                layoutPhase2ActiveHeader.visibility = View.GONE
                layoutPhase2ActiveFooter.visibility = View.GONE
                ivPhase2Icon.visibility = View.VISIBLE
                ivPhase2Icon.setImageResource(R.drawable.ic_check_circle)
                ivPhase2Icon.setColorFilter(Color.parseColor("#10B981"))
                tvPhase2Title.setTextColor(Color.parseColor("#131B2E"))
                tvPhase2Title.textSize = 13.5f
                tvPhase2Desc.setTextColor(Color.parseColor("#434655"))
                tvPhase2Status.visibility = View.VISIBLE
                tvPhase2Status.text = "100%"
                tvPhase2Status.setTextColor(Color.parseColor("#10B981"))
            }
            RoadmapPhaseStatus.LOCKED -> {
                cardPhase2.setBackgroundResource(R.drawable.bg_roadmap_phase_locked)
                cardPhase2.elevation = 0f
                layoutPhase2ActiveHeader.visibility = View.GONE
                layoutPhase2ActiveFooter.visibility = View.GONE
                ivPhase2Icon.visibility = View.VISIBLE
                ivPhase2Icon.setImageResource(R.drawable.ic_lock)
                ivPhase2Icon.setColorFilter(Color.parseColor("#737686"))
                tvPhase2Title.setTextColor(Color.parseColor("#737686"))
                tvPhase2Title.textSize = 13f
                tvPhase2Desc.setTextColor(Color.parseColor("#737686"))
                tvPhase2Status.visibility = View.VISIBLE
                tvPhase2Status.text = "Mở sau"
                tvPhase2Status.setTextColor(Color.parseColor("#737686"))
            }
        }
    }

    private fun bindPhase3(p3: RoadmapPhaseItem) {
        tvPhase3Title.text = p3.title
        tvPhase3Desc.text = p3.description
        tvPhase3Status.text = if (p3.status == RoadmapPhaseStatus.COMPLETED) "100%" else "Mở sau"
    }

    private fun bindTask1(task: RoadmapTaskItem) {
        tvTask1Title.text = task.title
        tvTask1Subtitle.text = task.subtitle
        btnTask1Action.text = task.actionLabel

        if (task.isCompleted) {
            flTask1IconBg.setBackgroundResource(R.drawable.bg_badge_blue)
            ivTask1Icon.setImageResource(R.drawable.ic_check_small)
            ivTask1Icon.setColorFilter(Color.parseColor("#10B981"))
            tvTask1Title.paintFlags = tvTask1Title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            tvTask1Title.setTextColor(Color.parseColor("#434655"))
            btnTask1Action.setBackgroundResource(0)
            btnTask1Action.setTextColor(Color.parseColor("#737686"))
        } else {
            flTask1IconBg.setBackgroundResource(R.drawable.bg_pill_soft_blue)
            ivTask1Icon.setImageResource(R.drawable.ic_menu_book)
            ivTask1Icon.setColorFilter(Color.parseColor("#004AC6"))
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
            btnTask3Action.setTextColor(Color.parseColor("#004AC6"))
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
                            user.targetBand?.let { putFloat(Constants.KEY_TARGET_BAND, it) }
                            user.expectedExamDate?.let { putString("exam_date_iso", it.take(10)) }
                            apply()
                        }
                        renderRoadmapUI()
                    }
                } catch (_: Exception) {
                }
            }

            val currentData = RoadmapManager.buildRoadmapData(this@RoadmapActivity)
            if (!currentData.hasAiCache) {
                triggerAiRoadmapOptimization(forceRefresh = false)
            }
        }
    }

    private fun triggerAiRoadmapOptimization(forceRefresh: Boolean) {
        if (isAiGenerating) return
        isAiGenerating = true

        showAiStatusToast("AI đang phân tích hồ sơ & kho đề học tập...")
        btnAiOptimize.animate().rotationBy(360f).setDuration(900).start()

        lifecycleScope.launch {
            val result = RoadmapManager.generatePersonalizedRoadmapWithAi(this@RoadmapActivity)
            isAiGenerating = false

            result.onSuccess {
                renderRoadmapUI()
                showAiStatusToast("✨ AI đã cập nhật lộ trình cá nhân hóa!")
                delay(2800)
                hideAiStatusToast()
            }.onFailure {
                if (forceRefresh) {
                    showAiStatusToast("Đã tối ưu lộ trình theo hồ sơ mục tiêu hiện tại!")
                    delay(2500)
                }
                hideAiStatusToast()
            }
        }
    }

    private fun showAiStatusToast(message: String) {
        tvAiToastMessage.text = message
        if (layoutAiStatusToast.visibility != View.VISIBLE) {
            layoutAiStatusToast.alpha = 0f
            layoutAiStatusToast.visibility = View.VISIBLE
            layoutAiStatusToast.animate().alpha(1f).setDuration(220).start()
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
