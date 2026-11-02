package com.example.ielts.ui.reading

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.datasource.CambridgeReadingDataSource
import com.example.ielts.data.local.ReadingStatsManager
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.PassageDto
import com.example.ielts.data.remote.dto.QuestionDetailDto
import com.example.ielts.data.remote.dto.QuestionResultReviewDto
import com.example.ielts.data.remote.dto.ReadingSubmissionResultDto
import com.example.ielts.data.remote.dto.SubmitReadingRequest
import com.example.ielts.data.remote.dto.UserAnswerDto
import com.example.ielts.ui.reading.adapter.QuestionPaletteAdapter
import com.example.ielts.ui.reading.adapter.ReadingQuestionAdapter
import com.example.ielts.ui.reading.dialog.ReadingSubmitBottomSheet
import com.example.ielts.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ReadingPracticeActivity : AppCompatActivity() {

    // Views
    private lateinit var tvExamTimer: TextView
    private lateinit var btnPassage1: TextView
    private lateinit var btnPassage2: TextView
    private lateinit var btnPassage3: TextView
    private lateinit var btnSubmitExam: View
    private lateinit var btnCloseExam: View

    private lateinit var tvPassageBadge: TextView
    private lateinit var tvPassageWordCount: TextView
    private lateinit var tvPassageTitle: TextView
    private lateinit var tvPassageContent: TextView
    private lateinit var btnFontIncrease: View
    private lateinit var btnFontDecrease: View
    private lateinit var btnHighlightTool: TextView
    private lateinit var btnClearHighlight: View

    private lateinit var rvQuestions: RecyclerView
    private lateinit var rvPalette: RecyclerView
    private lateinit var tvPaletteProgress: TextView
    private lateinit var btnPrevQuestion: View
    private lateinit var btnNextQuestion: View

    // Adapters
    private lateinit var questionAdapter: ReadingQuestionAdapter
    private lateinit var paletteAdapter: QuestionPaletteAdapter

    // Data State
    private var testId: String = ""
    private var testTitle: String = ""
    private var totalDurationMinutes: Int = 60
    private var remainingSeconds: Int = 60 * 60

    private val passages = mutableListOf<PassageDto>()
    private val allQuestions = mutableListOf<QuestionDetailDto>()
    private var currentPassageIndex: Int = 0 // 0 -> P1, 1 -> P2, 2 -> P3
    private var activeQuestionPosition: Int = 1

    private val userAnswers = mutableMapOf<Int, String>()
    private val flaggedQuestions = mutableSetOf<Int>()

    private var countDownTimer: CountDownTimer? = null
    private var currentTextSizeSp: Float = 14f
    private var isExamStarted: Boolean = false
    private val passageHighlights = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_practice)

        testId = intent.getStringExtra("TEST_ID") ?: "cam18-test1"
        testTitle = intent.getStringExtra("TEST_TITLE") ?: "Cambridge IELTS 18 - Test 1"
        totalDurationMinutes = intent.getIntExtra("TEST_DURATION", 60)
        remainingSeconds = totalDurationMinutes * 60

        initViews()
        setupListeners()
        setupAdapters()
        loadExamData()
        showStartExamDialog()
    }

    private fun initViews() {
        tvExamTimer = findViewById(R.id.tvExamTimer)
        btnPassage1 = findViewById(R.id.btnPassage1)
        btnPassage2 = findViewById(R.id.btnPassage2)
        btnPassage3 = findViewById(R.id.btnPassage3)
        btnSubmitExam = findViewById(R.id.btnSubmitExam)
        btnCloseExam = findViewById(R.id.btnCloseExam)

        tvPassageBadge = findViewById(R.id.tvPassageBadge)
        tvPassageWordCount = findViewById(R.id.tvPassageWordCount)
        tvPassageTitle = findViewById(R.id.tvPassageTitle)
        tvPassageContent = findViewById(R.id.tvPassageContent)
        btnFontIncrease = findViewById(R.id.btnFontIncrease)
        btnFontDecrease = findViewById(R.id.btnFontDecrease)
        btnHighlightTool = findViewById(R.id.btnHighlightTool)
        btnClearHighlight = findViewById(R.id.btnClearHighlight)

        rvQuestions = findViewById(R.id.rvQuestions)
        rvPalette = findViewById(R.id.rvPalette)
        tvPaletteProgress = findViewById(R.id.tvPaletteProgress)
        btnPrevQuestion = findViewById(R.id.btnPrevQuestion)
        btnNextQuestion = findViewById(R.id.btnNextQuestion)

        findViewById<TextView>(R.id.tvExamHeaderTitle)?.text = testTitle
    }

    private fun setupListeners() {
        btnCloseExam.setOnClickListener {
            showExitConfirmDialog()
        }

        btnPassage1.setOnClickListener { switchPassage(0) }
        btnPassage2.setOnClickListener { switchPassage(1) }
        btnPassage3.setOnClickListener { switchPassage(2) }

        btnSubmitExam.setOnClickListener {
            showSubmitConfirmModal()
        }

        // Font Adjusters
        btnFontIncrease.setOnClickListener {
            if (currentTextSizeSp < 20f) {
                currentTextSizeSp += 1.5f
                tvPassageContent.textSize = currentTextSizeSp
            }
        }

        btnFontDecrease.setOnClickListener {
            if (currentTextSizeSp > 12f) {
                currentTextSizeSp -= 1.5f
                tvPassageContent.textSize = currentTextSizeSp
            }
        }

        // Highlight System
        setupHighlightFeature()

        // Draggable Split Divider
        setupDraggableSplitDivider()

        // Navigation Next / Prev Question
        btnPrevQuestion.setOnClickListener {
            if (activeQuestionPosition > 1) {
                jumpToQuestion(activeQuestionPosition - 1)
            }
        }

        btnNextQuestion.setOnClickListener {
            if (activeQuestionPosition < 40) {
                jumpToQuestion(activeQuestionPosition + 1)
            }
        }
    }

    private fun setupDraggableSplitDivider() {
        val splitContainer = findViewById<LinearLayout>(R.id.splitContainer)
        val panePassage = findViewById<LinearLayout>(R.id.panePassage)
        val paneQuestions = findViewById<LinearLayout>(R.id.paneQuestions)
        val handleSplitDivider = findViewById<View>(R.id.handleSplitDivider)

        var initialTouchY = 0f
        var initialPassageHeight = 0

        handleSplitDivider.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialTouchY = event.rawY
                    initialPassageHeight = panePassage.height
                    v.setBackgroundColor(Color.parseColor("#DBEAFE"))
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = event.rawY - initialTouchY
                    val totalAvailableHeight = splitContainer.height - handleSplitDivider.height
                    val minHeight = (120 * resources.displayMetrics.density).toInt()
                    val maxHeight = totalAvailableHeight - (120 * resources.displayMetrics.density).toInt()
                    val newHeight = (initialPassageHeight + deltaY).toInt().coerceIn(minHeight, maxHeight)

                    val passageParams = panePassage.layoutParams as LinearLayout.LayoutParams
                    passageParams.height = newHeight
                    passageParams.weight = 0f
                    panePassage.layoutParams = passageParams

                    val questionParams = paneQuestions.layoutParams as LinearLayout.LayoutParams
                    questionParams.height = 0
                    questionParams.weight = 1f
                    paneQuestions.layoutParams = questionParams

                    panePassage.requestLayout()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.setBackgroundResource(R.drawable.bg_split_divider)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupHighlightFeature() {
        // 1. Android contextual selection menu action
        tvPassageContent.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.add(0, 9999, 0, "✏ Highlight")?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                if (item?.itemId == 9999) {
                    val start = tvPassageContent.selectionStart
                    val end = tvPassageContent.selectionEnd
                    if (start >= 0 && end > start) {
                        applyHighlight(start, end)
                        mode?.finish()
                        return true
                    }
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {}
        }

        // 2. Toolbar button
        btnHighlightTool.setOnClickListener {
            val start = tvPassageContent.selectionStart
            val end = tvPassageContent.selectionEnd
            if (start >= 0 && end > start) {
                applyHighlight(start, end)
            } else {
                Toast.makeText(this, "Bôi đen từ hoặc câu trong bài đọc rồi nhấn Highlight", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Clear Highlights
        btnClearHighlight.setOnClickListener {
            passageHighlights[currentPassageIndex]?.clear()
            displayPassage(currentPassageIndex)
            Toast.makeText(this, "Đã xoá các đoạn highlight của bài đọc này", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyHighlight(start: Int, end: Int) {
        val list = passageHighlights.getOrPut(currentPassageIndex) { mutableListOf() }
        list.add(Pair(start, end))

        val currentText = tvPassageContent.text
        val spannable = if (currentText is SpannableStringBuilder) currentText else SpannableStringBuilder(currentText)
        spannable.setSpan(
            BackgroundColorSpan(Color.parseColor("#FEF08A")), // Soft yellow highlight
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvPassageContent.text = spannable
        Toast.makeText(this, "Đã highlight đoạn văn bản!", Toast.LENGTH_SHORT).show()
    }

    private fun showStartExamDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_start_reading_exam, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<TextView>(R.id.tvStartExamTitle)?.text = testTitle
        dialogView.findViewById<View>(R.id.btnConfirmStartExam)?.setOnClickListener {
            dialog.dismiss()
            isExamStarted = true
            startTimer()
        }
        dialogView.findViewById<View>(R.id.btnCancelStartExam)?.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun setupAdapters() {
        // Questions List
        questionAdapter = ReadingQuestionAdapter(
            questions = emptyList(),
            getAnswer = { pos -> userAnswers[pos] },
            isFlagged = { pos -> flaggedQuestions.contains(pos) },
            onAnswerSelected = { pos, ans ->
                userAnswers[pos] = ans
                updateProgressHeader()
                paletteAdapter.notifyItemChanged(pos - 1)
            },
            onToggleFlag = { pos ->
                if (flaggedQuestions.contains(pos)) {
                    flaggedQuestions.remove(pos)
                } else {
                    flaggedQuestions.add(pos)
                }
                paletteAdapter.notifyItemChanged(pos - 1)
            }
        )
        rvQuestions.layoutManager = LinearLayoutManager(this)
        rvQuestions.adapter = questionAdapter

        // Bottom 40 Palette
        paletteAdapter = QuestionPaletteAdapter(
            totalQuestions = 40,
            getAnswerState = { pos -> userAnswers[pos]?.isNotBlank() == true },
            getFlagState = { pos -> flaggedQuestions.contains(pos) },
            getActivePosition = { activeQuestionPosition },
            onQuestionSelected = { pos ->
                jumpToQuestion(pos)
            }
        )
        rvPalette.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvPalette.adapter = paletteAdapter
    }

    private fun loadExamData() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getReadingTestDetail(testId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.passages.isNotEmpty()) {
                        passages.clear()
                        passages.addAll(body.passages)
                    }
                    if (body.questions.isNotEmpty()) {
                        allQuestions.clear()
                        allQuestions.addAll(body.questions)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Fallback sample data if empty
            if (passages.isEmpty()) {
                val camPassages = CambridgeReadingDataSource.getPassagesForTest(testId)
                if (camPassages.isNotEmpty()) {
                    passages.addAll(camPassages)
                } else {
                    populateSamplePassages()
                }
            }
            if (allQuestions.isEmpty()) {
                populateSampleQuestions()
            }

            switchPassage(0)
            updateProgressHeader()
            paletteAdapter.notifyDataSetChanged()
        }
    }

    private fun switchPassage(index: Int) {
        if (index !in passages.indices) return
        currentPassageIndex = index

        // Update Tabs style
        val selectedBg = R.drawable.bg_btn_passage_selected
        val unselectedBg = R.drawable.bg_btn_passage_unselected
        val white = Color.WHITE
        val gray = ContextCompat.getColor(this, R.color.text_secondary)

        btnPassage1.setBackgroundResource(if (index == 0) selectedBg else unselectedBg)
        btnPassage1.setTextColor(if (index == 0) white else gray)

        btnPassage2.setBackgroundResource(if (index == 1) selectedBg else unselectedBg)
        btnPassage2.setTextColor(if (index == 1) white else gray)

        btnPassage3.setBackgroundResource(if (index == 2) selectedBg else unselectedBg)
        btnPassage3.setTextColor(if (index == 2) white else gray)

        displayPassage(index)

        // Filter and display questions for this passage
        val passageNum = index + 1
        val passageQuestions = allQuestions.filter { it.passageNumber == passageNum }
        questionAdapter.updateData(passageQuestions)
    }

    private fun displayPassage(index: Int) {
        val p = passages[index]
        tvPassageBadge.text = "READING PASSAGE ${p.passageNumber}"
        tvPassageWordCount.text = "📖 ${p.wordCount.takeIf { it > 0 } ?: 950} từ"
        tvPassageTitle.text = p.title

        // Format paragraph letters cleanly
        val formattedContent = p.content
            .replace("\\n", "\n")
            .replace("[Paragraph A]", "\nA. ")
            .replace("[Paragraph B]", "\n\nB. ")
            .replace("[Paragraph C]", "\n\nC. ")
            .replace("[Paragraph D]", "\n\nD. ")
            .replace("[Paragraph E]", "\n\nE. ")
            .replace("[Paragraph F]", "\n\nF. ")
            .replace("[Paragraph G]", "\n\nG. ")

        val spannable = SpannableStringBuilder(formattedContent)
        val highlights = passageHighlights[index]
        if (!highlights.isNullOrEmpty()) {
            for (range in highlights) {
                if (range.first in 0 until spannable.length && range.second in (range.first + 1)..spannable.length) {
                    spannable.setSpan(
                        BackgroundColorSpan(Color.parseColor("#FEF08A")),
                        range.first,
                        range.second,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
        tvPassageContent.text = spannable
    }

    private fun jumpToQuestion(pos: Int) {
        activeQuestionPosition = pos
        val targetPassage = when {
            pos <= 13 -> 0
            pos <= 26 -> 1
            else -> 2
        }

        if (targetPassage != currentPassageIndex) {
            switchPassage(targetPassage)
        }

        // Scroll question list to target item
        val passageQuestions = allQuestions.filter { it.passageNumber == targetPassage + 1 }
        val itemIndex = passageQuestions.indexOfFirst { it.position == pos }
        if (itemIndex != -1) {
            rvQuestions.smoothScrollToPosition(itemIndex)
        }

        // Scroll palette to keep active in view
        rvPalette.smoothScrollToPosition(pos - 1)
        paletteAdapter.notifyDataSetChanged()
    }

    private fun updateProgressHeader() {
        val answeredCount = userAnswers.values.count { it.isNotBlank() }
        val percent = (answeredCount * 100) / 40
        tvPaletteProgress.text = "• Đã làm: $answeredCount/40 câu • $percent%"
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((remainingSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingSeconds = (millisUntilFinished / 1000).toInt()
                val minutes = remainingSeconds / 60
                val seconds = remainingSeconds % 60
                tvExamTimer.text = String.format("%02d:%02d", minutes, seconds)

                // Warning color if under 5 minutes
                if (remainingSeconds < 300) {
                    tvExamTimer.setTextColor(Color.parseColor("#DC2626"))
                }
            }

            override fun onFinish() {
                Toast.makeText(this@ReadingPracticeActivity, "Đã hết 60 phút! Tự động nộp bài...", Toast.LENGTH_LONG).show()
                submitExam()
            }
        }.start()
    }

    private fun showSubmitConfirmModal() {
        val completedCount = userAnswers.values.count { it.isNotBlank() }
        val flaggedCount = flaggedQuestions.size
        val unansweredList = (1..40).filter { userAnswers[it].isNullOrBlank() }

        val bottomSheet = ReadingSubmitBottomSheet(
            completedCount = completedCount,
            flaggedCount = flaggedCount,
            totalCount = 40,
            unansweredList = unansweredList,
            remainingSeconds = remainingSeconds,
            onConfirmSubmit = {
                submitExam()
            }
        )
        bottomSheet.show(supportFragmentManager, "SubmitConfirmModal")
    }

    private fun submitExam() {
        countDownTimer?.cancel()

        lifecycleScope.launch {
            val answersList = allQuestions.map { q ->
                UserAnswerDto(
                    questionId = q.id,
                    answer = userAnswers[q.position] ?: ""
                )
            }

            val request = SubmitReadingRequest(
                testId = testId,
                durationSpentSeconds = (totalDurationMinutes * 60) - remainingSeconds,
                answers = answersList
            )

            // Evaluate all 40 questions to construct complete result review breakdown
            var score = 0
            val fullBreakdown = mutableListOf<QuestionResultReviewDto>()

            for (pos in 1..40) {
                val q = allQuestions.find { it.position == pos }
                val qType = q?.type ?: if (pos % 2 == 0) "TRUE_FALSE" else "MULTIPLE_CHOICE"
                val qPrompt = q?.prompt ?: "Câu hỏi số $pos kiểm tra thông tin chi tiết bài đọc Cambridge IELTS."
                val qPassage = q?.passageNumber ?: when {
                    pos <= 13 -> 1
                    pos <= 26 -> 2
                    else -> 3
                }
                val userAns = userAnswers[pos]?.trim()
                val (correctAns, explanation) = getExplanationForQuestion(pos, qType)

                val isCorrect = if (userAns.isNullOrBlank()) {
                    false
                } else {
                    if (qType == "TRUE_FALSE" || qType == "MULTIPLE_CHOICE") {
                        userAns.equals(correctAns, ignoreCase = true)
                    } else {
                        userAns.contains(correctAns, ignoreCase = true) || correctAns.contains(userAns, ignoreCase = true)
                    }
                }

                if (isCorrect) {
                    score++
                }

                fullBreakdown.add(
                    QuestionResultReviewDto(
                        questionId = q?.id ?: "q_$pos",
                        position = pos,
                        userAnswer = if (userAns.isNullOrBlank()) null else userAns,
                        correctAnswer = correctAns,
                        isCorrect = isCorrect,
                        explanation = explanation,
                        prompt = qPrompt,
                        passageNumber = qPassage,
                        evidenceQuote = getEvidenceQuoteForQuestion(pos)
                    )
                )
            }

            var bandEstimate = calculateBand(score)
            var resultJson: String? = null

            try {
                val token = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                    .getString(Constants.KEY_AUTH_TOKEN, null)
                val response = ApiClient.apiService.submitReading("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    val resultDto = response.body()!!
                    score = resultDto.correctCount
                    bandEstimate = resultDto.resolvedBand()
                    val backendItems = resultDto.resolvedBreakdown()
                    if (backendItems.size >= 40) {
                        resultJson = Gson().toJson(resultDto)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (resultJson == null) {
                val fallbackDto = ReadingSubmissionResultDto(
                    submissionId = "local-submission",
                    testId = testId,
                    score = score,
                    correctCount = score,
                    totalQuestions = 40,
                    bandEstimate = bandEstimate,
                    bandScore = bandEstimate,
                    durationSpentSeconds = (totalDurationMinutes * 60) - remainingSeconds,
                    breakdown = fullBreakdown,
                    results = fullBreakdown
                )
                resultJson = Gson().toJson(fallbackDto)
            }

            ReadingStatsManager.recordTestCompletion(
                context = this@ReadingPracticeActivity,
                testId = testId,
                score = score,
                totalQuestions = 40,
                bandEstimate = bandEstimate,
                resultJson = resultJson,
                passagesJson = Gson().toJson(passages)
            )

            val intent = Intent(this@ReadingPracticeActivity, ReadingResultActivity::class.java).apply {
                putExtra("TEST_ID", testId)
                putExtra("TEST_TITLE", testTitle)
                putExtra("SCORE", score)
                putExtra("TOTAL_QUESTIONS", 40)
                putExtra("BAND_ESTIMATE", bandEstimate)
                putExtra("DURATION_SPENT", (totalDurationMinutes * 60) - remainingSeconds)
                putExtra("RESULT_JSON", resultJson)
                putExtra("PASSAGES_JSON", Gson().toJson(passages))
            }
            startActivity(intent)
            finish()
        }
    }

    private fun calculateBand(correctCount: Int): Double {
        return when {
            correctCount >= 39 -> 9.0
            correctCount >= 37 -> 8.5
            correctCount >= 35 -> 8.0
            correctCount >= 33 -> 7.5
            correctCount >= 30 -> 7.0
            correctCount >= 27 -> 6.5
            correctCount >= 23 -> 6.0
            correctCount >= 20 -> 5.5
            correctCount >= 16 -> 5.0
            correctCount >= 13 -> 4.5
            correctCount >= 10 -> 4.0
            correctCount >= 8 -> 3.5
            correctCount >= 6 -> 3.0
            correctCount >= 4 -> 2.5
            correctCount >= 2 -> 2.0
            correctCount >= 1 -> 1.0
            else -> 0.0
        }
    }

    private fun getExplanationForQuestion(position: Int, type: String): Pair<String, String> {
        return when (position) {
            1 -> Pair("TRUE", "Dẫn chứng: Paragraph B, '...canopy coverage exceeding thirty percent correlates directly with an average 4.2°C temperature attenuation during peak summer heatwaves.' (Độ che phủ tán cây trên 30% làm giảm nhiệt độ trung bình 4.2°C, tức là hơn 4°C -> Đúng).")
            2 -> Pair("NOT GIVEN", "Dẫn chứng: Bài đọc có nhắc đến việc cây hấp thụ nhiệt và khí thải nhưng không đề cập quy định bắt buộc của EU về việc trồng cây sồi bản địa.")
            3 -> Pair("FALSE", "Dẫn chứng: Paragraph C, '...root matrices act as subterranean porous sumps, mitigating flash flooding events...' (Hệ thống rễ đóng vai trò như bể chứa xốp ngầm, làm giảm hiện tượng lũ quét chứ không làm trầm trọng thêm -> Sai).")
            4 -> Pair("TRUE", "Dẫn chứng: Paragraph D, '...diverse municipal arboretums sequester substantial nitrogen dioxide concentrations emitted from high-density vehicular corridors.' (Vườn thực vật đô thị hấp thụ lượng lớn NO2 phát thải từ giao thông -> Đúng).")
            5 -> Pair("stormwater", "Dẫn chứng: Paragraph A, '...reduce ambient stormwater runoff, and capture airborne particulates.' (Giảm dòng chảy nước mưa đô thị).")
            6 -> Pair("heatwaves", "Dẫn chứng: Paragraph B, '...attenuation during peak summer heatwaves.' (Giảm nhiệt trong các đợt nắng nóng gay gắt mùa hè).")
            7 -> Pair("topsoil", "Dẫn chứng: Paragraph C, '...mitigating flash flooding events by stabilizing topsoil shear resistance...' (Ổn định độ bền trượt của tầng đất mặt).")
            8 -> Pair("corridors", "Dẫn chứng: Paragraph D, '...emitted from high-density vehicular corridors.' (Phát thải từ các hành lang giao thông đông đúc).")
            9 -> Pair("A", "Dẫn chứng: Paragraph A & B, mật độ lá và sự thoát hơi nước sinh học là yếu tố then chốt tạo bóng râm và làm mát tiểu khí hậu đô thị.")
            10 -> Pair("C", "Dẫn chứng: Paragraph C, kết cấu rễ và nền đất tự nhiên giúp tăng cường sự thẩm thấu thủy văn và hạn chế ngập úng.")
            11 -> Pair("A", "Dẫn chứng: Paragraph D, các loài cây bóng mát lớn hấp thụ khí NO2 từ các phương tiện giao thông trên đường.")
            12 -> Pair("B", "Dẫn chứng: Đoạn B phân tích việc che phủ tán cây giúp giảm hiện tượng đảo nhiệt đô thị (Urban Heat Island).")
            13 -> Pair("D", "Dẫn chứng: Toàn bộ bài đọc nhấn mạnh tầm quan trọng của cơ sở hạ tầng sinh học tự nhiên trong quy hoạch đô thị tương lai.")
            in 14..26 -> {
                val p = position
                val isTf = type == "TRUE_FALSE"
                if (isTf) {
                    val ans = if (p % 3 == 0) "NOT GIVEN" else if (p % 2 == 0) "FALSE" else "TRUE"
                    Pair(ans, "Dẫn chứng: Passage 2, đoạn ${(p - 14) / 4 + 1}, chi tiết về mạng lưới thương mại Con đường Tơ lụa và sự trao đổi công nghệ sản xuất giấy giữa phương Đông và Trung Á.")
                } else {
                    val ans = when (p % 4) { 0 -> "A"; 1 -> "B"; 2 -> "C"; else -> "D" }
                    Pair(ans, "Dẫn chứng: Passage 2, đoạn B-C, phân tích sự lan tỏa của tôn giáo, văn hóa và kỹ thuật qua các đoàn lữ hành trên Con đường Tơ lụa.")
                }
            }
            else -> {
                val p = position
                if (type == "FILL_IN_BLANK") {
                    Pair("black box", "Dẫn chứng: Passage 3, Paragraph C, '...algorithmic opacity, commonly designated the black box phenomenon...' (Hiện tượng hộp đen thuật toán trong chẩn đoán y tế).")
                } else if (type == "TRUE_FALSE") {
                    val ans = if (p % 2 == 0) "TRUE" else "FALSE"
                    Pair(ans, "Dẫn chứng: Passage 3, Paragraph A-B, các thuật toán học sâu chứng minh khả năng phân tích hình ảnh X-quang và chụp cắt lớp quang học võng mạc đạt độ chính xác cao.")
                } else {
                    val ans = when (p % 4) { 0 -> "B"; 1 -> "A"; 2 -> "C"; else -> "D" }
                    Pair(ans, "Dẫn chứng: Passage 3, Paragraph B, việc ứng dụng trí tuệ nhân tạo trong y tế giúp phát hiện sớm tổn thương trước khi xảy ra thoái hóa không hồi phục.")
                }
            }
        }
    }

    private fun getEvidenceQuoteForQuestion(position: Int): String {
        return when (position) {
            1 -> "canopy coverage exceeding thirty percent correlates directly with an average 4.2°C temperature attenuation"
            2 -> "European"
            3 -> "root matrices act as subterranean porous sumps, mitigating flash flooding events"
            4 -> "diverse municipal arboretums sequester substantial nitrogen dioxide concentrations"
            5 -> "reduce ambient stormwater runoff"
            6 -> "peak summer heatwaves"
            7 -> "stabilizing topsoil shear resistance"
            8 -> "emitted from high-density vehicular corridors"
            9 -> "modulate microclimates, reduce ambient stormwater runoff"
            10 -> "natural hydrological percolation"
            11 -> "sequester substantial nitrogen dioxide concentrations"
            12 -> "satellite thermography studies"
            13 -> "complex biological infrastructures"
            in 14..26 -> {
                when (position % 4) {
                    0 -> "Papermaking techniques migrated eastward along trans-Pamir caravan tracks"
                    1 -> "expansive network of Eurasian trade conduits active from the Han dynasty"
                    2 -> "Central Asian equine breeds, and Roman glassware"
                    else -> "facilitated unprecedented intellectual and religious dissemination"
                }
            }
            else -> {
                when (position % 4) {
                    0 -> "algorithmic opacity, commonly designated the 'black box' phenomenon"
                    1 -> "Machine learning algorithms trained on expansive radiological datasets"
                    2 -> "Deep neural network architectures evaluate subtle optical coherence tomography"
                    else -> "irreversible photoreceptor apoptosis"
                }
            }
        }
    }

    private fun showExitConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Tạm dừng làm bài?")
            .setMessage("Bạn có chắc chắn muốn thoát khỏi phòng thi? Tiến độ làm bài thi sẽ không được lưu nếu chưa nộp.")
            .setPositiveButton("Thoát phòng thi") { _, _ ->
                countDownTimer?.cancel()
                finish()
            }
            .setNegativeButton("Tiếp tục làm bài", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    // --- SAMPLE FALLBACK DATA ---
    private fun populateSamplePassages() {
        passages.add(
            PassageDto(
                passageNumber = 1,
                title = "The Secret Life of Urban Forests",
                wordCount = 1140,
                content = "[Paragraph A] Urban trees are not merely aesthetic enhancements to concrete landscapes; they are complex biological infrastructures that modulate microclimates, reduce ambient stormwater runoff, and capture airborne particulates.\n\n[Paragraph B] Recent satellite thermography studies conducted across European metropolitan zones revealed that canopy coverage exceeding thirty percent correlates directly with an average 4.2°C temperature attenuation during peak summer heatwaves.\n\n[Paragraph C] Furthermore, root matrices act as subterranean porous sumps, mitigating flash flooding events by stabilizing topsoil shear resistance and augmenting natural hydrological percolation.\n\n[Paragraph D] In addition to climatic buffers, diverse municipal arboretums sequester substantial nitrogen dioxide concentrations emitted from high-density vehicular corridors."
            )
        )
        passages.add(
            PassageDto(
                passageNumber = 2,
                title = "The History of Silk Road Trade & Cultural Exchange",
                wordCount = 1220,
                content = "[Paragraph A] The Silk Road was an expansive network of Eurasian trade conduits active from the Han dynasty until the Ottoman boycott of Western commerce.\n\n[Paragraph B] Beyond raw commodities such as Chinese sericulture, Central Asian equine breeds, and Roman glassware, the arteries facilitated unprecedented intellectual and religious dissemination.\n\n[Paragraph C] Papermaking techniques migrated eastward along trans-Pamir caravan tracks, revolutionizing archival administration throughout medieval Islamic caliphates."
            )
        )
        passages.add(
            PassageDto(
                passageNumber = 3,
                title = "Artificial Intelligence & Computational Diagnostics in Healthcare",
                wordCount = 1350,
                content = "[Paragraph A] Machine learning algorithms trained on expansive radiological datasets now demonstrate non-inferiority to board-certified radiologists in identifying early-stage pulmonary nodules.\n\n[Paragraph B] Deep neural network architectures evaluate subtle optical coherence tomography variations to diagnose macular degeneration prior to irreversible photoreceptor apoptosis.\n\n[Paragraph C] However, algorithmic opacity, commonly designated the 'black box' phenomenon, continues to provoke ethical and jurisdictional hesitation among clinical practitioners."
            )
        )
    }

    private fun populateSampleQuestions() {
        // P1: 1..13
        for (i in 1..4) {
            allQuestions.add(
                QuestionDetailDto(
                    id = "p1_q$i",
                    position = i,
                    type = "TRUE_FALSE",
                    prompt = when (i) {
                        1 -> "Urban canopy coverage over thirty percent reduces urban temperatures by more than four degrees Celsius."
                        2 -> "The European Union mandates that all newly developed urban zones must plant native oak trees."
                        3 -> "Subterranean root structures worsen flash flooding events in concrete city areas."
                        else -> "Vehicular nitrogen dioxide emissions are absorbed by municipal arboretums."
                    },
                    options = "TRUE FALSE NOT GIVEN",
                    passageNumber = 1,
                    lineRef = "P1 • Dòng ${i * 4 + 4}"
                )
            )
        }
        for (i in 5..8) {
            allQuestions.add(
                QuestionDetailDto(
                    id = "p1_q$i",
                    position = i,
                    type = "FILL_IN_BLANK",
                    prompt = "Canopy shade prevents soil erosion by moderating [Câu $i] absorption throughout seasonal rainy cycles.",
                    passageNumber = 1,
                    lineRef = "P1 • Summary Completion"
                )
            )
        }
        for (i in 9..13) {
            allQuestions.add(
                QuestionDetailDto(
                    id = "p1_q$i",
                    position = i,
                    type = "MULTIPLE_CHOICE",
                    prompt = "Which primary factor determines the cooling effectiveness of mature canopy cover in dense metropolitan zones?",
                    options = "A. Foliage leaf density and transpirational cooling\nB. Deep limestone bedrock deposits\nC. Concrete pavement reflection angles\nD. Directional seasonal trade winds",
                    passageNumber = 1,
                    lineRef = "P1 • Multiple Choice"
                )
            )
        }

        // P2: 14..26
        for (i in 14..26) {
            allQuestions.add(
                QuestionDetailDto(
                    id = "p2_q$i",
                    position = i,
                    type = if (i % 2 == 0) "MULTIPLE_CHOICE" else "TRUE_FALSE",
                    prompt = "The trans-Pamir caravan network was essential for the dissemination of technological papermaking know-how.",
                    options = if (i % 2 == 0) "A. Han imperial edicts\nB. Religious and scholastic texts\nC. Silver bullion coinage\nD. Bronze metallurgical ores" else "TRUE FALSE NOT GIVEN",
                    passageNumber = 2,
                    lineRef = "P2 • Question $i"
                )
            )
        }

        // P3: 27..40
        for (i in 27..40) {
            allQuestions.add(
                QuestionDetailDto(
                    id = "p3_q$i",
                    position = i,
                    type = if (i % 3 == 0) "FILL_IN_BLANK" else if (i % 2 == 0) "TRUE_FALSE" else "MULTIPLE_CHOICE",
                    prompt = "The inability of deep networks to show the intelligible causal reasoning behind classifications is commonly called the '________' problem.",
                    options = if (i % 2 == 0) "TRUE FALSE NOT GIVEN" else "A. Black box\nB. Algorithmic bias\nC. Optical variance\nD. Data loss",
                    passageNumber = 3,
                    lineRef = "P3 • Question $i"
                )
            )
        }
    }
}
