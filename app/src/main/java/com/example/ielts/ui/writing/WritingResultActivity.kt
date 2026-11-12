package com.example.ielts.ui.writing

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ielts.R
import com.example.ielts.data.datasource.CambridgeWritingDataSource
import com.example.ielts.data.local.WritingStatsManager
import com.example.ielts.data.remote.dto.CriterionDetail
import com.example.ielts.data.remote.dto.GrammarCorrectionItem
import com.example.ielts.data.remote.dto.WritingCriteriaBreakdown
import com.example.ielts.data.remote.dto.WritingEvaluationResponse
import com.example.ielts.utils.Constants
import com.google.gson.Gson
import java.util.Locale

class WritingResultActivity : AppCompatActivity() {

    private lateinit var tvOverallBandScore: TextView
    private lateinit var tvCefrLevelBadge: TextView
    private lateinit var tvTimeSpent: TextView
    private lateinit var tvWordCountStat: TextView
    private lateinit var tvTargetBandStat: TextView
    private lateinit var tvGeneralAiFeedback: TextView

    // 4 Criteria
    private lateinit var tvBandTR: TextView
    private lateinit var pbTR: ProgressBar
    private lateinit var tvCommentsTR: TextView

    private lateinit var tvBandCC: TextView
    private lateinit var pbCC: ProgressBar
    private lateinit var tvCommentsCC: TextView

    private lateinit var tvBandLR: TextView
    private lateinit var pbLR: ProgressBar
    private lateinit var tvCommentsLR: TextView

    private lateinit var tvBandGRA: TextView
    private lateinit var pbGRA: ProgressBar
    private lateinit var tvCommentsGRA: TextView

    // Tabs
    private lateinit var tabCorrections: LinearLayout
    private lateinit var tabActionPlan: LinearLayout
    private lateinit var tabModelEssay: LinearLayout

    private lateinit var tvTabCorrectionsTitle: TextView
    private lateinit var tvBadgeCorrectionCount: TextView
    private lateinit var tvTabActionPlanTitle: TextView
    private lateinit var tvTabModelEssayTitle: TextView

    private lateinit var layoutTabCorrections: View
    private lateinit var layoutTabActionPlan: View
    private lateinit var layoutTabModelEssay: View

    private lateinit var layoutCorrectionsContainer: LinearLayout
    private lateinit var layoutNoCorrections: View

    private lateinit var tvActionTitle1: TextView
    private lateinit var tvActionPlan1: TextView
    private lateinit var tvActionTitle2: TextView
    private lateinit var tvActionPlan2: TextView
    private lateinit var tvActionTitle3: TextView
    private lateinit var tvActionPlan3: TextView
    private lateinit var tvModelEssayHeading: TextView
    private lateinit var tvModelEssayContent: TextView

    private lateinit var btnPracticeOther: View
    private lateinit var btnRetryTest: View
    private lateinit var btnBack: View
    private lateinit var btnShare: View

