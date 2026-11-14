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

    private fun bindBlankEditText(et: EditText, qPosition: Int) {
        val existing = answersMap[qPosition]
        if (!existing.isNullOrBlank()) {
            et.setText(existing)
        }

        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val txt = s?.toString()?.trim() ?: ""
                if (txt.isNotBlank()) {
                    answersMap[qPosition] = txt
                } else {
                    answersMap.remove(qPosition)
                }
                updateAnsweredCounter()
            }
        })
    }

    private fun renderCam18Test2Section1() {
        val inflater = LayoutInflater.from(this)

        // 1. Group 1: Working at Milo's Restaurants (Questions 1–5)
        val notesView = inflater.inflate(R.layout.layout_cam18_t2_p1_notes, layoutDynamicQuestions, false)
        val btnZoomNotes1 = notesView.findViewById<TextView>(R.id.btnZoomNotes1)
        btnZoomNotes1.setOnClickListener {
            ListeningImageDialogHelper.show(this, R.drawable.img_cam18_test2_part1_notes, "Working at Milo's Restaurants")
        }

        bindBlankEditText(notesView.findViewById(R.id.etNoteBlank1), 1)
        bindBlankEditText(notesView.findViewById(R.id.etNoteBlank2), 2)
        bindBlankEditText(notesView.findViewById(R.id.etNoteBlank3), 3)
        bindBlankEditText(notesView.findViewById(R.id.etNoteBlank4), 4)
        bindBlankEditText(notesView.findViewById(R.id.etNoteBlank5), 5)

        layoutDynamicQuestions.addView(notesView)

        // 2. Group 2: Table (Questions 6–10)
        val tableView = inflater.inflate(R.layout.layout_cam18_t2_p1_table, layoutDynamicQuestions, false)
        val btnZoomTable1 = tableView.findViewById<TextView>(R.id.btnZoomTable1)
        btnZoomTable1.setOnClickListener {
            ListeningImageDialogHelper.show(this, R.drawable.img_cam18_test2_part1_table, "Bảng tuyển dụng (Jobs Table)")
        }

        bindBlankEditText(tableView.findViewById(R.id.etTableBlank6), 6)
        bindBlankEditText(tableView.findViewById(R.id.etTableBlank7), 7)
        bindBlankEditText(tableView.findViewById(R.id.etTableBlank8), 8)
        bindBlankEditText(tableView.findViewById(R.id.etTableBlank9), 9)
        bindBlankEditText(tableView.findViewById(R.id.etTableBlank10), 10)

        layoutDynamicQuestions.addView(tableView)
    }

    private fun renderCam18Test2Section4() {
        val inflater = LayoutInflater.from(this)

        // Group: Pockets (Questions 31–40)
        val pocketsView = inflater.inflate(R.layout.layout_cam18_t2_p4_pockets, layoutDynamicQuestions, false)
        val btnZoomPockets4 = pocketsView.findViewById<TextView>(R.id.btnZoomPockets4)
        btnZoomPockets4.setOnClickListener {
            ListeningImageDialogHelper.show(this, R.drawable.img_cam18_test2_part4_pockets, "Pockets")
        }

        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank31), 31)
        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank32), 32)
        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank33), 33)
        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank34), 34)
        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank35), 35)
        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank36), 36)
        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank37), 37)
        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank38), 38)
        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank39), 39)
        bindBlankEditText(pocketsView.findViewById(R.id.etPocketBlank40), 40)

        layoutDynamicQuestions.addView(pocketsView)
    }

    private fun renderMultiSelectQuestionCard(
        q1Pos: Int,
        q2Pos: Int,
        badgeText: String,
        instructionText: String,
        stemText: String,
        options: List<String>
    ) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.item_listening_question_multiselect, layoutDynamicQuestions, false)

        val tvBadge = view.findViewById<TextView>(R.id.tvMultiBadge)
        val tvInstruction = view.findViewById<TextView>(R.id.tvMultiInstruction)
        val tvStem = view.findViewById<TextView>(R.id.tvMultiStem)
        val layoutOptions = view.findViewById<LinearLayout>(R.id.layoutMultiOptionsContainer)

        tvBadge.text = badgeText
        tvStem.text = stemText

        fun updateInstruction() {
            var selectedCount = 0
            if (!answersMap[q1Pos].isNullOrBlank()) selectedCount++
            if (!answersMap[q2Pos].isNullOrBlank()) selectedCount++
            tvInstruction.text = "$instructionText ($selectedCount/2)"
        }
        updateInstruction()

        // Inflate option rows
        for (opt in options) {
            val optionView = inflater.inflate(R.layout.item_choice_option_row, layoutOptions, false)
            val tvOpt = optionView.findViewById<TextView>(R.id.tvOptionText)
            val ivDot = optionView.findViewById<ImageView>(R.id.ivRadioDot)
            val ivCheck = optionView.findViewById<ImageView>(R.id.ivSelectedCheck)

            tvOpt.text = opt
            val optKey = opt.firstOrNull()?.toString() ?: opt

            val isSelected = (answersMap[q1Pos] == optKey || answersMap[q2Pos] == optKey)
            applyOptionSelection(optionView, ivDot, ivCheck, isSelected)

            optionView.setOnClickListener {
                val currentSelected = mutableListOf<String>()
                answersMap[q1Pos]?.let { currentSelected.add(it) }
                answersMap[q2Pos]?.let { currentSelected.add(it) }

                if (optKey in currentSelected) {
                    currentSelected.remove(optKey)
                    answersMap.remove(q1Pos)
                    answersMap.remove(q2Pos)
                    if (currentSelected.isNotEmpty()) {
                        answersMap[q1Pos] = currentSelected[0]
                    }
                } else {
                    if (currentSelected.size < 2) {
                        currentSelected.add(optKey)
                    } else {
                        // Already 2 selected: replace the 2nd one
                        currentSelected[1] = optKey
                    }
                    if (currentSelected.size >= 1) answersMap[q1Pos] = currentSelected[0]
                    if (currentSelected.size >= 2) answersMap[q2Pos] = currentSelected[1]
                }

                updateAnsweredCounter()
                updateInstruction()

                // Re-apply selection UI on all options
                for (i in 0 until layoutOptions.childCount) {
                    val child = layoutOptions.getChildAt(i)
                    val cTv = child.findViewById<TextView>(R.id.tvOptionText)?.text?.toString() ?: ""
                    val cKey = cTv.firstOrNull()?.toString() ?: cTv
                    val selected = (cKey == answersMap[q1Pos] || cKey == answersMap[q2Pos])
                    applyOptionSelection(child, child.findViewById(R.id.ivRadioDot), child.findViewById(R.id.ivSelectedCheck), selected)
                }
            }

            layoutOptions.addView(optionView)
        }

        layoutDynamicQuestions.addView(view)
    }

    private fun bindMatchingChips(
        qPos: Int,
        tvSelected: TextView,
        chips: List<TextView>,
        options: List<String>
    ) {
        val activeBg = R.drawable.bg_chip_selected_pill_purple
        val inactiveBg = R.drawable.bg_pill_soft_purple
        val activeTextColor = ContextCompat.getColor(this, R.color.white)
        val inactiveTextColor = ContextCompat.getColor(this, R.color.primary_blue)

        fun updateUI() {
            val ans = answersMap[qPos]
            if (!ans.isNullOrBlank()) {
                tvSelected.text = "Đã chọn: $ans"
                tvSelected.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
            } else {
                tvSelected.text = "Chưa chọn"
                tvSelected.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }

            chips.forEachIndexed { idx, chip ->
                val letter = options[idx]
                val isSelected = (ans == letter)
                chip.setBackgroundResource(if (isSelected) activeBg else inactiveBg)
                chip.setTextColor(if (isSelected) activeTextColor else inactiveTextColor)
            }
        }

        updateUI()

        chips.forEachIndexed { idx, chip ->
            val letter = options[idx]
            chip.setOnClickListener {
                if (answersMap[qPos] == letter) {
                    answersMap.remove(qPos)
                } else {
                    answersMap[qPos] = letter
                }
                updateAnsweredCounter()
                updateUI()
            }
        }
    }

    private fun renderCam18Test2Section2() {
        val inflater = LayoutInflater.from(this)

        // Q11 & 12
        renderMultiSelectQuestionCard(
            q1Pos = 11,
            q2Pos = 12,
            badgeText = "11 & 12",
            instructionText = "Chọn 2 đáp án",
            stemText = "Questions 11 and 12\nWhat are the TWO main reasons why this site has been chosen for the housing development?",
            options = listOf(
                "A. It has suitable geographical features.",
                "B. There is easy access to local facilities.",
                "C. It has good connections with the airport.",
                "D. The land is of little agricultural value.",
                "E. It will be convenient for workers."
            )
        )

        // Q13 & 14
        renderMultiSelectQuestionCard(
            q1Pos = 13,
            q2Pos = 14,
            badgeText = "13 & 14",
            instructionText = "Chọn 2 đáp án",
            stemText = "Questions 13 and 14\nWhich TWO aspects of the planned housing development have people given positive feedback about?",
            options = listOf(
                "A. the facilities for cyclists",
                "B. the impact on the environment",
                "C. the encouragement of good relations between residents",
                "D. the low cost of all the accommodation",
                "E. the rural location"
            )
        )

        // Q15 to Q20: Map Labeling (School, Sports centre, Clinic, Community centre, Supermarket, Playground)
        val mapView = inflater.inflate(R.layout.layout_cam18_t2_p2_map_labeling, layoutDynamicQuestions, false)

        val openZoom = View.OnClickListener {
            ListeningImageDialogHelper.show(this, R.drawable.img_cam18_test2_part2_map, "Bản đồ Planned Housing Development (A–I)")
        }
        mapView.findViewById<View>(R.id.cardMapWrapperP2).setOnClickListener(openZoom)
        mapView.findViewById<View>(R.id.btnZoomMapP2).setOnClickListener(openZoom)

        val letters = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I")

        // 15 School
        val chips15 = listOf<TextView>(
            mapView.findViewById(R.id.chip15_A),
            mapView.findViewById(R.id.chip15_B),
            mapView.findViewById(R.id.chip15_C),
            mapView.findViewById(R.id.chip15_D),
            mapView.findViewById(R.id.chip15_E),
            mapView.findViewById(R.id.chip15_F),
            mapView.findViewById(R.id.chip15_G),
            mapView.findViewById(R.id.chip15_H),
            mapView.findViewById(R.id.chip15_I)
        )
        bindMatchingChips(15, mapView.findViewById(R.id.tvMapSelected15), chips15, letters)

        // 16 Sports centre
        val chips16 = listOf<TextView>(
            mapView.findViewById(R.id.chip16_A),
            mapView.findViewById(R.id.chip16_B),
            mapView.findViewById(R.id.chip16_C),
            mapView.findViewById(R.id.chip16_D),
            mapView.findViewById(R.id.chip16_E),
            mapView.findViewById(R.id.chip16_F),
            mapView.findViewById(R.id.chip16_G),
            mapView.findViewById(R.id.chip16_H),
            mapView.findViewById(R.id.chip16_I)
        )
        bindMatchingChips(16, mapView.findViewById(R.id.tvMapSelected16), chips16, letters)

        // 17 Clinic
        val chips17 = listOf<TextView>(
            mapView.findViewById(R.id.chip17_A),
            mapView.findViewById(R.id.chip17_B),
            mapView.findViewById(R.id.chip17_C),
            mapView.findViewById(R.id.chip17_D),
            mapView.findViewById(R.id.chip17_E),
            mapView.findViewById(R.id.chip17_F),
            mapView.findViewById(R.id.chip17_G),
            mapView.findViewById(R.id.chip17_H),
            mapView.findViewById(R.id.chip17_I)
        )
        bindMatchingChips(17, mapView.findViewById(R.id.tvMapSelected17), chips17, letters)

        // 18 Community centre
        val chips18 = listOf<TextView>(
            mapView.findViewById(R.id.chip18_A),
            mapView.findViewById(R.id.chip18_B),
            mapView.findViewById(R.id.chip18_C),
            mapView.findViewById(R.id.chip18_D),
            mapView.findViewById(R.id.chip18_E),
            mapView.findViewById(R.id.chip18_F),
            mapView.findViewById(R.id.chip18_G),
            mapView.findViewById(R.id.chip18_H),
            mapView.findViewById(R.id.chip18_I)
        )
        bindMatchingChips(18, mapView.findViewById(R.id.tvMapSelected18), chips18, letters)

        // 19 Supermarket
        val chips19 = listOf<TextView>(
            mapView.findViewById(R.id.chip19_A),
            mapView.findViewById(R.id.chip19_B),
            mapView.findViewById(R.id.chip19_C),
            mapView.findViewById(R.id.chip19_D),
            mapView.findViewById(R.id.chip19_E),
            mapView.findViewById(R.id.chip19_F),
            mapView.findViewById(R.id.chip19_G),
            mapView.findViewById(R.id.chip19_H),
            mapView.findViewById(R.id.chip19_I)
        )
        bindMatchingChips(19, mapView.findViewById(R.id.tvMapSelected19), chips19, letters)

        // 20 Playground
        val chips20 = listOf<TextView>(
            mapView.findViewById(R.id.chip20_A),
            mapView.findViewById(R.id.chip20_B),
            mapView.findViewById(R.id.chip20_C),
            mapView.findViewById(R.id.chip20_D),
            mapView.findViewById(R.id.chip20_E),
            mapView.findViewById(R.id.chip20_F),
            mapView.findViewById(R.id.chip20_G),
            mapView.findViewById(R.id.chip20_H),
            mapView.findViewById(R.id.chip20_I)
        )
        bindMatchingChips(20, mapView.findViewById(R.id.tvMapSelected20), chips20, letters)

        layoutDynamicQuestions.addView(mapView)
    }

    private fun renderCam18Test2Section3() {
        val inflater = LayoutInflater.from(this)
        val allQuestions = testDetail?.questions ?: emptyList()

        // Q21 to Q24: Multiple Choice (A, B, C)
        for (pos in 21..24) {
            val q = allQuestions.find { it.position == pos } ?: continue
            val choiceView = inflater.inflate(R.layout.item_listening_question_choice, layoutDynamicQuestions, false)
            val tvBadge = choiceView.findViewById<TextView>(R.id.tvChoiceNumberBadge)
            val tvStem = choiceView.findViewById<TextView>(R.id.tvChoiceQuestionStem)
            val btnBookmark = choiceView.findViewById<ImageView>(R.id.btnBookmarkChoice)
            val layoutOptions = choiceView.findViewById<LinearLayout>(R.id.layoutOptionsContainer)

            tvBadge.text = "$pos"
            tvStem.text = q.prompt

            val isFlagged = flaggedSet.contains(pos)
            btnBookmark.setImageResource(if (isFlagged) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border)
            btnBookmark.setColorFilter(ContextCompat.getColor(this, if (isFlagged) R.color.tertiary_orange_container else R.color.text_secondary))
            btnBookmark.setOnClickListener {
                if (flaggedSet.contains(pos)) {
                    flaggedSet.remove(pos)
                    btnBookmark.setImageResource(R.drawable.ic_bookmark_border)
                    btnBookmark.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary))
                } else {
                    flaggedSet.add(pos)
                    btnBookmark.setImageResource(R.drawable.ic_bookmark)
                    btnBookmark.setColorFilter(ContextCompat.getColor(this, R.color.tertiary_orange_container))
                }
            }

            for (opt in (q.options ?: emptyList())) {
                val optionView = inflater.inflate(R.layout.item_choice_option_row, layoutOptions, false)
                val tvOpt = optionView.findViewById<TextView>(R.id.tvOptionText)
                val ivDot = optionView.findViewById<ImageView>(R.id.ivRadioDot)
                val ivCheck = optionView.findViewById<ImageView>(R.id.ivSelectedCheck)

                tvOpt.text = opt
                val optKey = opt.firstOrNull()?.toString() ?: opt
                val isSelected = (answersMap[pos] == optKey || answersMap[pos] == opt)
                applyOptionSelection(optionView, ivDot, ivCheck, isSelected)

                optionView.setOnClickListener {
                    answersMap[pos] = optKey
                    updateAnsweredCounter()
                    for (i in 0 until layoutOptions.childCount) {
                        val child = layoutOptions.getChildAt(i)
                        val cTv = child.findViewById<TextView>(R.id.tvOptionText)?.text?.toString() ?: ""
                        val cKey = cTv.firstOrNull()?.toString() ?: cTv
                        val childSelected = (cKey == optKey)
                        applyOptionSelection(child, child.findViewById(R.id.ivRadioDot), child.findViewById(R.id.ivSelectedCheck), childSelected)
                    }
                }
                layoutOptions.addView(optionView)
            }

            layoutDynamicQuestions.addView(choiceView)
        }

        // Q25 & 26: Multi-Select Pair (Choose TWO letters)
        renderMultiSelectQuestionCard(
            q1Pos = 25,
            q2Pos = 26,
            badgeText = "25 & 26",
            instructionText = "Chọn 2 đáp án",
            stemText = "Questions 25 and 26\nWhich TWO issues following the Laki eruption surprised the students?",
            options = listOf(
                "A. how widespread the effects were",
                "B. how long-lasting the effects were",
                "C. the number of deaths it caused",
                "D. the speed at which the volcanic ash cloud spread",
                "E. how people ignored the warning signs"
            )
        )

        // Q27 to Q30: 2-column Matching Card with Comments Box
        val matchingView = inflater.inflate(R.layout.layout_cam18_t2_p3_matching, layoutDynamicQuestions, false)
        val btnZoomMatching3 = matchingView.findViewById<TextView>(R.id.btnZoomMatching3)
        btnZoomMatching3.setOnClickListener {
            ListeningImageDialogHelper.show(this, R.drawable.img_cam18_test2_part3_comments, "Comments A–F")
        }

        val letters = listOf("A", "B", "C", "D", "E", "F")

        // 27 Iceland
        val chips27 = listOf<TextView>(
            matchingView.findViewById(R.id.chip27_A),
            matchingView.findViewById(R.id.chip27_B),
            matchingView.findViewById(R.id.chip27_C),
            matchingView.findViewById(R.id.chip27_D),
            matchingView.findViewById(R.id.chip27_E),
            matchingView.findViewById(R.id.chip27_F)
        )
        bindMatchingChips(27, matchingView.findViewById(R.id.tvMatchingSelected27), chips27, letters)

        // 28 Egypt
        val chips28 = listOf<TextView>(
            matchingView.findViewById(R.id.chip28_A),
            matchingView.findViewById(R.id.chip28_B),
            matchingView.findViewById(R.id.chip28_C),
            matchingView.findViewById(R.id.chip28_D),
            matchingView.findViewById(R.id.chip28_E),
            matchingView.findViewById(R.id.chip28_F)
        )
        bindMatchingChips(28, matchingView.findViewById(R.id.tvMatchingSelected28), chips28, letters)

        // 29 UK
        val chips29 = listOf<TextView>(
            matchingView.findViewById(R.id.chip29_A),
            matchingView.findViewById(R.id.chip29_B),
            matchingView.findViewById(R.id.chip29_C),
            matchingView.findViewById(R.id.chip29_D),
            matchingView.findViewById(R.id.chip29_E),
            matchingView.findViewById(R.id.chip29_F)
        )
        bindMatchingChips(29, matchingView.findViewById(R.id.tvMatchingSelected29), chips29, letters)

        // 30 USA
        val chips30 = listOf<TextView>(
            matchingView.findViewById(R.id.chip30_A),
            matchingView.findViewById(R.id.chip30_B),
            matchingView.findViewById(R.id.chip30_C),
            matchingView.findViewById(R.id.chip30_D),
            matchingView.findViewById(R.id.chip30_E),
            matchingView.findViewById(R.id.chip30_F)
        )
        bindMatchingChips(30, matchingView.findViewById(R.id.tvMatchingSelected30), chips30, letters)

        layoutDynamicQuestions.addView(matchingView)
    }

    private fun renderQuestionsForSection(sectionNumber: Int) {
        layoutDynamicQuestions.removeAllViews()

        val isTest2 = currentTestId.contains("test2") || testDetail?.title?.contains("Test 2") == true

        if (isTest2 && sectionNumber == 1) {
            renderCam18Test2Section1()
            return
        }

        if (isTest2 && sectionNumber == 2) {
            renderCam18Test2Section2()
            return
        }

        if (isTest2 && sectionNumber == 3) {
            renderCam18Test2Section3()
            return
        }

        if (isTest2 && sectionNumber == 4) {
            renderCam18Test2Section4()
            return
        }

        val allQuestions = testDetail?.questions ?: emptyList()
        val sectionQuestions = allQuestions.filter { it.sectionNumber == sectionNumber }

        val inflater = LayoutInflater.from(this)
        var lastRenderedImageResId = 0

        for (question in sectionQuestions) {
            val qImgRes = question.imageResId ?: 0
            if (qImgRes != 0 && qImgRes != lastRenderedImageResId) {
                lastRenderedImageResId = qImgRes
                val contextCard = inflater.inflate(R.layout.item_listening_context_card, layoutDynamicQuestions, false)
                val tvCardTitle = contextCard.findViewById<TextView>(R.id.tvContextCardTitle)
                val ivContext = contextCard.findViewById<ImageView>(R.id.ivContextImage)
                val btnZoom = contextCard.findViewById<TextView>(R.id.btnZoomHint)
                val cardWrapper = contextCard.findViewById<View>(R.id.cardContextImageWrapper)

                tvCardTitle.text = question.imageTitle ?: "Tài liệu đề thi"
                ivContext.setImageResource(qImgRes)

                val openZoom = View.OnClickListener {
                    ListeningImageDialogHelper.show(this, qImgRes, question.imageTitle)
                }
                cardWrapper.setOnClickListener(openZoom)
                btnZoom.setOnClickListener(openZoom)

                layoutDynamicQuestions.addView(contextCard)
            }

            if (question.type == "MULTIPLE_CHOICE" && !question.options.isNullOrEmpty()) {
                val choiceView = inflater.inflate(R.layout.item_listening_question_choice, layoutDynamicQuestions, false)

                val tvBadge = choiceView.findViewById<TextView>(R.id.tvChoiceNumberBadge)
                val tvStem = choiceView.findViewById<TextView>(R.id.tvChoiceQuestionStem)
                val btnBookmark = choiceView.findViewById<ImageView>(R.id.btnBookmarkChoice)
                val layoutOptions = choiceView.findViewById<LinearLayout>(R.id.layoutOptionsContainer)
                val btnAttachment = choiceView.findViewById<TextView?>(R.id.btnQuickViewAttachment)

                if (qImgRes != 0) {
                    btnAttachment?.visibility = View.VISIBLE
                    btnAttachment?.text = "👁️ ${question.imageTitle ?: "Xem tài liệu đính kèm"}"
                    btnAttachment?.setOnClickListener {
                        ListeningImageDialogHelper.show(this, qImgRes, question.imageTitle)
                    }
                } else {
                    btnAttachment?.visibility = View.GONE
                }

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
                val btnAttachment = fillView.findViewById<TextView?>(R.id.btnQuickViewAttachment)

                if (qImgRes != 0) {
                    btnAttachment?.visibility = View.VISIBLE
                    btnAttachment?.text = "👁️ ${question.imageTitle ?: "Xem tài liệu đính kèm"}"
                    btnAttachment?.setOnClickListener {
                        ListeningImageDialogHelper.show(this, qImgRes, question.imageTitle)
                    }
                } else {
                    btnAttachment?.visibility = View.GONE
                }

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

        val isTest4 = testId.contains("test4") || testDetail?.title?.contains("Test 4") == true
        val isTest3 = testId.contains("test3") || testDetail?.title?.contains("Test 3") == true
        val isTest2 = testId.contains("test2") || testDetail?.title?.contains("Test 2") == true

        val defaultAnswers = if (isTest4) {
            listOf(
                "receptionist", "medical", "Chastons", "appointments", "database",
                "experience", "confident", "temporary", "1.15", "parking",
                "B", "A", "A", "C", "F",
                "G", "E", "A", "C", "B",
                "B", "D", "D", "A", "C",
                "G", "F", "A", "B", "C",
                "plot", "poverty", "Europe", "poetry", "drawings",
                "furniture", "lamps", "harbour", "children", "relatives"
            )
        } else if (isTest3) {
            listOf(
                "Marrowfield", "relative", "socialise", "full", "Domestic life",
                "clouds", "timing", "Animal Magic", "movement", "dark",
                "B", "C", "B", "D", "C",
                "B", "B", "C", "A", "A",
                "A", "E", "B", "D", "G",
                "E", "B", "C", "F", "A",
                "technical", "cheap", "thousands", "identification", "tracking",
                "military", "location", "prediction", "database", "trust"
            )
        } else if (isTest2) {
            listOf(
                "training", "discount", "taxi", "service", "English",
                "Wivenhoe", "equipment", "9.75", "deliveries", "Sunday",
                "B", "E", "B", "C", "G",
                "C", "D", "B", "H", "A",
                "C", "A", "B", "B", "A",
                "B", "D", "A", "C", "F",
                "convenient", "suits", "tailor", "profession", "visible",
                "string", "waist", "perfume", "image", "handbag"
            )
        } else {
            listOf(
                "DW30 7YZ", "24th April", "dentist", "parking", "Claxby",
                "late", "evening", "supermarket", "pollution", "storage",
                "C", "A", "A", "B", "E",
                "B", "G", "D", "A", "F",
                "A", "B", "A", "C", "B",
                "A", "B", "E", "A", "C",
                "fences", "family", "helicopters", "stress", "sides",
                "breathing", "feet", "employment", "weapons", "tourism"
            )
        }

        val defaultQuotes = if (isTest4) {
            listOf(
                "It's a receptionist job. I believe you've done similar work before, haven't you?",
                "The job is in Fordham, so it shouldn't be too far for you to travel. You'll be working at the local medical centre.",
                "It's near the railway station, on Chastons Road. That's spelled C-H-A-S-T-O-N-S.",
                "You would also arrange appointments, either in person or over the telephone, and change appointment times when necessary.",
                "Another responsibility would be updating the medical centre's database.",
                "The employer specifically needs somebody with previous experience, and I remembered your work at the sports centre.",
                "They need somebody who can remain calm and communicate confidently with members of the public.",
                "However, it's a temporary position rather than a permanent one.",
                "What time would I finish? At 1:15 p.m.",
                "Is there a car park for employees at the medical centre? Yes, staff can park there.",
                "We're standing at the entrance to a large building originally constructed in 1880. It was the private home of Alfred Palmer",
                "Apart from two rooms used as our offices, the university uses the main part of the building for teaching.",
                "Fortunately, admission to the museum is free... We do have a donation box, though, if you'd like to contribute.",
                "carrying a heavy backpack around may be uncomfortable. Please keep your coats on because the museum garden is quite cold today.",
                "Four Seasons features a short animated film... Young people studying film production created it especially for this museum.",
                "A specialist cheesemaker will demonstrate the production process this morning, and visitors may even have the chance to taste the finished product.",
                "Visitors can also test their memory by answering questions about the exhibits they've seen.",
                "These carts are old and fragile, so please keep children close and don't allow them to climb on the displays.",
                "One garden area, called Bees are Magic, is currently being redeveloped and cannot be visited.",
                "Finally, the pond is home to a variety of wildlife, including very young ducklings and small frogs.",
                "We could see how carefully they listened to the instructions and followed the steps in the correct order.",
                "one thing I particularly liked was that the children were enjoying themselves while learning something new.",
                "Once he started folding the paper, however, he became calmer and more focused.",
                "He seemed keen to solve problems by himself. Instead of immediately asking the teacher for help, he experimented with different approaches.",
                "she became motivated because she wanted to make the best paper mouse.",
                "She listened closely and produced a very good bird without much difficulty. The teacher could probably have given her a more challenging task.",
                "Zara seemed unfamiliar with origami and initially didn't understand what she needed to do.",
                "Preparing examples for every stage of the folding process must have taken time, but it was an effective idea.",
                "Maybe some teachers simply don't feel confident doing practical work with their hands.",
                "It might also be useful when we teach fractions, which is coming up soon.",
                "Although people may know the general plot, they often know less about the author himself.",
                "Besides writing, he spoke publicly about social problems, including poverty and the need for educational reform.",
                "He lived in several places elsewhere in Europe. Guernsey was his third place of exile",
                "He bought the property using money earned from publishing a collection of poetry in France.",
                "Family portraits remain on the ground floor, alongside drawings Hugo made during his travels.",
                "Hugo created decorative wooden wall panels himself. He bought inexpensive wooden furniture at a market and transformed it into detailed carvings.",
                "Chinese design influenced the wallpaper and lamps. Hugo made the lamps himself by copying existing examples.",
                "At the top of the house is a room known as the Lookout because it overlooks the harbour.",
                "He entertained well-known writers at his home, but he also invited many disadvantaged local children to dinner once a week.",
                "In 1927, Hugo's relatives owned the house and decided to donate it to the city of Paris."
            )
        } else if (isTest3) {
            listOf(
                "It's number 52, Marrowfield Street.",
                "Actually, a relative told me about it.",
                "I'd also like to meet other people who share my interest in photography.",
                "I think I'd prefer the full membership.",
                "The theme was Domestic Life. I submitted a photograph of a family sitting around a table and having dinner together.",
                "The judges felt that the photograph would have been better if there had been some clouds in the sky.",
                "They also thought I'd taken the photograph too early. I should have waited longer to capture the most impressive moment.",
                "It was called Animal Magic.",
                "There were quite a lot of photographs of animals, which can be difficult because animals don't always stay still.",
                "I managed to take a nice photograph of a fox, but the problem was that the picture was taken at night and it came out too dark.",
                "You should also avoid collecting mushrooms growing close to busy roads.",
                "You should never eat mushrooms collected by friends or neighbours unless you can identify them yourself.",
                "The fact that animals eat a particular mushroom doesn't mean that it is safe for humans, either.",
                "Some mushrooms that are perfectly safe to eat have bright colours, including red ones.",
                "It's best to go early in the morning. Other people or animals may have collected the mushrooms if you arrive later in the day.",
                "A much better option is to join a group led by someone with experience.",
                "Don't collect every mushroom you find. Only take what you actually need",
                "building developments and the removal of water from natural areas can damage mushroom habitats.",
                "put them in a brown paper bag and keep them in the refrigerator. You should use them within a few days",
                "There are plenty of ways to cook wild mushrooms. You can add them to stir-fries, risotto or pasta dishes.",
                "Their protests weren't successful in stopping industrialisation.",
                "I can understand why those workers felt threatened. They had good reasons to worry about their future.",
                "I also doubt that unemployment will decrease in the near future.",
                "people often predict that technology will give us more free time, but the evidence seems to suggest that working hours are actually increasing.",
                "Although computers have changed the way they work, the number of people employed in accounting has actually increased. Technology allows them to complete their tasks more efficiently",
                "That may be because people have higher incomes and can afford to spend more money on personal services.",
                "administrative employees often have greater responsibilities and a higher professional status.",
                "quarter of the population worked on farms. That's a huge difference from today, when the proportion is less than 0.2 per cent.",
                "As people live longer, demand for care workers is expected to increase.",
                "technology may eventually make many traditional banking tasks unnecessary. It's difficult to predict exactly what will happen to those jobs in the future.",
                "To achieve this, we need both legal regulations and suitable technical systems.",
                "launching satellites has become much less expensive, allowing more organisations to send objects into space.",
                "constellations, of thousands of satellites that operate together rather than relying on individual satellites.",
                "satellites do not always send information identifying themselves back to Earth.",
                "Current satellite-tracking methods are not yet sufficient to guarantee safety and prevent every possible collision.",
                "They may have military or commercial reasons for keeping that information private.",
                "We need to know the characteristics of each object and where it is located at any particular moment.",
                "scientists have to calculate and predict the future paths of objects in orbit.",
                "Another proposal is to create a shared database containing information about objects in space.",
                "Finally, cooperation depends on trust. This needs to exist not only between individual organisations but also at national and international levels."
            )
        } else if (isTest2) {
            listOf(
                "all employees get training, even temporary staff.",
                "you can get a discount at any of their restaurants.",
                "the company will pay for you to get a taxi home.",
                "We had to make sure we gave a really high level of service.",
                "good communication skills, so you'll need to have a certificate in English.",
                "The first one is in Wivenhoe Street.",
                "checking all the procedures for cleaning the equipment are being followed.",
                "to begin with, you'd be getting £9.75",
                "making sure there's enough stock each week and sorting out all the deliveries.",
                "you do get one Sunday off every four weeks",
                "because of the new industrial centre in Nunston, there's a lot of demand for housing for employees",
                "excellent hospital just 15 km away, and a large secondary school even closer",
                "blended in well with the natural features of the landscape, with provision made for protecting trees and wildlife",
                "place where people of all ages can live together happily.",
                "The school will be on that road, at the corner of the second turning to the left.",
                "A large sports centre is planned... western side of the development, just below the road that branches off",
                "The clinic will be just below this, to the right of a street of houses.",
                "row of specially designed houses... community centre will be adjoining this.",
                "There'll be a supermarket between the two entrances... just to the south of these.",
                "road that goes up from the south entrance... curves round to the left... playground will be in that curve",
                "impacted directly and indirectly on political events, as well as having massive social and economic consequences.",
                "surprised there were so many weather stations established by that time.",
                "Reports from the period blamed the haze for an increase in headaches, respiratory issues and asthma attacks.",
                "It was Benjamin Franklin who realised that... But a French naturalist beat him to it.",
                "volcanic ash drifted so swiftly, but not that the effects would go on for so long.",
                "strange weather events were being reported as far away as North America and North Africa.",
                "They were poisoned because they ate vegetation that had been contaminated with fluorine",
                "the famine there led to more people dying than any other country.",
                "included an unusually high percentage of people under the age of 25.",
                "snowbound until March in Virginia... ice floating down the Mississippi, which was unprecedented.",
                "convenient", "suits", "tailor", "profession", "visible",
                "string", "waist", "perfume", "image", "handbag"
            )
        } else {
            listOf(
                "It's DW3Q 7YZ.", "It's the 24th of April, isn't it?", "the main reason I came here was to go to the dentist.",
                "parking was so difficult to find and it costs so much.", "At the bus stop on Claxby Street.",
                "this morning it was late. Only about 10 minutes, but still.", "any time I've been in town in the evening... wait a long time",
                "Mainly just to go to the supermarket.", "because of all the pollution.", "it doesn't have any storage.",
                "people at the back of the room may be a bit squashed", "Other considerations, such as reliability, are crucial in voluntary work and age isn't related to these",
                "training doesn't end when you start working for us. It takes place before, during and after", "What we value is dedication.", "critical that you have enough hours in the day for whatever role we agree is suitable for you",
                "creativity to come up with an imaginative or novel way of fundraising", "useful if you can walk for long periods, sometimes uphill.",
                "good if you know something about nutrition and can give clear instructions.", "helping out at our Story Club for disabled children, especially if you have done some acting.",
                "take in a lot of information and not forget any important steps or details.",
                "hard to see through people's heads, isn't it?", "there's a whole range of … areas of work that we hadn't even thought of",
                "we're only first years, after all. We've got a lot to learn.", "none of them were very inspiring. They could have asked more people like today's speaker",
                "promised myself that I'd go through this course and keep an open mind till the end.", "we'll be unpaid assistants in the industry for quite a long time... I'm prepared for that",
                "more interested in her own appearance than his!", "she should have hidden her negative feelings about him, but she didn't.",
                "garments sold there were being returned because they fell apart in the wash", "an item that no one's stocking but that consumers are looking for",
                "Elephants were routinely knocking down fences around the park", "all belonging to one family. Because relationships are very important",
                "vets and park rangers flew over the park in helicopters", "done as quickly as possible so as to minimise the stress caused.",
                "all the elephants had to be placed on their sides.",
                "keep an eye on their breathing – if there were fewer than six breaths per minute", "Measurements were taken of each elephant's tusks... and also of their feet.",
                "Employment prospects have improved enormously, contributing to rising living standards", "many of them volunteered to give up their weapons",
                "All this has been a big draw for tourism, which contributes five times more than the illegal wildlife trade to GDP"
            )
        }

        val timestamps = if (isTest4) {
            listOf(
                106, 122, 134, 158, 169, 238, 258, 303, 337, 348,
                67, 101, 144, 176, 246, 290, 305, 350, 366, 379,
                88, 123, 157, 171, 186, 198, 214, 286, 323, 350,
                101, 123, 176, 194, 252, 273, 312, 340, 366, 390
            )
        } else if (isTest3) {
            listOf(
                124, 146, 178, 195, 266, 323, 340, 355, 359, 375,
                82, 82, 174, 128, 262, 299, 308, 338, 369, 390,
                76, 84, 122, 111, 193, 228, 254, 277, 283, 305,
                117, 132, 153, 175, 189, 232, 251, 282, 336, 364
            )
        } else if (isTest2) {
            listOf(
                99, 115, 136, 172, 204, 254, 288, 316, 361, 441,
                79, 112, 159, 179, 299, 308, 333, 340, 362, 370,
                106, 134, 163, 200, 226, 242, 313, 330, 353, 368,
                87, 125, 150, 170, 193, 223, 245, 261, 309, 325
            )
        } else {
            listOf(
                121, 154, 173, 192, 211, 273, 297, 308, 332, 345,
                78, 111, 130, 196, 168, 251, 269, 303, 312, 345,
                90, 119, 160, 174, 204, 238, 289, 314, 345, 366,
                143, 195, 210, 242, 256, 277, 289, 316, 336, 381
            )
        }

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
            val isCorrect = if (isTest4) {
                when (i) {
                    1 -> userRaw.contains("receptionist", ignoreCase = true)
                    2 -> userRaw.contains("medical", ignoreCase = true)
                    3 -> userRaw.contains("Chaston", ignoreCase = true)
                    4 -> userRaw.contains("appointment", ignoreCase = true)
                    5 -> userRaw.contains("database", ignoreCase = true)
                    6 -> userRaw.contains("experience", ignoreCase = true)
                    7 -> userRaw.contains("confident", ignoreCase = true)
                    8 -> userRaw.contains("temporary", ignoreCase = true)
                    9 -> userRaw.contains("1.15") || userRaw.contains("1:15")
                    10 -> userRaw.contains("parking", ignoreCase = true) || userRaw.contains("park", ignoreCase = true)
                    21, 22 -> {
                        val pairAnswers = setOf("B", "D")
                        val uAns = userRaw.uppercase().trim().take(1)
                        uAns in pairAnswers
                    }
                    31 -> userRaw.contains("plot", ignoreCase = true)
                    32 -> userRaw.contains("poverty", ignoreCase = true)
                    33 -> userRaw.contains("Europe", ignoreCase = true)
                    34 -> userRaw.contains("poetry", ignoreCase = true) || userRaw.contains("poem", ignoreCase = true)
                    35 -> userRaw.contains("drawing", ignoreCase = true)
                    36 -> userRaw.contains("furniture", ignoreCase = true)
                    37 -> userRaw.contains("lamp", ignoreCase = true)
                    38 -> userRaw.contains("harbour", ignoreCase = true) || userRaw.contains("harbor", ignoreCase = true)
                    39 -> userRaw.contains("child", ignoreCase = true)
                    40 -> userRaw.contains("relative", ignoreCase = true)
                    else -> {
                        if (userRaw.isBlank()) false
                        else userRaw.equals(correctAns, ignoreCase = true) ||
                             userRaw.contains(correctAns, ignoreCase = true) ||
                             correctAns.contains(userRaw, ignoreCase = true)
                    }
                }
            } else if (isTest3) {
                when (i) {
                    1 -> userRaw.contains("Marrowfield", ignoreCase = true)
                    2 -> userRaw.contains("relative", ignoreCase = true)
                    3 -> userRaw.contains("socialis", ignoreCase = true) || userRaw.contains("socializ", ignoreCase = true)
                    4 -> userRaw.equals("full", ignoreCase = true) || userRaw.contains("full", ignoreCase = true)
                    5 -> userRaw.contains("Domestic", ignoreCase = true)
                    6 -> userRaw.contains("cloud", ignoreCase = true)
                    7 -> userRaw.contains("timing", ignoreCase = true)
                    8 -> userRaw.contains("Animal Magic", ignoreCase = true)
                    9 -> userRaw.contains("movement", ignoreCase = true)
                    10 -> userRaw.contains("dark", ignoreCase = true)
                    11, 12 -> {
                        val pairAnswers = setOf("B", "C")
                        val uAns = userRaw.uppercase().trim().take(1)
                        uAns in pairAnswers
                    }
                    13, 14 -> {
                        val pairAnswers = setOf("B", "D")
                        val uAns = userRaw.uppercase().trim().take(1)
                        uAns in pairAnswers
                    }
                    21, 22 -> {
                        val pairAnswers = setOf("A", "E")
                        val uAns = userRaw.uppercase().trim().take(1)
                        uAns in pairAnswers
                    }
                    23, 24 -> {
                        val pairAnswers = setOf("B", "D")
                        val uAns = userRaw.uppercase().trim().take(1)
                        uAns in pairAnswers
                    }
                    31 -> userRaw.contains("technical", ignoreCase = true)
                    32 -> userRaw.contains("cheap", ignoreCase = true)
                    33 -> userRaw.contains("thousand", ignoreCase = true)
                    34 -> userRaw.contains("identification", ignoreCase = true) || userRaw.contains("identify", ignoreCase = true)
                    35 -> userRaw.contains("tracking", ignoreCase = true) || userRaw.contains("track", ignoreCase = true)
                    36 -> userRaw.contains("military", ignoreCase = true)
                    37 -> userRaw.contains("location", ignoreCase = true)
                    38 -> userRaw.contains("prediction", ignoreCase = true) || userRaw.contains("predict", ignoreCase = true)
                    39 -> userRaw.contains("database", ignoreCase = true)
                    40 -> userRaw.contains("trust", ignoreCase = true)
                    else -> {
                        if (userRaw.isBlank()) false
                        else userRaw.equals(correctAns, ignoreCase = true) ||
                             userRaw.contains(correctAns, ignoreCase = true) ||
                             correctAns.contains(userRaw, ignoreCase = true)
                    }
                }
            } else if (isTest2) {
                when (i) {
                    8 -> userRaw.contains("9.75", ignoreCase = true) || userRaw.contains("9,75")
                    9 -> userRaw.contains("deliveries", ignoreCase = true) || userRaw.contains("delivery", ignoreCase = true)
                    11, 12 -> {
                        val pairAnswers = setOf("B", "E")
                        val uAns = userRaw.uppercase().trim().take(1)
                        uAns in pairAnswers
                    }
                    13, 14 -> {
                        val pairAnswers = setOf("B", "C")
                        val uAns = userRaw.uppercase().trim().take(1)
                        uAns in pairAnswers
                    }
                    25, 26 -> {
                        val pairAnswers = setOf("A", "B")
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
            } else {
                when (i) {
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
                    timestampStr = timeStr,
                    imageResId = qDetail?.imageResId,
                    imageTitle = qDetail?.imageTitle
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
