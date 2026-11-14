package com.example.ielts.ui.listening

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.datasource.CambridgeListeningTestDetailDataSource
import com.example.ielts.data.local.ListeningStatsManager
import com.example.ielts.data.player.AudioPlayerListener
import com.example.ielts.data.player.ListeningAudioPlayerManager
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.ListeningQuestionDetailDto
import com.example.ielts.data.remote.dto.ListeningSectionDto
import com.example.ielts.data.remote.dto.ListeningTestDetailDto
import com.example.ielts.data.remote.dto.SubmitListeningRequest
import com.example.ielts.ui.listening.adapter.ListeningPaletteAdapter
import com.example.ielts.ui.listening.ListeningResultActivity
import com.example.ielts.data.remote.dto.ListeningResultReviewDto
import com.example.ielts.data.remote.dto.ListeningSubmissionResultDto
import com.google.gson.Gson
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.util.Locale

data class TranscriptLine(
    val startMs: Long,
    val endMs: Long,
    val timeLabel: String,
    val speaker: String,
    val content: String
)

class ListeningPracticeActivity : AppCompatActivity(), AudioPlayerListener {

    private lateinit var audioPlayerManager: ListeningAudioPlayerManager
    private var testDetail: ListeningTestDetailDto? = null
    private var currentTestId: String = "cam18-listening-test1"

    private var currentSectionNumber: Int = 1
    private val answersMap = mutableMapOf<Int, String>()
    private val flaggedSet = mutableSetOf<Int>()

    // Countdown Timer
    private var countDownTimer: CountDownTimer? = null
    private var remainingSeconds: Long = 35 * 60
    private var totalExamDurationSeconds: Long = 35 * 60

    // Top Bar Views
    private lateinit var btnLeaveTest: ImageView
    private lateinit var tvExamHeaderTitle: TextView

    // Section Ribbon Tabs & Timer
    private lateinit var tabPart1: TextView
    private lateinit var tabPart2: TextView
    private lateinit var tabPart3: TextView
    private lateinit var tabPart4: TextView
    private lateinit var tvCountdownTimer: TextView

    // Audio Player Module Views
    private lateinit var tvAudioSectionTitle: TextView
    private lateinit var tvAudioTrackSubtitle: TextView
    private lateinit var seekBarAudio: SeekBar
    private lateinit var tvAudioCurrentTime: TextView
    private lateinit var tvAudioSpeedStatus: TextView
    private lateinit var tvAudioTotalTime: TextView
    private lateinit var btnSpeedToggle: TextView
    private lateinit var btnRewind5: ImageView
    private lateinit var btnPlayPause: FrameLayout
    private lateinit var ivPlayPauseIcon: ImageView
    private lateinit var pbAudioBuffering: ProgressBar
    private lateinit var btnForward5: ImageView
    private lateinit var btnTranscriptToggle: LinearLayout
    private lateinit var layoutTranscriptDrawer: LinearLayout
    private lateinit var scrollTranscript: NestedScrollView
    private lateinit var layoutTranscriptContainer: LinearLayout
    private lateinit var tvTranscriptContent: TextView

    private val currentTranscriptLines = mutableListOf<TranscriptLine>()
    private val transcriptRowViews = mutableListOf<View>()
    private var activeTranscriptIndex: Int = -1

    // Questions Container
    private lateinit var scrollQuestions: NestedScrollView
    private lateinit var tvInstructionHeader: TextView
    private lateinit var tvInstructionBody: TextView
    private lateinit var layoutDynamicQuestions: LinearLayout

    // Bottom Navigation Bar
    private lateinit var btnOpenPalette: LinearLayout
    private lateinit var tvAnsweredCount: TextView
    private lateinit var btnFlagActiveQuestion: ImageView
    private lateinit var btnSubmitExam: View

