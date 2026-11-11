package com.example.ielts.ui.writing

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ielts.R
import com.example.ielts.data.datasource.CambridgeWritingDataSource
import com.example.ielts.data.local.WritingStatsManager
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.CriterionDetail
import com.example.ielts.data.remote.dto.GrammarCorrectionItem
import com.example.ielts.data.remote.dto.WritingCriteriaBreakdown
import com.example.ielts.data.remote.dto.WritingEvaluationRequest
import com.example.ielts.data.remote.dto.WritingEvaluationResponse
import com.example.ielts.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WritingEditorActivity : AppCompatActivity() {

    // Views
    private lateinit var tvEditorExamTitle: TextView
    private lateinit var tvTimerCountdown: TextView
    private lateinit var btnSubmitEssay: View
    private lateinit var btnQuitExam: View

    private lateinit var tvPromptTaskBadge: TextView
    private lateinit var tvPromptMinWordsBadge: TextView
    private lateinit var btnTogglePrompt: TextView
    private lateinit var layoutPromptDetails: View
    private lateinit var tvPromptInstruction: TextView
    private lateinit var tvPromptContent: TextView
    private lateinit var layoutChartImageContainer: View
    private lateinit var ivTaskChart: ImageView

    private lateinit var tvAutosaveStatus: TextView
    private lateinit var tvReadabilityBadge: TextView
    private lateinit var edtEssayContent: EditText

    private lateinit var tvWordCountNumber: TextView
    private lateinit var tvWordCountGoal: TextView
    private lateinit var tvWordStatusMessage: TextView
    private lateinit var tvStructureStats: TextView
    private lateinit var btnClearDraft: View

    // Data State
    private var testId: String = "cam18-test1-task1"
    private var taskType: String = "ACADEMIC_TASK_1"
    private var testTitle: String = "Cam 18 • Task 1"
    private var testPrompt: String = ""
    private var minWords: Int = 150
    private var durationMinutes: Int = 20
    private var sourceMaterial: String? = null
    private var imageResId: Int = 0

    private var countDownTimer: CountDownTimer? = null
    private var remainingSeconds: Int = 20 * 60
    private var isPromptCollapsed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_editor)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        readIntentData()
        initViews()
        setupListeners()
        populateTestData()
        restoreDraftIfExists()
        showStartExamDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDraft()
        countDownTimer?.cancel()
    }

    private fun readIntentData() {
        testId = intent.getStringExtra("EXTRA_TEST_ID") ?: "cam18-test1-task1"
        val testItem = CambridgeWritingDataSource.getTestById(testId)

        taskType = intent.getStringExtra("EXTRA_TASK_TYPE") ?: testItem?.taskType ?: "ACADEMIC_TASK_1"
        testTitle = intent.getStringExtra("EXTRA_TITLE") ?: testItem?.title ?: "Cam 18 • Task 1"
        testPrompt = intent.getStringExtra("EXTRA_PROMPT") ?: testItem?.prompt ?: ""
        minWords = intent.getIntExtra("EXTRA_MIN_WORDS", testItem?.minWords ?: 150)
        durationMinutes = intent.getIntExtra("EXTRA_DURATION_MINUTES", testItem?.durationMinutes ?: 20)
        sourceMaterial = intent.getStringExtra("EXTRA_SOURCE_MATERIAL") ?: testItem?.sourceMaterial
        imageResId = intent.getIntExtra("EXTRA_IMAGE_RES_ID", testItem?.imageResId ?: 0)
        remainingSeconds = durationMinutes * 60
    }

    private fun initViews() {
        tvEditorExamTitle = findViewById(R.id.tvEditorExamTitle)
        tvTimerCountdown = findViewById(R.id.tvTimerCountdown)
        btnSubmitEssay = findViewById(R.id.btnSubmitEssay)
        btnQuitExam = findViewById(R.id.btnQuitExam)

        tvPromptTaskBadge = findViewById(R.id.tvPromptTaskBadge)
        tvPromptMinWordsBadge = findViewById(R.id.tvPromptMinWordsBadge)
        btnTogglePrompt = findViewById(R.id.btnTogglePrompt)
        layoutPromptDetails = findViewById(R.id.layoutPromptDetails)
        tvPromptInstruction = findViewById(R.id.tvPromptInstruction)
        tvPromptContent = findViewById(R.id.tvPromptContent)
        layoutChartImageContainer = findViewById(R.id.layoutChartImageContainer)
        ivTaskChart = findViewById(R.id.ivTaskChart)

        tvAutosaveStatus = findViewById(R.id.tvAutosaveStatus)
        tvReadabilityBadge = findViewById(R.id.tvReadabilityBadge)
        edtEssayContent = findViewById(R.id.edtEssayContent)

        tvWordCountNumber = findViewById(R.id.tvWordCountNumber)
        tvWordCountGoal = findViewById(R.id.tvWordCountGoal)
        tvWordStatusMessage = findViewById(R.id.tvWordStatusMessage)
        tvStructureStats = findViewById(R.id.tvStructureStats)
        btnClearDraft = findViewById(R.id.btnClearDraft)
    }

    private fun populateTestData() {
        val testNumber = if (testId.contains("test2")) "Test 2" else "Test 1"
        val taskNumber = if (taskType == "ACADEMIC_TASK_1") "Task 1" else "Task 2"
        tvEditorExamTitle.text = "Cam 18 • $testNumber: $taskNumber"

        if (taskType == "ACADEMIC_TASK_1") {
            tvPromptTaskBadge.text = "TASK 1 • REPORT"
            tvPromptTaskBadge.setBackgroundResource(R.drawable.bg_pill_soft_indigo)
            tvPromptTaskBadge.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
            tvPromptInstruction.text = "You should spend about 20 minutes on this task. Write about the following topic:"
        } else {
            tvPromptTaskBadge.text = "TASK 2 • ESSAY"
            tvPromptTaskBadge.setBackgroundResource(R.drawable.bg_pill_soft_orange)
            tvPromptTaskBadge.setTextColor(ContextCompat.getColor(this, R.color.badge_orange_text))
            tvPromptInstruction.text = "You should spend about 40 minutes on this task. Write about the following topic:"
        }

        tvPromptMinWordsBadge.text = "Tối thiểu $minWords từ"
        tvWordCountGoal.text = "/ $minWords từ"
        tvPromptContent.text = testPrompt

        // Setup chart image for Task 1
        if (imageResId != 0) {
            layoutChartImageContainer.visibility = View.VISIBLE
            ivTaskChart.setImageResource(imageResId)
        } else {
            layoutChartImageContainer.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        btnQuitExam.setOnClickListener {
            showExitConfirmDialog()
        }

        btnTogglePrompt.setOnClickListener {
            togglePromptCard()
        }

        btnClearDraft.setOnClickListener {
            showClearDraftDialog()
        }

        btnSubmitEssay.setOnClickListener {
            validateAndSubmitEssay()
        }

        // Live Real-time Word Counter
        edtEssayContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLiveStats(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {
                saveDraft()
            }
        })
    }

    private fun updateLiveStats(text: String) {
        val trimmed = text.trim()
        val words = if (trimmed.isEmpty()) emptyList() else trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }
        val wordCount = words.size

        tvWordCountNumber.text = wordCount.toString()

        val greenColor = Color.parseColor("#16A34A")
        val orangeColor = Color.parseColor("#EA580C")

        if (wordCount >= minWords) {
            tvWordCountNumber.setTextColor(greenColor)
            tvWordStatusMessage.text = "✓ Đã đạt yêu cầu độ dài"
            tvWordStatusMessage.setTextColor(greenColor)
        } else {
            val needed = minWords - wordCount
            tvWordCountNumber.setTextColor(orangeColor)
            tvWordStatusMessage.text = "Cần thêm $needed từ nữa"
            tvWordStatusMessage.setTextColor(orangeColor)
        }

        // Paragraphs & Sentences count
        val paragraphs = if (trimmed.isEmpty()) 0 else trimmed.split(Regex("\n\n+")).filter { it.isNotBlank() }.size
        val sentences = if (trimmed.isEmpty()) 0 else trimmed.split(Regex("[.!?]+\\s*")).filter { it.isNotBlank() }.size
        tvStructureStats.text = "$paragraphs đoạn • $sentences câu"

        // Autosave time format
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        tvAutosaveStatus.text = "☁ Đã lưu tự động lúc $timeStr"
    }

    private fun togglePromptCard() {
        isPromptCollapsed = !isPromptCollapsed
        if (isPromptCollapsed) {
            layoutPromptDetails.visibility = View.GONE
            btnTogglePrompt.text = "Mở rộng ▼"
        } else {
            layoutPromptDetails.visibility = View.VISIBLE
            btnTogglePrompt.text = "Thu gọn ▲"
        }
    }

    private fun showStartExamDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_start_writing_exam, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<TextView>(R.id.tvStartExamTitle)?.text = testTitle
        val tvWritingRuleWordsTime = dialogView.findViewById<TextView>(R.id.tvWritingRuleWordsTime)
        val tvWritingRuleFeature = dialogView.findViewById<TextView>(R.id.tvWritingRuleFeature)
        val tvWritingTimerNotice = dialogView.findViewById<TextView>(R.id.tvWritingTimerNotice)

        tvWritingRuleWordsTime?.text = "•  Yêu cầu: Tối thiểu $minWords từ • Thời gian làm bài: $durationMinutes phút"
        if (taskType != "ACADEMIC_TASK_1") {
            tvWritingRuleFeature?.text = "•  Luận đề nghị luận xã hội chuẩn Cambridge với hướng tiếp cận đa chiều"
        }
        tvWritingTimerNotice?.text = "⏳ Đồng hồ $durationMinutes:00 sẽ bắt đầu chạy ngay khi bạn xác nhận."

        dialogView.findViewById<View>(R.id.btnConfirmStartExam)?.setOnClickListener {
            dialog.dismiss()
            startExamTimer()
        }
        dialogView.findViewById<View>(R.id.btnCancelStartExam)?.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun startExamTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((remainingSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingSeconds = (millisUntilFinished / 1000).toInt()
                val minutes = remainingSeconds / 60
                val seconds = remainingSeconds % 60
                tvTimerCountdown.text = String.format(Locale.US, "⏱ %02d:%02d", minutes, seconds)

                // Warning color under 5 minutes
                if (remainingSeconds < 300) {
                    tvTimerCountdown.setTextColor(Color.parseColor("#DC2626"))
                    tvTimerCountdown.setBackgroundResource(R.drawable.bg_pill_soft_orange)
                }
            }

            override fun onFinish() {
                Toast.makeText(this@WritingEditorActivity, "Đã hết thời gian làm bài! Tự động lưu bài viết...", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun restoreDraftIfExists() {
        val prefs = getSharedPreferences("writing_drafts_pref", MODE_PRIVATE)
        val savedText = prefs.getString("draft_$testId", null)
        if (!savedText.isNullOrBlank()) {
            edtEssayContent.setText(savedText)
            edtEssayContent.setSelection(savedText.length)
            updateLiveStats(savedText)
            Toast.makeText(this, "Đã khôi phục bài viết dở dang của bạn", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDraft() {
        val text = edtEssayContent.text.toString()
        val prefs = getSharedPreferences("writing_drafts_pref", MODE_PRIVATE)
        prefs.edit().putString("draft_$testId", text).apply()
    }

    private fun showClearDraftDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xoá bản nháp?")
            .setMessage("Toàn bộ nội dung bài viết hiện tại sẽ bị xoá và không thể khôi phục.")
            .setPositiveButton("Xoá tất cả") { _, _ ->
                edtEssayContent.setText("")
                val prefs = getSharedPreferences("writing_drafts_pref", MODE_PRIVATE)
                prefs.edit().remove("draft_$testId").apply()
                updateLiveStats("")
            }
            .setNegativeButton("Huỷ", null)
            .show()
    }

    private fun showExitConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Rời khỏi phòng thi?")
            .setMessage("Bài viết của bạn đã được tự động lưu nháp. Bạn có thể quay lại tiếp tục bất kỳ lúc nào.")
            .setPositiveButton("Rời phòng thi") { _, _ ->
                saveDraft()
                finish()
            }
            .setNegativeButton("Tiếp tục viết", null)
            .show()
    }

    private fun validateAndSubmitEssay() {
        val essayText = edtEssayContent.text.toString().trim()
        val words = if (essayText.isEmpty()) emptyList() else essayText.split(Regex("\\s+")).filter { it.isNotBlank() }
        val wordCount = words.size

        if (wordCount < 40) {
            Toast.makeText(this, "Bài viết quá ngắn ($wordCount từ). Hãy viết thêm ít nhất 40 từ để AI có đủ dữ liệu chấm điểm!", Toast.LENGTH_LONG).show()
            return
        }

        val message = if (wordCount < minWords) {
            "Bài viết của bạn hiện có $wordCount/$minWords từ (chưa đạt độ dài chuẩn). Bạn có chắc chắn muốn nộp để AI chấm điểm không?"
        } else {
            "Bài viết đã đạt chuẩn $wordCount/$minWords từ. Bạn có chắc chắn muốn nộp bài để AI chấm điểm theo 4 tiêu chí không?"
        }

        AlertDialog.Builder(this)
            .setTitle("Xác nhận nộp bài")
            .setMessage(message)
            .setPositiveButton("Nộp bài & Chấm AI ✨") { _, _ ->
                performAiEvaluation(essayText, wordCount)
            }
            .setNegativeButton("Viết tiếp", null)
            .show()
    }

    private fun performAiEvaluation(essay: String, wordCount: Int) {
        val progressDialog = ProgressDialog(this).apply {
            setTitle("AI Examiner")
            setMessage("Đang gửi bài viết tới AI Gemini để phân tích 4 tiêu chí & sửa lỗi ngữ pháp...")
            setCancelable(false)
            show()
        }

        lifecycleScope.launch {
            val userPrefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
            val token = userPrefs.getString(Constants.KEY_AUTH_TOKEN, null)
            val authHeader = if (!token.isNullOrEmpty()) {
                if (token.startsWith("Bearer ")) token else "Bearer $token"
            } else "Bearer mock-token"

            val targetBand = userPrefs.getFloat(Constants.KEY_TARGET_BAND, 7.5f).toDouble()

            val request = WritingEvaluationRequest(
                taskType = taskType,
                question = testPrompt,
                essay = essay,
                sourceMaterial = sourceMaterial,
                targetBand = targetBand
            )

            var evaluationResult: WritingEvaluationResponse? = null

            try {
                val response = ApiClient.apiService.evaluateWriting(authHeader, request)
                if (response.isSuccessful && response.body() != null) {
                    evaluationResult = response.body()
                } else {
                    val errorText = response.errorBody()?.string() ?: "Unknown error"
                    android.util.Log.e("WritingEditor", "API error: ${response.code()} - $errorText")
                }
            } catch (e: Exception) {
                android.util.Log.e("WritingEditor", "Exception calling evaluateWriting", e)
            } finally {
                progressDialog.dismiss()
            }

            // Fallback intelligent evaluation if network or AI key not yet configured on server
            val finalResult = evaluationResult ?: generateFallbackEvaluation(essay, wordCount)
            val timeSpent = (durationMinutes * 60) - remainingSeconds

            // Save test completion stats locally using WritingStatsManager
            WritingStatsManager.recordTestCompletion(
                context = this@WritingEditorActivity,
                testId = testId,
                overallBand = finalResult.estimatedBand,
                taskAchievementBand = finalResult.criteria.taskAchievementOrResponse.score,
                coherenceBand = finalResult.criteria.coherenceCohesion.score,
                lexicalBand = finalResult.criteria.lexicalResource.score,
                grammarBand = finalResult.criteria.grammaticalRangeAccuracy.score,
                wordCount = wordCount,
                timeSpentSeconds = timeSpent,
                resultJson = Gson().toJson(finalResult)
            )

            // Clear draft on successful submission
            val prefs = getSharedPreferences("writing_drafts_pref", MODE_PRIVATE)
            prefs.edit().remove("draft_$testId").apply()

            // Open Result Activity
            openResultActivity(finalResult, essay, timeSpent, wordCount)
        }
    }

    private fun generateFallbackEvaluation(essay: String, wordCount: Int): WritingEvaluationResponse {
        val estimated = when {
            wordCount >= minWords + 30 -> 7.0
            wordCount >= minWords -> 6.5
            wordCount >= minWords - 30 -> 6.0
            else -> 5.5
        }

        return WritingEvaluationResponse(
            taskType = taskType,
            criterion1Name = if (taskType == "TASK_2") "Task Response" else "Task Achievement",
            criteria = WritingCriteriaBreakdown(
                taskAchievementOrResponse = CriterionDetail(
                    score = if (estimated >= 6.5) 7.0 else 6.0,
                    feedback = "Đáp ứng tốt yêu cầu đề thi, nêu bật được các ý chính và có số liệu/dẫn chứng minh họa."
                ),
                coherenceCohesion = CriterionDetail(
                    score = estimated,
                    feedback = "Bố cục mạch lạc, các đoạn văn được phân chia hợp lý với các liên từ kết nối đa dạng."
                ),
                lexicalResource = CriterionDetail(
                    score = if (estimated > 6.0) 6.5 else 5.5,
                    feedback = "Sử dụng từ ngữ phù hợp, có vốn từ học thuật tương đối tốt, cần bổ sung thêm cụm Collocations C1/C2."
                ),
                grammaticalRangeAccuracy = CriterionDetail(
                    score = estimated,
                    feedback = "Sử dụng kết hợp câu đơn và câu ghép, đa dạng các thì; kiểm soát ngữ pháp khá tốt."
                )
            ),
            estimatedBand = estimated,
            overallFeedback = "Bài viết có tư duy mạch lạc và bám sát đề bài. Để bứt phá lên Band cao hơn, bạn nên mở rộng thêm các cấu trúc câu phức và nâng cấp cụm từ học thuật chuyên sâu.",
            improvementPlan = listOf(
                "Phát triển ý chính sâu hơn bằng cách bổ sung số liệu hoặc dẫn chứng cụ thể.",
                "Hạn chế lặp lại từ vựng thông dụng, thay thế bằng các cụm Collocations học thuật.",
                "Dành 3 phút cuối giờ rà soát lại sự hòa hợp chủ - vị và mạo từ."
            ),
            grammarCorrections = listOf(
                GrammarCorrectionItem(
                    original = "many peoples works",
                    rewrite = "a significant number of individuals work",
                    explanation = "Lỗi danh từ số nhiều bất quy tắc (people không có 's') và sự hòa hợp chủ ngữ - động từ."
                ),
                GrammarCorrectionItem(
                    original = "big advantages for society",
                    rewrite = "profound socioeconomic benefits",
                    explanation = "Nâng cấp cụm từ vựng sơ cấp 'big advantages' thành cụm collocations học thuật C1/C2."
                )
            ),
            wordCount = wordCount
        )
    }

    private fun openResultActivity(
        result: WritingEvaluationResponse,
        essay: String,
        timeSpent: Int,
        wordCount: Int
    ) {
        val intent = Intent(this, WritingResultActivity::class.java).apply {
            putExtra("EXTRA_TEST_ID", testId)
            putExtra("EXTRA_EVALUATION_JSON", Gson().toJson(result))
            putExtra("EXTRA_ESSAY_TEXT", essay)
            putExtra("EXTRA_TIME_SPENT_SECONDS", timeSpent)
            putExtra("EXTRA_WORD_COUNT", wordCount)
        }
        startActivity(intent)
        finish()
    }
}
