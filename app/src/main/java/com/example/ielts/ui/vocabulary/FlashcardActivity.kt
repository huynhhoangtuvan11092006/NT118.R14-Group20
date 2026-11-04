package com.example.ielts.ui.vocabulary

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.R
import com.example.ielts.data.datasource.VocabularyDataSource
import com.example.ielts.data.datasource.VocabularyWord
import com.example.ielts.data.local.SrsGrade
import com.example.ielts.data.local.VocabularyProgressManager
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.abs

/**
 * Phiên học flashcard:
 * - Hiển thị loại từ rõ ràng (Danh từ, Động từ, Tính từ...)
 * - Các nút đánh giá SRS (Chưa nhớ, Khó, Dễ) luôn luôn hiển thị trực tiếp để người học có thể chọn ngay khi nhìn thấy từ.
 * - Hỗ trợ lật thẻ xem nghĩa / collocations bất kỳ lúc nào.
 */
class FlashcardActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var cardContainer: View
    private lateinit var faceFront: View
    private lateinit var faceBack: View
    private lateinit var btnHeaderNext: View
    private lateinit var layoutSrs: View
    private lateinit var pbSession: ProgressBar

    private lateinit var tvScopeChip: TextView
    private lateinit var tvCardCounter: TextView
    private lateinit var tvCardTotal: TextView
    private lateinit var tvPosFront: TextView
    private lateinit var tvWord: TextView
    private lateinit var tvPhonetic: TextView
    private lateinit var tvExampleFront: TextView
    private lateinit var tvExampleViFront: TextView
    private lateinit var tvPosBack: TextView
    private lateinit var tvPhoneticBack: TextView
    private lateinit var tvMeaningVi: TextView
    private lateinit var tvDefinition: TextView
    private lateinit var tvCollocations: TextView
    private lateinit var tvSynonyms: TextView
    private lateinit var tvAntonyms: TextView
    private lateinit var tvAgainInterval: TextView
    private lateinit var tvHardInterval: TextView
    private lateinit var tvEasyInterval: TextView

    private val queue = ArrayDeque<VocabularyWord>()
    private var totalWords = 0
    private var completedWords = 0
    private val lapsedIds = mutableSetOf<String>()
    private val finalResults = LinkedHashMap<String, Boolean>()

    private var scopeType = VocabularyDataSource.SCOPE_ALL
    private var scopeId: String? = null
    private var sessionLimit = VocabularyProgressManager.SESSION_SIZE

    private var showingBack = false
    private var isFlipping = false
    private var isAdvancing = false

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        scopeType = intent.getStringExtra(EXTRA_SCOPE_TYPE) ?: VocabularyDataSource.SCOPE_ALL
        scopeId = intent.getStringExtra(EXTRA_SCOPE_ID)
        sessionLimit = intent.getIntExtra(EXTRA_LIMIT, VocabularyProgressManager.SESSION_SIZE)

        bindViews()
        setupListeners()

        val pool = VocabularyDataSource.resolveScope(scopeType, scopeId)
        queue.addAll(
            VocabularyProgressManager.buildQueue(
                this, pool, sessionLimit, applyDailyNewCap = false
            )
        )
        if (queue.isEmpty()) {
            Toast.makeText(this, "Không có từ nào cần học lúc này.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        totalWords = queue.size

        val minBand = queue.minOf { it.band }
        tvScopeChip.text = String.format(
            Locale.US, "%s • Band %.1f+", VocabularyDataSource.scopeTitle(scopeType, scopeId), minBand
        )
        tvCardTotal.text = " / $totalWords"

        tts = TextToSpeech(this, this)
        showCurrentCard()
    }

    private fun bindViews() {
        cardContainer = findViewById(R.id.cardContainer)
        faceFront = findViewById(R.id.faceFront)
        faceBack = findViewById(R.id.faceBack)
        btnHeaderNext = findViewById(R.id.btnHeaderNext)
        layoutSrs = findViewById(R.id.layoutSrs)
        pbSession = findViewById(R.id.pbSession)

        tvScopeChip = findViewById(R.id.tvScopeChip)
        tvCardCounter = findViewById(R.id.tvCardCounter)
        tvCardTotal = findViewById(R.id.tvCardTotal)
        tvPosFront = findViewById(R.id.tvPosFront)
        tvWord = findViewById(R.id.tvWord)
        tvPhonetic = findViewById(R.id.tvPhonetic)
        tvExampleFront = findViewById(R.id.tvExampleFront)
        tvExampleViFront = findViewById(R.id.tvExampleViFront)

        tvPosBack = findViewById(R.id.tvPosBack)
        tvPhoneticBack = findViewById(R.id.tvPhoneticBack)
        tvMeaningVi = findViewById(R.id.tvMeaningVi)
        tvDefinition = findViewById(R.id.tvDefinition)
        tvCollocations = findViewById(R.id.tvCollocations)
        tvSynonyms = findViewById(R.id.tvSynonyms)
        tvAntonyms = findViewById(R.id.tvAntonyms)

        tvAgainInterval = findViewById(R.id.tvAgainInterval)
        tvHardInterval = findViewById(R.id.tvHardInterval)
        tvEasyInterval = findViewById(R.id.tvEasyInterval)
    }

    private fun setupListeners() {
        findViewById<View>(R.id.btnClose).setOnClickListener { finish() }

        // Next card trigger (nút mũi tên trên thanh tiêu đề)
        btnHeaderNext.setOnClickListener { advanceCard(null) }

        // Pronounce audio
        findViewById<View>(R.id.btnAudioFront).setOnClickListener { speakCurrentWord() }
        findViewById<View>(R.id.btnAudioBack).setOnClickListener { speakCurrentWord() }

        // SRS Rating triggers (người dùng có thể chọn ngay bất cứ lúc nào)
        findViewById<View>(R.id.btnAgain).setOnClickListener { advanceCard(SrsGrade.AGAIN) }
        findViewById<View>(R.id.btnHard).setOnClickListener { advanceCard(SrsGrade.HARD) }
        findViewById<View>(R.id.btnEasy).setOnClickListener { advanceCard(SrsGrade.EASY) }

        // Gesture detector for cardContainer
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                flipCard()
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (abs(diffX) > abs(diffY) && abs(diffX) > 80 && abs(velocityX) > 100) {
                    if (diffX < 0) {
                        advanceCard(if (showingBack) SrsGrade.HARD else null)
                    } else {
                        flipCard()
                    }
                    return true
                }
                return false
            }
        })

        cardContainer.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun showCurrentCard() {
        if (queue.isEmpty()) {
            finishSession()
            return
        }
        val word = queue.first()

        tvCardCounter.text = String.format(Locale.US, "Thẻ %02d", (completedWords + 1).coerceAtMost(totalWords))
        pbSession.progress = if (totalWords > 0) completedWords * 100 / totalWords else 0

        val formattedPos = VocabularyDataSource.formatPartOfSpeech(word.pos)
        tvPosFront.text = formattedPos
        tvPosBack.text = formattedPos

        tvWord.text = word.word.replaceFirstChar { it.uppercase() }
        tvPhonetic.text = word.phonetic
        tvExampleFront.text = highlightKeyword(word.example, word.word)
        tvExampleViFront.text = "(${word.exampleVi})"

        tvPhoneticBack.text = word.phonetic
        tvMeaningVi.text = word.meaningVi
        tvDefinition.text = word.definition
        tvCollocations.text = word.collocations
        bindExtraLine(tvSynonyms, "Đồng nghĩa: ", word.synonyms)
        bindExtraLine(tvAntonyms, "Trái nghĩa: ", word.antonyms)

        val progress = VocabularyProgressManager.getProgress(this, word.id)
        tvAgainInterval.text = VocabularyProgressManager.previewIntervalLabel(progress, SrsGrade.AGAIN)
        tvHardInterval.text = VocabularyProgressManager.previewIntervalLabel(progress, SrsGrade.HARD)
        tvEasyInterval.text = VocabularyProgressManager.previewIntervalLabel(progress, SrsGrade.EASY)

        // Bắt đầu thẻ mới luôn ở mặt trước
        showingBack = false
        isFlipping = false
        cardContainer.rotationY = 0f
        faceFront.visibility = View.VISIBLE
        faceBack.visibility = View.GONE
        layoutSrs.visibility = View.VISIBLE
    }

    private fun bindExtraLine(view: TextView, label: String, value: String) {
        if (value.isBlank() || value == "-") {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            val full = "$label$value"
            val span = SpannableString(full)
            span.setSpan(
                ForegroundColorSpan(Color.parseColor("#64748B")),
                0, label.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            span.setSpan(
                ForegroundColorSpan(Color.parseColor("#0F172A")),
                label.length, full.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            view.text = span
        }
    }

    private fun highlightKeyword(sentence: String, keyword: String): SpannableString {
        val spannable = SpannableString(sentence)
        val lower = keyword.lowercase()
        val exact = Pattern.compile("\\b" + Pattern.quote(lower) + "\\b", Pattern.CASE_INSENSITIVE).matcher(sentence)
        var start = -1
        var end = -1
        if (exact.find()) {
            start = exact.start()
            end = exact.end()
            if (!lower.contains(' ')) {
                while (end < sentence.length && sentence[end].isLetter()) end++
            }
        } else if (!lower.contains(' ')) {
            val prefix = if (lower.length > 5) lower.dropLast(2) else lower
            val m = Pattern.compile(Pattern.quote(prefix) + "\\w*", Pattern.CASE_INSENSITIVE).matcher(sentence)
            if (m.find()) {
                start = m.start()
                end = m.end()
            }
        }
        if (start >= 0) {
            val flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            spannable.setSpan(ForegroundColorSpan(Color.parseColor("#2563EB")), start, end, flag)
            spannable.setSpan(StyleSpan(android.graphics.Typeface.BOLD), start, end, flag)
            spannable.setSpan(UnderlineSpan(), start, end, flag)
        }
        return spannable
    }

    private fun flipCard() {
        if (isFlipping || isAdvancing) return
        isFlipping = true
        val toBack = !showingBack

        cardContainer.animate()
            .rotationY(if (toBack) 90f else -90f)
            .setDuration(160)
            .withEndAction {
                showingBack = toBack
                faceFront.visibility = if (toBack) View.GONE else View.VISIBLE
                faceBack.visibility = if (toBack) View.VISIBLE else View.GONE

                cardContainer.rotationY = if (toBack) -90f else 90f
                cardContainer.animate()
                    .rotationY(0f)
                    .setDuration(160)
                    .withEndAction {
                        isFlipping = false
                    }.start()
            }.start()
    }

    /**
     * Chuyển sang thẻ tiếp theo:
     * - Lưu kết quả SRS nếu có grade
     * - Trượt thẻ cũ sang trái và trượt thẻ mới vào từ phải cực kỳ mượt mà.
     */
    private fun advanceCard(grade: SrsGrade?) {
        if (isAdvancing || queue.isEmpty()) return
        isAdvancing = true

        val word = queue.first()

        if (grade != null) {
            VocabularyProgressManager.grade(this, word.id, grade)
            queue.removeFirst()
            if (grade == SrsGrade.AGAIN) {
                lapsedIds.add(word.id)
                // Đưa thẻ quay lại sau vài thẻ để ôn tập ngay
                queue.add(minOf(AGAIN_REINSERT_OFFSET, queue.size), word)
            } else {
                completedWords++
                finalResults[word.id] = (grade == SrsGrade.EASY && word.id !in lapsedIds)
            }
        } else {
            // Nhấn Bỏ qua / Thẻ tiếp
            completedWords++
            finalResults[word.id] = false
            queue.removeFirst()
        }

        if (queue.isEmpty()) {
            isAdvancing = false
            finishSession()
            return
        }

        val slideDist = (cardContainer.width.toFloat()).coerceAtLeast(600f)
        cardContainer.animate()
            .translationX(-slideDist)
            .alpha(0.2f)
            .setDuration(150)
            .withEndAction {
                showCurrentCard()
                cardContainer.translationX = slideDist
                cardContainer.alpha = 0.2f
                cardContainer.animate()
                    .translationX(0f)
                    .alpha(1.0f)
                    .setDuration(170)
                    .withEndAction {
                        isAdvancing = false
                    }.start()
            }.start()
    }

    private fun finishSession() {
        val ids = finalResults.keys.toTypedArray()
        val good = BooleanArray(ids.size) { finalResults[ids[it]] == true }
        val xp = good.sumOf { if (it) XP_GOOD else XP_REVIEW }
        VocabularyProgressManager.addXp(this, xp)

        startActivity(Intent(this, VocabularySessionResultActivity::class.java).apply {
            putExtra(VocabularySessionResultActivity.EXTRA_WORD_IDS, ids)
            putExtra(VocabularySessionResultActivity.EXTRA_GOOD_FLAGS, good)
            putExtra(VocabularySessionResultActivity.EXTRA_XP, xp)
            putExtra(EXTRA_SCOPE_TYPE, scopeType)
            putExtra(EXTRA_SCOPE_ID, scopeId)
            putExtra(EXTRA_LIMIT, sessionLimit)
        })
        finish()
    }

    // ===== Text-to-speech =====
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.UK)
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    private fun speakCurrentWord() {
        if (queue.isEmpty()) return
        if (!ttsReady) {
            Toast.makeText(this, "Thiết bị chưa hỗ trợ phát âm tiếng Anh.", Toast.LENGTH_SHORT).show()
            return
        }
        tts?.speak(queue.first().word, TextToSpeech.QUEUE_FLUSH, null, "vocab_word")
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_SCOPE_TYPE = "SCOPE_TYPE"
        const val EXTRA_SCOPE_ID = "SCOPE_ID"
        const val EXTRA_LIMIT = "SESSION_LIMIT"

        private const val AGAIN_REINSERT_OFFSET = 3
        private const val XP_GOOD = 3
        private const val XP_REVIEW = 1

        fun start(
            context: android.content.Context,
            scopeType: String = VocabularyDataSource.SCOPE_ALL,
            scopeId: String? = null,
            limit: Int = VocabularyProgressManager.SESSION_SIZE
        ) {
            val intent = Intent(context, FlashcardActivity::class.java).apply {
                putExtra(EXTRA_SCOPE_TYPE, scopeType)
                putExtra(EXTRA_SCOPE_ID, scopeId)
                putExtra(EXTRA_LIMIT, limit)
            }
            context.startActivity(intent)
        }
    }
}