    private val speedOptions = listOf(1.0f, 1.25f, 1.5f, 0.75f)
    private var speedIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listening_practice)

        initViews()
        initAudioPlayer()
        setupListeners()
        loadTestData()
    }

    private fun initViews() {
        btnLeaveTest = findViewById(R.id.btnLeaveTest)
        tvExamHeaderTitle = findViewById(R.id.tvExamHeaderTitle)

        tabPart1 = findViewById(R.id.tabPart1)
        tabPart2 = findViewById(R.id.tabPart2)
        tabPart3 = findViewById(R.id.tabPart3)
        tabPart4 = findViewById(R.id.tabPart4)
        tvCountdownTimer = findViewById(R.id.tvCountdownTimer)

        tvAudioSectionTitle = findViewById(R.id.tvAudioSectionTitle)
        tvAudioTrackSubtitle = findViewById(R.id.tvAudioTrackSubtitle)
        seekBarAudio = findViewById(R.id.seekBarAudio)
        tvAudioCurrentTime = findViewById(R.id.tvAudioCurrentTime)
        tvAudioSpeedStatus = findViewById(R.id.tvAudioSpeedStatus)
        tvAudioTotalTime = findViewById(R.id.tvAudioTotalTime)
        btnSpeedToggle = findViewById(R.id.btnSpeedToggle)
        btnRewind5 = findViewById(R.id.btnRewind5)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        ivPlayPauseIcon = findViewById(R.id.ivPlayPauseIcon)
        pbAudioBuffering = findViewById(R.id.pbAudioBuffering)
        btnForward5 = findViewById(R.id.btnForward5)
        btnTranscriptToggle = findViewById(R.id.btnTranscriptToggle)
        layoutTranscriptDrawer = findViewById(R.id.layoutTranscriptDrawer)
        scrollTranscript = findViewById(R.id.scrollTranscript)
        layoutTranscriptContainer = findViewById(R.id.layoutTranscriptContainer)
        tvTranscriptContent = findViewById(R.id.tvTranscriptContent)

        scrollQuestions = findViewById(R.id.scrollQuestions)
        tvInstructionHeader = findViewById(R.id.tvInstructionHeader)
        tvInstructionBody = findViewById(R.id.tvInstructionBody)
        layoutDynamicQuestions = findViewById(R.id.layoutDynamicQuestions)

        btnOpenPalette = findViewById(R.id.btnOpenPalette)
        tvAnsweredCount = findViewById(R.id.tvAnsweredCount)
        btnFlagActiveQuestion = findViewById(R.id.btnFlagActiveQuestion)
        btnSubmitExam = findViewById(R.id.btnSubmitExam)
    }

    private fun initAudioPlayer() {
        audioPlayerManager = ListeningAudioPlayerManager(this)
        audioPlayerManager.setListener(this)

        seekBarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var userIsSeeking = false
            var targetSeekMs = 0L

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = audioPlayerManager.getDuration()
                    targetSeekMs = (duration * (progress / 1000f)).toLong()
                    tvAudioCurrentTime.text = formatDuration(targetSeekMs)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = false
                audioPlayerManager.seekTo(targetSeekMs)
            }
        })
    }

    private fun setupListeners() {
        btnLeaveTest.setOnClickListener {
            showLeaveExamConfirmationDialog()
        }

        tabPart1.setOnClickListener { switchSection(1) }
        tabPart2.setOnClickListener { switchSection(2) }
        tabPart3.setOnClickListener { switchSection(3) }
        tabPart4.setOnClickListener { switchSection(4) }

        btnPlayPause.setOnClickListener {
            audioPlayerManager.togglePlayPause()
        }

        btnRewind5.setOnClickListener {
            audioPlayerManager.seekRelative(-5000L)
        }

        btnForward5.setOnClickListener {
            audioPlayerManager.seekRelative(5000L)
        }

        btnSpeedToggle.setOnClickListener {
            speedIndex = (speedIndex + 1) % speedOptions.size
            val newSpeed = speedOptions[speedIndex]
            audioPlayerManager.setPlaybackSpeed(newSpeed)
            btnSpeedToggle.text = "${newSpeed}x  ▾"
            tvAudioSpeedStatus.text = "Đang phát ${newSpeed}x"
        }

        btnTranscriptToggle.setOnClickListener {
            if (layoutTranscriptDrawer.visibility == View.VISIBLE) {
                layoutTranscriptDrawer.visibility = View.GONE
            } else {
                layoutTranscriptDrawer.visibility = View.VISIBLE
                if (activeTranscriptIndex in transcriptRowViews.indices) {
                    scrollTranscript.post {
                        val activeRow = transcriptRowViews[activeTranscriptIndex]
                        val scrollY = (activeRow.top - 20).coerceAtLeast(0)
                        scrollTranscript.smoothScrollTo(0, scrollY)
                    }
                }
            }
        }

        btnOpenPalette.setOnClickListener {
            showQuestionPaletteBottomSheet()
        }

        btnFlagActiveQuestion.setOnClickListener {
            val qNum = (currentSectionNumber - 1) * 10 + 1
            if (flaggedSet.contains(qNum)) {
                flaggedSet.remove(qNum)
                btnFlagActiveQuestion.setImageResource(R.drawable.ic_bookmark_border)
                btnFlagActiveQuestion.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary))
                Toast.makeText(this, "Đã bỏ đánh dấu câu $qNum", Toast.LENGTH_SHORT).show()
            } else {
                flaggedSet.add(qNum)
                btnFlagActiveQuestion.setImageResource(R.drawable.ic_bookmark)
                btnFlagActiveQuestion.setColorFilter(ContextCompat.getColor(this, R.color.tertiary_orange_container))
                Toast.makeText(this, "Đã đánh dấu câu $qNum để xem lại", Toast.LENGTH_SHORT).show()
            }
        }

        btnSubmitExam.setOnClickListener {
            showSubmitExamConfirmationDialog()
        }
    }

    private fun loadTestData() {
        currentTestId = intent.getStringExtra("EXTRA_TEST_ID") ?: "cam18-listening-test1"
        val testId = currentTestId
        val testTitle = intent.getStringExtra("EXTRA_TEST_TITLE") ?: "Cambridge IELTS 18 - Listening Test 1"

        tvExamHeaderTitle.text = testTitle

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getListeningTestDetail(testId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val fallback = CambridgeListeningTestDetailDataSource.getTestDetail(testId, body.title)
                    val enrichedSections = body.sections.map { sec ->
                        if (sec.transcript.isNullOrBlank()) {
                            val fb = fallback.sections.find { it.sectionNumber == sec.sectionNumber }
                            sec.copy(transcript = fb?.transcript ?: "")
                        } else sec
                    }
                    testDetail = body.copy(sections = enrichedSections)
                } else {
                    testDetail = CambridgeListeningTestDetailDataSource.getTestDetail(testId)
                }
            } catch (_: Exception) {
                testDetail = CambridgeListeningTestDetailDataSource.getTestDetail(testId)
            }

            applyLoadedData()
        }
    }

    private fun applyLoadedData() {
        val detail = testDetail ?: return

        tvExamHeaderTitle.text = detail.title
        totalExamDurationSeconds = (detail.durationMinutes * 60).toLong()
        remainingSeconds = totalExamDurationSeconds
        startExamTimer(totalExamDurationSeconds)

        switchSection(1)
    }

    private fun startExamTimer(seconds: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                remainingSeconds = millisUntilFinished / 1000L
                tvCountdownTimer.text = formatDuration(millisUntilFinished)
                if (remainingSeconds < 300) {
                    tvCountdownTimer.setTextColor(ContextCompat.getColor(this@ListeningPracticeActivity, R.color.tertiary_orange_container))
                }
            }

            override fun onFinish() {
                remainingSeconds = 0
                tvCountdownTimer.text = "00:00"
                Toast.makeText(this@ListeningPracticeActivity, "Đã hết thời gian làm bài thi!", Toast.LENGTH_LONG).show()
                submitExam()
            }
        }.start()
    }

    private fun switchSection(sectionNumber: Int) {
        currentSectionNumber = sectionNumber

        // 1. Update Tabs Highlight
        val activeBg = R.drawable.bg_chip_selected_pill_purple
        val inactiveBg = R.drawable.bg_pill_soft_purple
        val activeText = ContextCompat.getColor(this, R.color.white)
        val inactiveText = ContextCompat.getColor(this, R.color.text_secondary)

        tabPart1.setBackgroundResource(if (sectionNumber == 1) activeBg else inactiveBg)
        tabPart1.setTextColor(if (sectionNumber == 1) activeText else inactiveText)

        tabPart2.setBackgroundResource(if (sectionNumber == 2) activeBg else inactiveBg)
        tabPart2.setTextColor(if (sectionNumber == 2) activeText else inactiveText)

        tabPart3.setBackgroundResource(if (sectionNumber == 3) activeBg else inactiveBg)
        tabPart3.setTextColor(if (sectionNumber == 3) activeText else inactiveText)

        tabPart4.setBackgroundResource(if (sectionNumber == 4) activeBg else inactiveBg)
        tabPart4.setTextColor(if (sectionNumber == 4) activeText else inactiveText)

        // 2. Load Section Details
        val section = testDetail?.sections?.find { it.sectionNumber == sectionNumber }
            ?: ListeningSectionDto(sectionNumber, "Section $sectionNumber", "", 420)

        tvAudioSectionTitle.text = section.title
        tvAudioTrackSubtitle.text = "Track 0$sectionNumber • Cambridge Audio"
        val fallbackSection = CambridgeListeningTestDetailDataSource.getTestDetail(currentTestId, testDetail?.title).sections.find { it.sectionNumber == sectionNumber }
        val sectionTranscript = if (!section.transcript.isNullOrBlank()) section.transcript else fallbackSection?.transcript ?: ""
        val parsedLines = parseTranscriptLines(sectionTranscript)
        displayTranscriptLines(parsedLines)

        // 3. Play Section Audio
        if (section.audioUrl.isNotBlank()) {
            audioPlayerManager.playUrl(section.audioUrl, autoPlay = true)
        }

        // 4. Update Instructions Header
        val startQ = (sectionNumber - 1) * 10 + 1
        val endQ = sectionNumber * 10
        tvInstructionHeader.text = "ℹ️ HƯỚNG DẪN LÀM BÀI • QUESTIONS $startQ–$endQ"
        tvInstructionBody.text = when (sectionNumber) {
            1 -> "Complete the notes below. Write NO MORE THAN TWO WORDS AND/OR A NUMBER for each answer."
            2 -> "Choose the correct letter, A, B, or C for questions 11–20."
            3 -> "Choose the correct letter, A, B, or C for questions 21–30."
            else -> "Complete the summary below. Write ONE WORD ONLY for each answer."
        }

        // 5. Render Questions for Section
        renderQuestionsForSection(sectionNumber)

        // Scroll to top
        scrollQuestions.smoothScrollTo(0, 0)
    }

    private fun renderQuestionsForSection(sectionNumber: Int) {
        layoutDynamicQuestions.removeAllViews()

        val allQuestions = testDetail?.questions ?: emptyList()
        val sectionQuestions = allQuestions.filter { it.sectionNumber == sectionNumber }

        val inflater = LayoutInflater.from(this)

        for (question in sectionQuestions) {
            if (question.type == "MULTIPLE_CHOICE" && !question.options.isNullOrEmpty()) {
                val choiceView = inflater.inflate(R.layout.item_listening_question_choice, layoutDynamicQuestions, false)

                val tvBadge = choiceView.findViewById<TextView>(R.id.tvChoiceNumberBadge)
                val tvStem = choiceView.findViewById<TextView>(R.id.tvChoiceQuestionStem)
                val btnBookmark = choiceView.findViewById<ImageView>(R.id.btnBookmarkChoice)
                val layoutOptions = choiceView.findViewById<LinearLayout>(R.id.layoutOptionsContainer)

                tvBadge.text = "${question.position}"
                tvStem.text = question.prompt

                val isFlagged = flaggedSet.contains(question.position)
                btnBookmark.setImageResource(if (isFlagged) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border)
                btnBookmark.setColorFilter(ContextCompat.getColor(this, if (isFlagged) R.color.tertiary_orange_container else R.color.text_secondary))

                btnBookmark.setOnClickListener {
                    if (flaggedSet.contains(question.position)) {
                        flaggedSet.remove(question.position)
                        btnBookmark.setImageResource(R.drawable.ic_bookmark_border)
                        btnBookmark.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary))
                    } else {
                        flaggedSet.add(question.position)
                        btnBookmark.setImageResource(R.drawable.ic_bookmark)
                        btnBookmark.setColorFilter(ContextCompat.getColor(this, R.color.tertiary_orange_container))
                    }
                }

                // Render Option rows
                for (opt in question.options) {
                    val optionView = inflater.inflate(R.layout.item_choice_option_row, layoutOptions, false)
                    val tvOpt = optionView.findViewById<TextView>(R.id.tvOptionText)
                    val ivDot = optionView.findViewById<ImageView>(R.id.ivRadioDot)
                    val ivCheck = optionView.findViewById<ImageView>(R.id.ivSelectedCheck)

                    tvOpt.text = opt

                    val optKey = opt.firstOrNull()?.toString() ?: opt
                    val currentAnswer = answersMap[question.position]
                    val isSelected = currentAnswer != null && (currentAnswer == optKey || currentAnswer == opt)

                    applyOptionSelection(optionView, ivDot, ivCheck, isSelected)

                    optionView.setOnClickListener {
                        answersMap[question.position] = optKey
                        updateAnsweredCounter()
                        // Re-render options for this question
                        for (i in 0 until layoutOptions.childCount) {
                            val child = layoutOptions.getChildAt(i)
                            val cTv = child.findViewById<TextView>(R.id.tvOptionText)?.text?.toString() ?: ""
                            val cKey = cTv.firstOrNull()?.toString() ?: cTv
                            val isChildSelected = (cKey == optKey)
                            applyOptionSelection(child, child.findViewById(R.id.ivRadioDot), child.findViewById(R.id.ivSelectedCheck), isChildSelected)
                        }
                    }

                    layoutOptions.addView(optionView)
                }

                layoutDynamicQuestions.addView(choiceView)
            } else {
                // Form / Note Completion (FILL_IN_BLANK)
                val fillView = inflater.inflate(R.layout.item_listening_question_fill, layoutDynamicQuestions, false)

                val tvBadge = fillView.findViewById<TextView>(R.id.tvQuestionBadge)
                val tvPrompt = fillView.findViewById<TextView>(R.id.tvQuestionPrompt)
                val etAnswer = fillView.findViewById<EditText>(R.id.etFillAnswer)
                val ivStatus = fillView.findViewById<ImageView>(R.id.ivInputStatusIcon)

                tvBadge.text = "[ ${question.position} ]"
                tvPrompt.text = question.prompt

                val existing = answersMap[question.position]
                if (!existing.isNullOrBlank()) {
                    etAnswer.setText(existing)
                    ivStatus.setImageResource(R.drawable.ic_check_circle)
                    ivStatus.setColorFilter(ContextCompat.getColor(this, R.color.primary_blue))
                }

                etAnswer.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        val txt = s?.toString()?.trim() ?: ""
                        if (txt.isNotBlank()) {
                            answersMap[question.position] = txt
                            ivStatus.setImageResource(R.drawable.ic_check_circle)
                            ivStatus.setColorFilter(ContextCompat.getColor(this@ListeningPracticeActivity, R.color.primary_blue))
                        } else {
                            answersMap.remove(question.position)
                            ivStatus.setImageResource(R.drawable.ic_edit)
                            ivStatus.setColorFilter(ContextCompat.getColor(this@ListeningPracticeActivity, R.color.text_secondary))
                        }
                        updateAnsweredCounter()
                    }
                })

                layoutDynamicQuestions.addView(fillView)
            }
        }
    }

    private fun applyOptionSelection(view: View, ivDot: ImageView, ivCheck: ImageView, isSelected: Boolean) {
        if (isSelected) {
            view.setBackgroundResource(R.drawable.bg_choice_selected_blue)
            ivDot.setImageResource(R.drawable.ic_radio_custom_checked)
            ivCheck.visibility = View.VISIBLE
        } else {
            view.setBackgroundResource(R.drawable.bg_choice_unselected_soft)
            ivDot.setImageResource(R.drawable.ic_radio_custom_unchecked)
            ivCheck.visibility = View.GONE
        }
    }

    private fun updateAnsweredCounter() {
        val count = answersMap.size
        tvAnsweredCount.text = "$count / 40 câu"
    }

    private fun showQuestionPaletteBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_listening_palette, null)
        dialog.setContentView(view)

        val tvAnswered = view.findViewById<TextView>(R.id.tvLegendAnswered)
        val tvFlagged = view.findViewById<TextView>(R.id.tvLegendFlagged)
        val tvUnanswered = view.findViewById<TextView>(R.id.tvLegendUnanswered)
        val btnClose = view.findViewById<ImageView>(R.id.btnClosePalette)
        val rvGrid = view.findViewById<RecyclerView>(R.id.rvQuestionPaletteGrid)

        val answeredCount = answersMap.size
        val flaggedCount = flaggedSet.size
        val unansweredCount = (40 - answeredCount).coerceAtLeast(0)

        tvAnswered.text = "Đã làm ($answeredCount)"
        tvFlagged.text = "Đánh dấu ($flaggedCount)"
        tvUnanswered.text = "Chưa làm ($unansweredCount)"

        rvGrid.layoutManager = GridLayoutManager(this, 5)
        rvGrid.adapter = ListeningPaletteAdapter(
            totalQuestions = 40,
            userAnswers = answersMap,
            flaggedQuestions = flaggedSet
        ) { qNum ->
            dialog.dismiss()
            val targetSection = ((qNum - 1) / 10) + 1
            if (targetSection != currentSectionNumber) {
                switchSection(targetSection)
            }
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showSubmitExamConfirmationDialog() {
        val answeredCount = answersMap.size
        AlertDialog.Builder(this)
            .setTitle("Xác nhận nộp bài thi 📝")
            .setMessage("Bạn đã hoàn thành $answeredCount / 40 câu hỏi.\nBạn có chắc chắn muốn nộp bài thi Listening ngay bây giờ?")
            .setPositiveButton("Nộp bài ngay") { _, _ ->
                submitExam()
            }
            .setNegativeButton("Làm tiếp", null)
            .show()
    }

    private fun submitExam() {
        audioPlayerManager.pause()
        countDownTimer?.cancel()

        val timeSpent = (totalExamDurationSeconds - remainingSeconds).toInt().coerceAtLeast(60)
        val testId = testDetail?.id ?: "cam18-listening-test1"

        val defaultAnswers = listOf(
            "DW30 7YZ", "24th April", "dentist", "parking", "Claxby",
            "late", "evening", "supermarket", "pollution", "storage",
            "C", "A", "A", "B", "E",
            "B", "G", "D", "A", "F",
            "A", "B", "A", "C", "B",
            "A", "B", "E", "A", "C",
            "fences", "family", "helicopters", "stress", "sides",
            "breathing", "feet", "employment", "weapons", "tourism"
        )

        val defaultQuotes = listOf(
            "It's DW3Q 7YZ.", "It's the 24th of April, isn't it?", "the main reason I came here was to go to the dentist.",
            "parking was so difficult to find and it costs so much.", "At the bus stop on Claxby Street.",
            "this morning it was late. Only about 10 minutes, but still.", "any time I've been in town in the evening... wait a long time",
            "Mainly just to go to the supermarket.", "because of all the pollution.", "it doesn't have any storage.",
            "people at the back of the room may be a bit squashed", "Other considerations, such as reliability, are crucial in voluntary work and age isn't related to these",
            "training doesn't end when you start working for us. It takes place before, during and after", "What we value is dedication.", "critical that you have enough hours in the day for whatever role we agree is suitable for you",
            "creativity to come up with an imaginative or novel way of fundraising", "useful if you can walk for long periods, sometimes uphill.",
            "good if you know something about nutrition and can give clear instructions.", "helping out at our Story Club for disabled children, especially if you have done some acting.",
            "take in a lot of information and not forget any important steps or details.",
            "the students sitting next to me were talking loudly.", "there is much more variety in the job market than we had realised.",
            "quite hard for us to follow at points.", "school careers advice was delivered far too late",
            "differ on why we would like a career in fashion.", "realistic about the practice",
            "paying too much attention to how she looked", "expecting to become well known right away",
            "reasons why people return items", "fashion designs customers want but can't find",
            "extensive damage to fences in the reserve.", "an entire family unit was selected",
            "employed helicopters to herd the animals", "minimise physiological stress",
            "positioned on their sides to prevent their immense weight from crushing lungs",
            "breathing was monitored constantly throughout transit", "circumference of their feet",
            "created valuable employment opportunities", "sharp decline in poachers and confiscated weapons",
            "eco-tourism flourished, providing a significant boost to GDP"
        )

        val timestamps = listOf(
            121, 154, 173, 192, 211, 273, 297, 308, 332, 345,
            78, 111, 130, 196, 168, 251, 269, 303, 312, 345,
            12, 24, 40, 55, 70, 88, 105, 105, 130, 130,
            18, 32, 48, 65, 80, 98, 112, 130, 146, 162
        )

        var correctCount = 0
        val reviewList = mutableListOf<ListeningResultReviewDto>()
        val questionsFromDetail = testDetail?.questions ?: emptyList()

        for (i in 1..40) {
            val qDetail = questionsFromDetail.find { it.position == i }
            val secNum = when {
                i <= 10 -> 1
                i <= 20 -> 2
                i <= 30 -> 3
                else -> 4
            }
            val userRaw = answersMap[i]?.trim() ?: ""
            val correctAns = defaultAnswers.getOrElse(i - 1) { "A" }
            val isCorrect = when (i) {
                1 -> userRaw.replace(" ", "").equals("DW307YZ", ignoreCase = true) || userRaw.contains("7YZ", ignoreCase = true)
                2 -> userRaw.contains("24", ignoreCase = true)
                14, 15 -> {
                    val pairAnswers = setOf("B", "E")
                    val uAns = userRaw.uppercase().trim().take(1)
                    uAns in pairAnswers
                }
                27, 28 -> {
                    val pairAnswers = setOf("B", "E")
                    val uAns = userRaw.uppercase().trim().take(1)
                    uAns in pairAnswers
                }
                29, 30 -> {
                    val pairAnswers = setOf("A", "C")
                    val uAns = userRaw.uppercase().trim().take(1)
                    uAns in pairAnswers
                }
                else -> {
                    if (userRaw.isBlank()) false
                    else userRaw.equals(correctAns, ignoreCase = true) ||
                         userRaw.contains(correctAns, ignoreCase = true) ||
                         correctAns.contains(userRaw, ignoreCase = true)
                }
            }

            if (isCorrect) correctCount++

            val timeSec = timestamps.getOrElse(i - 1) { 30 }
            val timeStr = "%02d:%02d".format(timeSec / 60, timeSec % 60)

            reviewList.add(
                ListeningResultReviewDto(
                    questionId = qDetail?.id ?: "q_$i",
                    position = i,
                    userAnswer = if (userRaw.isNullOrBlank()) null else userRaw,
                    correctAnswer = correctAns,
                    isCorrect = isCorrect,
                    explanation = "Dẫn chứng đối chiếu trực tiếp từ audio tại thời điểm $timeStr.",
                    prompt = qDetail?.prompt ?: "Câu hỏi số $i",
                    sectionNumber = secNum,
                    evidenceQuote = defaultQuotes.getOrElse(i - 1) { "Dẫn chứng trong đoạn nghe Section $secNum." },
                    timestampSeconds = timeSec,
                    timestampStr = timeStr
                )
            )
        }

        val estimatedBand = calculateIeltsListeningBand(correctCount)
        val resultDto = ListeningSubmissionResultDto(
            testId = testId,
            score = correctCount,
            totalQuestions = 40,
            bandEstimate = estimatedBand,
            bandScore = estimatedBand,
            durationSpent = timeSpent,
            results = reviewList
        )
        val resultJson = Gson().toJson(resultDto)

        // Save local stats
        ListeningStatsManager.recordTestCompletion(this, testId, correctCount, 40, estimatedBand, resultJson, null)

        Toast.makeText(
            this@ListeningPracticeActivity,
            "Đã nộp bài thành công! Đúng $correctCount/40 câu • Band %.1f".format(estimatedBand),
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(this@ListeningPracticeActivity, ListeningResultActivity::class.java).apply {
            putExtra("TEST_ID", testId)
            putExtra("TEST_TITLE", testDetail?.title ?: "Cam 18 - Listening Test 1")
            putExtra("SCORE", correctCount)
            putExtra("TOTAL_QUESTIONS", 40)
            putExtra("BAND_ESTIMATE", estimatedBand)
            putExtra("DURATION_SPENT", timeSpent)
            putExtra("RESULT_JSON", resultJson)
        }
        startActivity(intent)
        finish()

        // Background sync to backend
        val strAnswers = answersMap.mapKeys { it.key.toString() }
        lifecycleScope.launch {
            try {
                ApiClient.apiService.submitListening(
                    testId,
                    SubmitListeningRequest(testId, strAnswers, timeSpent)
                )
            } catch (_: Exception) {}
        }
    }

    private fun calculateIeltsListeningBand(correct: Int): Double {
        return when {
            correct >= 39 -> 9.0
            correct in 37..38 -> 8.5
            correct in 35..36 -> 8.0
            correct in 32..34 -> 7.5
            correct in 30..31 -> 7.0
            correct in 26..29 -> 6.5
            correct in 23..25 -> 6.0
            correct in 18..22 -> 5.5
            correct in 16..17 -> 5.0
            correct in 13..15 -> 4.5
            correct in 10..12 -> 4.0
            correct in 8..9 -> 3.5
            correct in 6..7 -> 3.0
            correct in 4..5 -> 2.5
            else -> 2.0
        }
    }

    private fun showLeaveExamConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Rời khỏi phòng thi? ⚠️")
            .setMessage("Tiến trình làm bài nghe hiện tại sẽ bị hủy và không được lưu lại.")
            .setPositiveButton("Rời phòng thi") { _, _ ->
                audioPlayerManager.pause()
                finish()
            }
            .setNegativeButton("Ở lại làm tiếp", null)
            .show()
    }

    // AudioPlayerListener Callbacks
    override fun onProgress(currentMs: Long, durationMs: Long) {
        if (durationMs > 0) {
            val progress = ((currentMs.toDouble() / durationMs.toDouble()) * 1000).toInt()
            seekBarAudio.progress = progress
            tvAudioCurrentTime.text = formatDuration(currentMs)
            tvAudioTotalTime.text = formatDuration(durationMs)
            syncTranscriptWithAudio(currentMs)
        }
    }

    private fun parseTranscriptLines(rawTranscript: String): List<TranscriptLine> {
        if (rawTranscript.isBlank()) return emptyList()
        val lines = rawTranscript.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        val regex = Regex("""^\[(\d{1,2}):(\d{2})\]\s*(.*)$""")
        val parsed = mutableListOf<TranscriptLine>()

        for (line in lines) {
            val match = regex.find(line)
            if (match != null) {
                val min = match.groupValues[1].toLongOrNull() ?: 0L
                val sec = match.groupValues[2].toLongOrNull() ?: 0L
                val startMs = (min * 60 + sec) * 1000L
                val remainder = match.groupValues[3].trim()
                val colonIdx = remainder.indexOf(':')
                val speaker = if (colonIdx in 1..30) remainder.substring(0, colonIdx).trim() else ""
                val content = if (colonIdx in 1..30) remainder.substring(colonIdx + 1).trim() else remainder
                val timeLabel = "%02d:%02d".format(min, sec)
                parsed.add(TranscriptLine(startMs, startMs + 6000L, timeLabel, speaker, content))
            }
        }

        val adjusted = mutableListOf<TranscriptLine>()
        for (i in parsed.indices) {
            val cur = parsed[i]
            val endMs = if (i < parsed.size - 1) {
                parsed[i + 1].startMs
            } else {
                cur.startMs + 8000L
            }
            adjusted.add(cur.copy(endMs = endMs))
        }
        return adjusted
    }

    private fun displayTranscriptLines(lines: List<TranscriptLine>) {
        layoutTranscriptContainer.removeAllViews()
        transcriptRowViews.clear()
        currentTranscriptLines.clear()
        currentTranscriptLines.addAll(lines)
        activeTranscriptIndex = -1

        if (lines.isEmpty()) {
            tvTranscriptContent.visibility = View.VISIBLE
            tvTranscriptContent.text = "Lời thoại cho phần nghe này đang được cập nhật."
            return
        }
        tvTranscriptContent.visibility = View.GONE

        val inflater = LayoutInflater.from(this)
        for ((index, item) in lines.withIndex()) {
            val row = inflater.inflate(R.layout.item_listening_transcript_row, layoutTranscriptContainer, false)
            val tvBadge = row.findViewById<TextView>(R.id.tvTranscriptTimeBadge)
            val tvText = row.findViewById<TextView>(R.id.tvTranscriptSentenceText)

            tvBadge.text = item.timeLabel

            val fullText = if (item.speaker.isNotBlank()) {
                Html.fromHtml("<b><font color='#2563EB'>${item.speaker}:</font></b> ${item.content}", Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(item.content, Html.FROM_HTML_MODE_LEGACY)
            }
            tvText.text = fullText

            row.setOnClickListener {
                audioPlayerManager.seekTo(item.startMs)
                highlightTranscriptIndex(index, smoothScroll = true)
            }

            layoutTranscriptContainer.addView(row)
            transcriptRowViews.add(row)
        }
    }

    private fun syncTranscriptWithAudio(currentMs: Long) {
        if (currentTranscriptLines.isEmpty() || transcriptRowViews.isEmpty()) return

        val targetIndex = currentTranscriptLines.indexOfFirst { currentMs in it.startMs until it.endMs }
        if (targetIndex != -1 && targetIndex != activeTranscriptIndex) {
            highlightTranscriptIndex(targetIndex, smoothScroll = true)
        }
    }

    private fun highlightTranscriptIndex(targetIndex: Int, smoothScroll: Boolean) {
        if (targetIndex == activeTranscriptIndex || targetIndex !in transcriptRowViews.indices) return

        // Unhighlight previous row
        if (activeTranscriptIndex in transcriptRowViews.indices) {
            val prevRow = transcriptRowViews[activeTranscriptIndex]
            prevRow.setBackgroundResource(R.drawable.bg_transcript_row_normal)
            val prevBadge = prevRow.findViewById<TextView>(R.id.tvTranscriptTimeBadge)
            prevBadge.setBackgroundResource(R.drawable.bg_transcript_badge_normal)
            prevBadge.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            val prevText = prevRow.findViewById<TextView>(R.id.tvTranscriptSentenceText)
            prevText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }

        // Highlight new row
        activeTranscriptIndex = targetIndex
        val activeRow = transcriptRowViews[targetIndex]
        activeRow.setBackgroundResource(R.drawable.bg_transcript_row_active)
        val activeBadge = activeRow.findViewById<TextView>(R.id.tvTranscriptTimeBadge)
        activeBadge.setBackgroundResource(R.drawable.bg_transcript_badge_active)
        activeBadge.setTextColor(ContextCompat.getColor(this, R.color.white))
        val activeText = activeRow.findViewById<TextView>(R.id.tvTranscriptSentenceText)
        activeText.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))

        // Smooth scroll to keep active sentence centered in transcript drawer
        if (smoothScroll && layoutTranscriptDrawer.visibility == View.VISIBLE) {
            scrollTranscript.post {
                val scrollY = (activeRow.top - 20).coerceAtLeast(0)
                scrollTranscript.smoothScrollTo(0, scrollY)
            }
        }
    }

    override fun onStateChanged(isPlaying: Boolean, isBuffering: Boolean) {
        if (isBuffering) {
            pbAudioBuffering.visibility = View.VISIBLE
            ivPlayPauseIcon.visibility = View.GONE
        } else {
            pbAudioBuffering.visibility = View.GONE
            ivPlayPauseIcon.visibility = View.VISIBLE
            ivPlayPauseIcon.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow)
        }
    }

    override fun onPlaybackCompleted() {
        ivPlayPauseIcon.setImageResource(R.drawable.ic_play_arrow)
        if (currentSectionNumber < 4) {
            Toast.makeText(this, "Kết thúc Section $currentSectionNumber. Tự động chuyển Section ${currentSectionNumber + 1}", Toast.LENGTH_SHORT).show()
            switchSection(currentSectionNumber + 1)
        }
    }

    override fun onError(message: String) {
        pbAudioBuffering.visibility = View.GONE
        ivPlayPauseIcon.visibility = View.VISIBLE
        ivPlayPauseIcon.setImageResource(R.drawable.ic_play_arrow)
    }

    private fun formatDuration(millis: Long): String {
        val totalSecs = (millis / 1000).toInt()
        val minutes = totalSecs / 60
        val seconds = totalSecs % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showLeaveExamConfirmationDialog()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            audioPlayerManager.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isFinishing) {
            audioPlayerManager.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        audioPlayerManager.release()
    }
}
