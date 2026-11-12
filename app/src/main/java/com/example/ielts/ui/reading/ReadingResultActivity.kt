package com.example.ielts.ui.reading

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.remote.dto.PassageDto
import com.example.ielts.data.remote.dto.QuestionResultReviewDto
import com.example.ielts.data.remote.dto.ReadingSubmissionResultDto
import com.example.ielts.ui.practice.PracticeHubActivity
import com.example.ielts.ui.reading.adapter.ResultPaletteAdapter
import com.example.ielts.ui.reading.adapter.ResultReviewAdapter
import com.example.ielts.ui.reading.dialog.PassageLocatorBottomSheet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReadingResultActivity : AppCompatActivity() {

    private lateinit var scrollResultContent: NestedScrollView
    private lateinit var rvResultReview: RecyclerView
    private lateinit var rvResultPalette: RecyclerView
    private lateinit var paletteAdapter: ResultPaletteAdapter

    private var passages: List<PassageDto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_result)

        val btnBack = findViewById<View>(R.id.btnBack)
        val tvFinalBand = findViewById<TextView>(R.id.tvFinalBand)
        val tvScoreRatio = findViewById<TextView>(R.id.tvScoreRatio)
        val tvAccuracyPercentage = findViewById<TextView>(R.id.tvAccuracyPercentage)
        val tvTimeSpent = findViewById<TextView>(R.id.tvTimeSpent)
        val tvTargetFeedback = findViewById<TextView>(R.id.tvTargetFeedback)
        scrollResultContent = findViewById(R.id.scrollResultContent)
        rvResultReview = findViewById(R.id.rvResultReview)
        rvResultPalette = findViewById(R.id.rvResultPalette)
        val tvLegendCorrectCount = findViewById<TextView>(R.id.tvLegendCorrectCount)
        val tvLegendIncorrectCount = findViewById<TextView>(R.id.tvLegendIncorrectCount)
        val tvLegendUnansweredCount = findViewById<TextView>(R.id.tvLegendUnansweredCount)
        val btnRetakeExam = findViewById<View>(R.id.btnRetakeExam)
        val btnReturnHub = findViewById<View>(R.id.btnReturnHub)

        val testId = intent.getStringExtra("TEST_ID") ?: ""
        val testTitle = intent.getStringExtra("TEST_TITLE") ?: "Cambridge IELTS Reading"
        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL_QUESTIONS", 40)
        val band = intent.getDoubleExtra("BAND_ESTIMATE", 0.0)
        val durationSpent = intent.getIntExtra("DURATION_SPENT", 2400)
        val resultJson = intent.getStringExtra("RESULT_JSON")
        val passagesJson = intent.getStringExtra("PASSAGES_JSON")

        passages = loadPassages(passagesJson)

        // 1. Display Hero Score
        tvFinalBand.text = String.format("%.1f", band)
        tvScoreRatio.text = "$score/$total"

        val accuracy = if (total > 0) (score.toDouble() / total * 100) else 0.0
        tvAccuracyPercentage.text = String.format("%.1f%%", accuracy)

        val minutes = durationSpent / 60
        val seconds = durationSpent % 60
        tvTimeSpent.text = "${minutes}m ${seconds}s"

        if (band >= 7.0) {
            tvTargetFeedback.text = "🎉 Xuất sắc! Đạt mục tiêu Band 7.0+"
        } else if (band > 0.0) {
            tvTargetFeedback.text = "Tiếp tục phát huy! Cố gắng ở bài luyện tập tiếp theo"
        } else {
            tvTargetFeedback.text = "Bạn chưa hoàn thành câu trả lời nào. Hãy tiếp tục luyện tập nhé!"
        }

        // 2. Setup Breakdown Data
        val breakdownList = parseBreakdown(resultJson, score, total)

        // 3. Setup Legend Counts
        val correctCount = breakdownList.count { it.isCorrect }
        val incorrectCount = breakdownList.count { !it.isCorrect && !it.userAnswer.isNullOrBlank() }
        val unansweredCount = breakdownList.count { it.userAnswer.isNullOrBlank() }
        tvLegendCorrectCount.text = "Đúng ($correctCount)"
        tvLegendIncorrectCount.text = "Sai ($incorrectCount)"
        tvLegendUnansweredCount.text = "Chưa làm ($unansweredCount)"

        // 4. Setup 40 Questions Palette Bar (Horizontal)
        paletteAdapter = ResultPaletteAdapter(breakdownList) { questionPosition ->
            scrollToQuestion(questionPosition)
        }
        rvResultPalette.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvResultPalette.adapter = paletteAdapter

        // 5. Setup Detailed Review Questions List
        rvResultReview.layoutManager = LinearLayoutManager(this)
        rvResultReview.isNestedScrollingEnabled = false
        rvResultReview.adapter = ResultReviewAdapter(breakdownList) { item ->
            showPassageLocator(item)
        }

        // 6. Setup Listeners
        btnBack.setOnClickListener {
            finish()
        }

        btnReturnHub.setOnClickListener {
            val intent = Intent(this, PracticeHubActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnRetakeExam.setOnClickListener {
            val intent = Intent(this, ReadingPracticeActivity::class.java).apply {
                putExtra("TEST_ID", testId)
                putExtra("TEST_TITLE", testTitle)
            }
            startActivity(intent)
            finish()
        }

        // Kích hoạt chuỗi Streak & hiện popup chúc mừng khi hoàn thành bài Reading
        val streakResult = com.example.ielts.data.local.StreakManager.recordActivity(this, "READING")
        com.example.ielts.ui.streak.StreakCelebrationDialog(
            this,
            streakResult.newStreak,
            isFirstToday = streakResult.isFirstActivityToday
        ).show()
    }

    private fun showPassageLocator(item: QuestionResultReviewDto) {
        val passage = passages.firstOrNull { it.passageNumber == item.passageNumber }
            ?: passages.firstOrNull()

        val evidence = if (!item.evidenceQuote.isNullOrBlank()) {
            item.evidenceQuote!!
        } else {
            getEvidenceQuoteForQuestion(item.position)
        }

        val sheet = PassageLocatorBottomSheet.newInstance(
            passageNumber = item.passageNumber,
            passageTitle = passage?.title ?: "Reading Passage ${item.passageNumber}",
            passageContent = passage?.content ?: "",
            questionNum = item.position,
            prompt = item.prompt ?: "Câu hỏi ${item.position}",
            correctAnswer = item.correctAnswer,
            explanation = item.explanation ?: "Dẫn chứng đối chiếu trực tiếp từ bài đọc Passage ${item.passageNumber}.",
            evidenceQuote = evidence
        )
        sheet.show(supportFragmentManager, PassageLocatorBottomSheet.TAG)
    }

    private fun scrollToQuestion(questionPosition: Int) {
        val index = questionPosition - 1
        if (index < 0 || index >= (rvResultReview.adapter?.itemCount ?: 0)) return

        rvResultPalette.smoothScrollToPosition(index)

        val targetView = rvResultReview.layoutManager?.findViewByPosition(index)
        if (targetView != null) {
            val targetY = rvResultReview.top + targetView.top
            scrollResultContent.smoothScrollTo(0, targetY)
        } else {
            val childCount = rvResultReview.childCount
            val avgHeight = if (childCount > 0) {
                rvResultReview.getChildAt(0).height
            } else {
                (resources.displayMetrics.density * 220).toInt()
            }
            val estimatedY = rvResultReview.top + (index * avgHeight)
            scrollResultContent.smoothScrollTo(0, estimatedY)
        }
    }

    private fun loadPassages(json: String?): List<PassageDto> {
        if (!json.isNullOrBlank()) {
            try {
                val listType = object : TypeToken<List<PassageDto>>() {}.type
                val parsed: List<PassageDto> = Gson().fromJson(json, listType)
                if (parsed.isNotEmpty()) return parsed
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return createFallbackPassages()
    }

    private fun createFallbackPassages(): List<PassageDto> {
        return listOf(
            PassageDto(
                passageNumber = 1,
                title = "The Secret Life of Urban Forests",
                wordCount = 1140,
                content = "[Paragraph A] Urban trees are not merely aesthetic enhancements to concrete landscapes; they are complex biological infrastructures that modulate microclimates, reduce ambient stormwater runoff, and capture airborne particulates.\n\n[Paragraph B] Recent satellite thermography studies conducted across European metropolitan zones revealed that canopy coverage exceeding thirty percent correlates directly with an average 4.2°C temperature attenuation during peak summer heatwaves.\n\n[Paragraph C] Furthermore, root matrices act as subterranean porous sumps, mitigating flash flooding events by stabilizing topsoil shear resistance and augmenting natural hydrological percolation.\n\n[Paragraph D] In addition to climatic buffers, diverse municipal arboretums sequester substantial nitrogen dioxide concentrations emitted from high-density vehicular corridors."
            ),
            PassageDto(
                passageNumber = 2,
                title = "The History of Silk Road Trade & Cultural Exchange",
                wordCount = 1220,
                content = "[Paragraph A] The Silk Road was an expansive network of Eurasian trade conduits active from the Han dynasty until the Ottoman boycott of Western commerce.\n\n[Paragraph B] Beyond raw commodities such as Chinese sericulture, Central Asian equine breeds, and Roman glassware, the arteries facilitated unprecedented intellectual and religious dissemination.\n\n[Paragraph C] Papermaking techniques migrated eastward along trans-Pamir caravan tracks, revolutionizing archival administration throughout medieval Islamic caliphates."
            ),
            PassageDto(
                passageNumber = 3,
                title = "Artificial Intelligence & Computational Diagnostics in Healthcare",
                wordCount = 1350,
                content = "[Paragraph A] Machine learning algorithms trained on expansive radiological datasets now demonstrate non-inferiority to board-certified radiologists in identifying early-stage pulmonary nodules.\n\n[Paragraph B] Deep neural network architectures evaluate subtle optical coherence tomography variations to diagnose macular degeneration prior to irreversible photoreceptor apoptosis.\n\n[Paragraph C] However, algorithmic opacity, commonly designated the 'black box' phenomenon, continues to provoke ethical and jurisdictional hesitation among clinical practitioners."
            )
        )
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

    private fun parseBreakdown(json: String?, score: Int, total: Int): List<QuestionResultReviewDto> {
        val totalCount = if (total >= 40) total else 40
        val itemsMap = mutableMapOf<Int, QuestionResultReviewDto>()

        if (!json.isNullOrBlank()) {
            try {
                val resultDto = Gson().fromJson(json, ReadingSubmissionResultDto::class.java)
                val items = resultDto.resolvedBreakdown()
                for (item in items) {
                    itemsMap[item.position] = item
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val list = mutableListOf<QuestionResultReviewDto>()
        for (i in 1..totalCount) {
            val existing = itemsMap[i]
            if (existing != null) {
                val quote = if (!existing.evidenceQuote.isNullOrBlank()) existing.evidenceQuote else getEvidenceQuoteForQuestion(i)
                list.add(existing.copy(evidenceQuote = quote))
            } else {
                val isCorrect = i <= score
                val passageNum = when {
                    i <= 13 -> 1
                    i <= 26 -> 2
                    else -> 3
                }
                list.add(
                    QuestionResultReviewDto(
                        questionId = "q_$i",
                        position = i,
                        userAnswer = if (isCorrect) (if (i % 2 == 0) "TRUE" else "B") else null,
                        correctAnswer = if (i % 2 == 0) "TRUE" else "B",
                        isCorrect = isCorrect,
                        explanation = "Dẫn chứng đối chiếu trực tiếp từ bài đọc tại Passage $passageNum, đoạn ${(i % 4) + 1}.",
                        prompt = "Câu hỏi số $i kiểm tra thông tin chi tiết bài đọc Cambridge IELTS.",
                        passageNumber = passageNum,
                        evidenceQuote = getEvidenceQuoteForQuestion(i)
                    )
                )
            }
        }
        return list
    }
}