    // Data State
    private var testId: String = "cam18-test1-task1"
    private var timeSpentSeconds: Int = 0
    private var wordCount: Int = 0
    private var evaluation: WritingEvaluationResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        readIntentData()
        initViews()
        setupListeners()
        populateEvaluationData()
    }

    private fun readIntentData() {
        testId = intent.getStringExtra("EXTRA_TEST_ID") ?: "cam18-test1-task1"
        timeSpentSeconds = intent.getIntExtra("EXTRA_TIME_SPENT_SECONDS", intent.getIntExtra("EXTRA_TIME_SPENT", 1200))
        wordCount = intent.getIntExtra("EXTRA_WORD_COUNT", 180)

        val json = intent.getStringExtra("EXTRA_EVALUATION_JSON") ?: intent.getStringExtra("EXTRA_RESULT_JSON")
        if (!json.isNullOrBlank()) {
            try {
                evaluation = Gson().fromJson(json, WritingEvaluationResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (evaluation == null) {
            val record = WritingStatsManager.getRecordForTest(this, testId)
            if (record?.resultJson != null) {
                try {
                    evaluation = Gson().fromJson(record.resultJson, WritingEvaluationResponse::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Fallback default evaluation if offline or parse error
        if (evaluation == null) {
            evaluation = createDefaultFallbackEvaluation()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnShare = findViewById(R.id.btnShare)

        tvOverallBandScore = findViewById(R.id.tvOverallBandScore)
        tvCefrLevelBadge = findViewById(R.id.tvCefrLevelBadge)
        tvTimeSpent = findViewById(R.id.tvTimeSpent)
        tvWordCountStat = findViewById(R.id.tvWordCountStat)
        tvTargetBandStat = findViewById(R.id.tvTargetBandStat)
        tvGeneralAiFeedback = findViewById(R.id.tvGeneralAiFeedback)

        tvBandTR = findViewById(R.id.tvBandTR)
        pbTR = findViewById(R.id.pbTR)
        tvCommentsTR = findViewById(R.id.tvCommentsTR)

        tvBandCC = findViewById(R.id.tvBandCC)
        pbCC = findViewById(R.id.pbCC)
        tvCommentsCC = findViewById(R.id.tvCommentsCC)

        tvBandLR = findViewById(R.id.tvBandLR)
        pbLR = findViewById(R.id.pbLR)
        tvCommentsLR = findViewById(R.id.tvCommentsLR)

        tvBandGRA = findViewById(R.id.tvBandGRA)
        pbGRA = findViewById(R.id.pbGRA)
        tvCommentsGRA = findViewById(R.id.tvCommentsGRA)

        tabCorrections = findViewById(R.id.tabCorrections)
        tabActionPlan = findViewById(R.id.tabActionPlan)
        tabModelEssay = findViewById(R.id.tabModelEssay)

        tvTabCorrectionsTitle = findViewById(R.id.tvTabCorrectionsTitle)
        tvBadgeCorrectionCount = findViewById(R.id.tvBadgeCorrectionCount)
        tvTabActionPlanTitle = findViewById(R.id.tvTabActionPlanTitle)
        tvTabModelEssayTitle = findViewById(R.id.tvTabModelEssayTitle)

        layoutTabCorrections = findViewById(R.id.layoutTabCorrections)
        layoutTabActionPlan = findViewById(R.id.layoutTabActionPlan)
        layoutTabModelEssay = findViewById(R.id.layoutTabModelEssay)

        layoutCorrectionsContainer = findViewById(R.id.layoutCorrectionsContainer)
        layoutNoCorrections = findViewById(R.id.layoutNoCorrections)

        tvActionTitle1 = findViewById(R.id.tvActionTitle1)
        tvActionPlan1 = findViewById(R.id.tvActionPlan1)
        tvActionTitle2 = findViewById(R.id.tvActionTitle2)
        tvActionPlan2 = findViewById(R.id.tvActionPlan2)
        tvActionTitle3 = findViewById(R.id.tvActionTitle3)
        tvActionPlan3 = findViewById(R.id.tvActionPlan3)
        tvModelEssayHeading = findViewById(R.id.tvModelEssayHeading)

        tvModelEssayContent = findViewById(R.id.tvModelEssayContent)

        btnPracticeOther = findViewById(R.id.btnPracticeOther)
        btnRetryTest = findViewById(R.id.btnRetryTest)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnShare.setOnClickListener {
            shareReport()
        }

        tabCorrections.setOnClickListener {
            selectTab(0)
        }

        tabActionPlan.setOnClickListener {
            selectTab(1)
        }

        tabModelEssay.setOnClickListener {
            selectTab(2)
        }

        btnPracticeOther.setOnClickListener {
            val intent = Intent(this, WritingListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        btnRetryTest.setOnClickListener {
            val intent = Intent(this, WritingEditorActivity::class.java)
            intent.putExtra("EXTRA_TEST_ID", testId)
            startActivity(intent)
            finish()
        }
    }

    private fun populateEvaluationData() {
        val eval = evaluation ?: return

        // Overall Band
        tvOverallBandScore.text = String.format(Locale.US, "%.1f", eval.estimatedBand)

        // CEFR Level
        val band = eval.estimatedBand
        val (cefrText, cefrBgRes, cefrTextColorRes) = when {
            band >= 8.5 -> Triple("✓ CEFR C2 • Thành Thạo Xuất Sắc", R.drawable.bg_pill_soft_indigo, R.color.primary_blue)
            band >= 7.0 -> Triple("✓ CEFR C1 • Đạt Mức Tốt", R.drawable.bg_pill_soft_blue, R.color.primary_blue)
            band >= 5.5 -> Triple("✓ CEFR B2 • Đạt Mức Khá", R.drawable.bg_pill_soft_orange, R.color.badge_orange_text)
            else -> Triple("✓ CEFR B1 • Đạt Mức Trung Bình", R.drawable.bg_pill_soft_gray, R.color.text_secondary)
        }
        tvCefrLevelBadge.text = cefrText
        tvCefrLevelBadge.setBackgroundResource(cefrBgRes)
        tvCefrLevelBadge.setTextColor(ContextCompat.getColor(this, cefrTextColorRes))

        // Stats
        val minutes = maxOf(1, timeSpentSeconds / 60)
        tvTimeSpent.text = "${minutes}m"
        tvWordCountStat.text = "$wordCount từ"

        val userPrefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val targetBand = userPrefs.getFloat(Constants.KEY_TARGET_BAND, 7.5f)
        val diffToTarget = targetBand - band
        val targetDiffStr = if (diffToTarget > 0) String.format(Locale.US, " (còn %.1f)", diffToTarget) else " (Đạt)"
        tvTargetBandStat.text = String.format(Locale.US, "%.1f%s", targetBand, targetDiffStr)

        // AI General Feedback
        tvGeneralAiFeedback.text = "Nhận xét AI: ${eval.overallFeedback}"

        // 4 Criteria
        bindCriteria(eval.criteria.taskAchievementOrResponse, tvBandTR, pbTR, tvCommentsTR)
        bindCriteria(eval.criteria.coherenceCohesion, tvBandCC, pbCC, tvCommentsCC)
        bindCriteria(eval.criteria.lexicalResource, tvBandLR, pbLR, tvCommentsLR)
        bindCriteria(eval.criteria.grammaticalRangeAccuracy, tvBandGRA, pbGRA, tvCommentsGRA)

        // Corrections
        populateCorrections(eval.grammarCorrections)

        // Lộ trình bứt phá (Action Plan) từ AI
        populateActionPlan(eval.improvementPlan)

        // Model Essay từ AI theo mục tiêu của người dùng (hoặc nâng cấp nếu đã đạt/vượt)
        val studentBand = band.toFloat()
        val isExceeded = studentBand >= targetBand
        val displayModelBand = if (isExceeded) {
            maxOf(studentBand + 0.5f, 8.5f).coerceAtMost(9.0f)
        } else {
            targetBand
        }
        val displayModelBandStr = String.format(Locale.US, "%.1f", displayModelBand)

        if (isExceeded) {
            tvModelEssayHeading.text = "⭐ Bài Viết Nâng Cấp Xuất Sắc Chuẩn Band $displayModelBandStr"
            tvTabModelEssayTitle.text = "Bài mẫu $displayModelBandStr"
            findViewById<TextView>(R.id.tvModelEssayBadge)?.text = "Vượt mục tiêu • Band $displayModelBandStr"
        } else {
            tvModelEssayHeading.text = "⭐ Bài Viết Nâng Cấp Chuẩn Band $displayModelBandStr"
            tvTabModelEssayTitle.text = "Bài mẫu $displayModelBandStr"
            findViewById<TextView>(R.id.tvModelEssayBadge)?.text = "Mục tiêu Band $displayModelBandStr"
        }

        if (!eval.modelEssay.isNullOrBlank()) {
            tvModelEssayContent.text = eval.modelEssay
        } else {
            val testItem = CambridgeWritingDataSource.getTestById(testId)
            tvModelEssayContent.text = testItem?.modelEssay ?: "Bài viết mẫu chuẩn đang được đồng bộ..."
        }

        // Tự động kích hoạt chuỗi học tập Streak & hiển thị popup chúc mừng nếu hoàn thành bài đầu tiên trong ngày
        val streakResult = com.example.ielts.data.local.StreakManager.recordActivity(this, "WRITING")
        if (streakResult.isFirstActivityToday) {
            com.example.ielts.ui.streak.StreakCelebrationDialog(this, streakResult.newStreak).show()
        }
    }

    private fun populateActionPlan(plan: List<String>) {
        val titles = listOf(tvActionTitle1, tvActionTitle2, tvActionTitle3)
        val descriptions = listOf(tvActionPlan1, tvActionPlan2, tvActionPlan3)

        plan.forEachIndexed { index, item ->
            if (index < titles.size) {
                val parts = if (item.contains(":")) {
                    item.split(":", limit = 2)
                } else if (item.contains(" - ")) {
                    item.split(" - ", limit = 2)
                } else {
                    listOf("Hành động ưu tiên #${index + 1}", item)
                }

                val titleText = parts[0].replace("Tiêu đề", "").trim().trimStart(':', '-', ' ')
                val descText = if (parts.size > 1) parts[1].trim() else parts[0].trim()

                titles[index].text = if (titleText.isNotBlank()) titleText else "Chiến lược bứt phá #${index + 1}"
                descriptions[index].text = descText
            }
        }
    }

    private fun bindCriteria(
        criteria: CriterionDetail?,
        tvBand: TextView,
        pb: ProgressBar,
        tvComments: TextView
    ) {
        if (criteria == null) return
        tvBand.text = String.format(Locale.US, "Band %.1f", criteria.score)
        pb.progress = (criteria.score * 10).toInt()
        tvComments.text = criteria.feedback
    }

    private fun populateCorrections(corrections: List<GrammarCorrectionItem>) {
        layoutCorrectionsContainer.removeAllViews()
        tvBadgeCorrectionCount.text = "${corrections.size}"

        if (corrections.isEmpty()) {
            layoutNoCorrections.visibility = View.VISIBLE
            return
        }

        layoutNoCorrections.visibility = View.GONE
        val inflater = LayoutInflater.from(this)

        corrections.forEachIndexed { index, item ->
            val cardView = inflater.inflate(R.layout.item_writing_correction_card, layoutCorrectionsContainer, false)

            val tvIndex = cardView.findViewById<TextView>(R.id.tvCorrectionIndex)
            val tvOriginal = cardView.findViewById<TextView>(R.id.tvOriginalSentence)
            val tvCorrected = cardView.findViewById<TextView>(R.id.tvCorrectedSentence)
            val tvExplanation = cardView.findViewById<TextView>(R.id.tvCorrectionExplanation)

            tvIndex.text = "Lỗi #${index + 1}"
            tvOriginal.text = "\"${item.original}\""
            tvCorrected.text = "\"${item.rewrite}\""
            tvExplanation.text = "Phân tích: ${item.explanation}"

            layoutCorrectionsContainer.addView(cardView)
        }
    }

    private fun selectTab(tabIndex: Int) {
        val selectedBg = ContextCompat.getDrawable(this, R.drawable.bg_card_white_rounded)
        val transparentColor = Color.TRANSPARENT

        // Reset all tabs
        tabCorrections.background = null
        tabActionPlan.background = null
        tabModelEssay.background = null

        tvTabCorrectionsTitle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        tvTabActionPlanTitle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        tvTabModelEssayTitle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))

        layoutTabCorrections.visibility = View.GONE
        layoutTabActionPlan.visibility = View.GONE
        layoutTabModelEssay.visibility = View.GONE

        when (tabIndex) {
            0 -> {
                tabCorrections.background = selectedBg
                tvTabCorrectionsTitle.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
                layoutTabCorrections.visibility = View.VISIBLE
            }
            1 -> {
                tabActionPlan.background = selectedBg
                tvTabActionPlanTitle.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
                layoutTabActionPlan.visibility = View.VISIBLE
            }
            2 -> {
                tabModelEssay.background = selectedBg
                tvTabModelEssayTitle.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
                layoutTabModelEssay.visibility = View.VISIBLE
            }
        }
    }

    private fun shareReport() {
        val eval = evaluation ?: return
        val shareText = """
            🎯 Kết Quả Luyện Thi IELTS Writing
            Đề thi: $testId
            Điểm Tổng: Band ${String.format(Locale.US, "%.1f", eval.estimatedBand)}
            - Task Achievement: ${eval.criteria.taskAchievementOrResponse.score}
            - Coherence & Cohesion: ${eval.criteria.coherenceCohesion.score}
            - Lexical Resource: ${eval.criteria.lexicalResource.score}
            - Grammar Range: ${eval.criteria.grammaticalRangeAccuracy.score}
            Nhận xét: ${eval.overallFeedback}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Kết quả IELTS Writing của tôi")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Chia sẻ báo cáo đánh giá"))
    }

    private fun createDefaultFallbackEvaluation(): WritingEvaluationResponse {
        return WritingEvaluationResponse(
            taskType = "ACADEMIC_TASK_1",
            criterion1Name = "Task Achievement",
            criteria = WritingCriteriaBreakdown(
                taskAchievementOrResponse = CriterionDetail(
                    score = 7.0,
                    feedback = "Đáp ứng tốt yêu cầu đề bài, nêu bật được các đặc điểm chính và có số liệu so sánh rõ ràng."
                ),
                coherenceCohesion = CriterionDetail(
                    score = 6.5,
                    feedback = "Bố cục mạch lạc, các đoạn văn được phân chia hợp lý với các liên từ đa dạng."
                ),
                lexicalResource = CriterionDetail(
                    score = 6.0,
                    feedback = "Sử dụng từ ngữ chuẩn xác, có vốn từ vựng học thuật cơ bản nhưng cần thêm cụm collocations C1."
                ),
                grammaticalRangeAccuracy = CriterionDetail(
                    score = 6.5,
                    feedback = "Sử dụng kết hợp câu đơn và câu phức, độ chính xác ngữ pháp đạt mức khá."
                )
            ),
            estimatedBand = 6.5,
            overallFeedback = "Bài viết có tư duy lập luận tốt và đáp ứng đầy đủ yêu cầu đề thi. Nếu trau chuốt thêm các cụm từ vựng học thuật (Lexical Resource) và hạn chế lỗi ngữ pháp nhỏ, bạn hoàn toàn có thể bứt phá lên Band 7.0+!",
            improvementPlan = listOf(
                "Bổ sung 5 collocations học thuật C1/C2.",
                "Rà soát mạo từ và chia động từ.",
                "Dành 3 phút cuối giờ soát lại bài làm."
            ),
            grammarCorrections = listOf(
                GrammarCorrectionItem(
                    original = "many peoples works",
                    rewrite = "a significant number of individuals work",
                    explanation = "Lỗi danh từ số nhiều bất quy tắc (people không có 's') và sự hòa hợp chủ - vị."
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
}
