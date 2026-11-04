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
import android.view.View
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

/**
 * Phiên học flashcard: thẻ lật 2 mặt, Spaced Repetition (Lặp lại / Khó / Dễ).
 * Tiến độ từng từ được lưu ngay sau mỗi lần đánh giá qua [VocabularyProgressManager].
 */
class FlashcardActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var cardContainer: View
    private lateinit var faceFront: View
    private lateinit var faceBack: View
    private lateinit var btnFlip: View
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

    private var tts: TextToSpeech? = null
    private var ttsReady = false

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
        btnFlip = findViewById(R.id.btnFlip)
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

        // Khoảng cách camera lớn giúp hiệu ứng lật 3D không bị méo
        cardContainer.cameraDistance = 12000f * resources.displayMetrics.density
    }

    private fun setupListeners() {
        findViewById<View>(R.id.btnClose).setOnClickListener { finish() }
        cardContainer.setOnClickListener { flipCard() }
        btnFlip.setOnClickListener { flipCard() }
        findViewById<View>(R.id.btnAudioFront).setOnClickListener { speakCurrentWord() }
        findViewById<View>(R.id.btnAudioBack).setOnClickListener { speakCurrentWord() }
        findViewById<View>(R.id.btnAgain).setOnClickListener { onGrade(SrsGrade.AGAIN) }
        findViewById<View>(R.id.btnHard).setOnClickListener { onGrade(SrsGrade.HARD) }
        findViewById<View>(R.id.btnEasy).setOnClickListener { onGrade(SrsGrade.EASY) }
    }

    private fun showCurrentCard() {
        val word = queue.first()

        tvCardCounter.text = String.format(Locale.US, "Thẻ %02d", (completedWords + 1).coerceAtMost(totalWords))
        pbSession.progress = if (totalWords > 0) completedWords * 100 / totalWords else 0

        tvPosFront.text = word.pos
        tvWord.text = word.word.replaceFirstChar { it.uppercase() }
        tvPhonetic.text = word.phonetic
        tvExampleFront.text = highlightKeyword(word.example, word.word)
        tvExampleViFront.text = "(${word.exampleVi})"

        tvPosBack.text = word.pos
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

        // Luôn bắt đầu thẻ mới ở mặt trước
        showingBack = false
        cardContainer.rotationY = 0f
        faceFront.visibility = View.VISIBLE
        faceBack.visibility = View.GONE
        btnFlip.visibility = View.VISIBLE
        layoutSrs.visibility = View.GONE
    }

    private fun bindExtraLine(view: TextView, label: String, value: String) {
        if (value.isBlank() || value == "-") {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            view.text = label + value
        }
    }

    /** Làm nổi bật từ khóa (kể cả biến thể như deteriorated) trong câu ví dụ. */
    private fun highlightKeyword(sentence: String, word: String): SpannableString {
        val spannable = SpannableString(sentence)
        val lower = word.lowercase()
        val exact = Pattern.compile(Pattern.quote(lower), Pattern.CASE_INSENSITIVE).matcher(sentence)
        var start = -1
        var end = -1
        if (exact.find()) {
            start = exact.start()
            end = exact.end()
            // mở rộng tới hết từ (vd. deteriorate -> deteriorated)
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
        if (isFlipping) return
        isFlipping = true
        val toBack = !showingBack

        cardContainer.animate().rotationY(if (toBack) 90f else -90f).setDuration(180).withEndAction {
            showingBack = toBack
            faceFront.visibility = if (toBack) View.GONE else View.VISIBLE
            faceBack.visibility = if (toBack) View.VISIBLE else View.GONE
            cardContainer.rotationY = if (toBack) -90f else 90f
            cardContainer.animate().rotationY(0f).setDuration(180).withEndAction {
                isFlipping = false
            }.start()
        }.start()

        if (toBack) {
            btnFlip.visibility = View.GONE
            layoutSrs.visibility = View.VISIBLE
        }
    }

    private fun onGrade(grade: SrsGrade) {
        if (!showingBack || isFlipping) return
        val word = queue.first()
        VocabularyProgressManager.grade(this, word.id, grade)

        queue.removeFirst()
        if (grade == SrsGrade.AGAIN) {
            lapsedIds.add(word.id)
            // Cho từ quay lại sau vài thẻ để ôn ngay trong phiên
            queue.add(minOf(AGAIN_REINSERT_OFFSET, queue.size), word)
        } else {
            completedWords++
            finalResults[word.id] = grade == SrsGrade.EASY && word.id !in lapsedIds
        }

        if (queue.isEmpty()) finishSession() else showCurrentCard()
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
