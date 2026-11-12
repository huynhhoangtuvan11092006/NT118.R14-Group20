package com.example.ielts.ui.vocabulary

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.datasource.VocabularyDataSource
import com.example.ielts.data.datasource.VocabularyWord
import com.example.ielts.data.local.VocabularyProgressManager
import com.example.ielts.ui.vocabulary.adapter.VocabularyWordRowAdapter
import com.example.ielts.ui.vocabulary.adapter.WordRowItem
import com.example.ielts.ui.vocabulary.adapter.WordStatus

class VocabularySessionResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_WORD_IDS = "extra_word_ids"
        const val EXTRA_GOOD_FLAGS = "extra_good_flags"
        const val EXTRA_XP = "extra_xp"
        const val EXTRA_SCOPE_TYPE = "extra_scope_type"
        const val EXTRA_SCOPE_ID = "extra_scope_id"
        const val EXTRA_LIMIT = "extra_limit"
        private const val COLLAPSED_COUNT = 3
    }

    private var scopeType: String = VocabularyDataSource.SCOPE_ALL
    private var scopeId: String? = null
    private var sessionLimit: Int = 20
    private var isExpanded: Boolean = false

    private val allReviewedItems = mutableListOf<WordRowItem>()
    private lateinit var adapter: VocabularyWordRowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary_session_result)

        scopeType = intent.getStringExtra(EXTRA_SCOPE_TYPE) ?: VocabularyDataSource.SCOPE_ALL
        scopeId = intent.getStringExtra(EXTRA_SCOPE_ID)
        sessionLimit = intent.getIntExtra(EXTRA_LIMIT, 20)

        val ids = intent.getStringArrayExtra(EXTRA_WORD_IDS) ?: emptyArray()
        val goodFlags = intent.getBooleanArrayExtra(EXTRA_GOOD_FLAGS) ?: BooleanArray(0)
        val xp = intent.getIntExtra(EXTRA_XP, ids.size * 2)

        setupData(ids, goodFlags)
        setupViews(xp)
        setupRecyclerView()
        setupActions()

        // Kích hoạt chuỗi Streak & hiện popup chúc mừng khi hoàn thành phiên học từ vựng
        val streakResult = com.example.ielts.data.local.StreakManager.recordActivity(this, "VOCABULARY")
        com.example.ielts.ui.streak.StreakCelebrationDialog(
            this,
            streakResult.newStreak,
            isFirstToday = streakResult.isFirstActivityToday
        ).show()
    }

    private fun setupData(ids: Array<String>, goodFlags: BooleanArray) {
        allReviewedItems.clear()
        ids.forEachIndexed { index, id ->
            val word = VocabularyDataSource.getWord(id)
            if (word != null) {
                val isGood = goodFlags.getOrNull(index) ?: true
                val status = if (isGood) WordStatus.MASTERED else WordStatus.REVIEW
                allReviewedItems.add(WordRowItem(word, status))
            }
        }
    }

    private fun setupViews(xp: Int) {
        val btnClose = findViewById<ImageButton>(R.id.btnClose)
        val tvResultSubtitle = findViewById<TextView>(R.id.tvResultSubtitle)
        val tvTotalStudied = findViewById<TextView>(R.id.tvTotalStudied)
        val tvMasteredCount = findViewById<TextView>(R.id.tvMasteredCount)
        val tvXpEarned = findViewById<TextView>(R.id.tvXpEarned)
        val tvNextReviewSchedule = findViewById<TextView>(R.id.tvNextReviewSchedule)
        val tvReviewedCount = findViewById<TextView>(R.id.tvReviewedCount)
        val btnToggleShowAll = findViewById<TextView>(R.id.btnToggleShowAll)

        btnClose.setOnClickListener { navigateToHub() }

        val scopeTitle = VocabularyDataSource.scopeTitle(scopeType, scopeId)
        val totalCount = allReviewedItems.size
        val masteredCount = allReviewedItems.count { it.status == WordStatus.MASTERED }

        tvResultSubtitle.text = "Bạn đã củng cố trí nhớ cho $totalCount từ vựng chủ đề $scopeTitle."
        tvTotalStudied.text = totalCount.toString()
        tvMasteredCount.text = masteredCount.toString()
        tvXpEarned.text = "+$xp XP"

        val dueTomorrow = VocabularyProgressManager.countDueByTomorrow(this)
        val tomorrowCount = if (dueTomorrow > 0) dueTomorrow else (totalCount / 2).coerceAtLeast(1)
        tvNextReviewSchedule.text = "$tomorrowCount từ sẽ đến hạn ôn lại vào ngày mai (theo chu kỳ SRS)."

        updateReviewedHeader(tvReviewedCount, btnToggleShowAll)
    }

    private fun updateReviewedHeader(tvReviewedCount: TextView, btnToggleShowAll: TextView) {
        val total = allReviewedItems.size
        val shown = if (isExpanded || total <= COLLAPSED_COUNT) total else COLLAPSED_COUNT
        tvReviewedCount.text = "Từ vựng vừa ôn tập ($shown/$total)"

        if (total <= COLLAPSED_COUNT) {
            btnToggleShowAll.visibility = android.view.View.GONE
        } else {
            btnToggleShowAll.visibility = android.view.View.VISIBLE
            btnToggleShowAll.text = if (isExpanded) "Thu gọn" else "Xem tất cả"
        }
    }

    private fun setupRecyclerView() {
        val rvReviewedWords = findViewById<RecyclerView>(R.id.rvReviewedWords)
        val tvReviewedCount = findViewById<TextView>(R.id.tvReviewedCount)
        val btnToggleShowAll = findViewById<TextView>(R.id.btnToggleShowAll)

        rvReviewedWords.layoutManager = LinearLayoutManager(this)
        val initialItems = if (allReviewedItems.size > COLLAPSED_COUNT && !isExpanded) {
            allReviewedItems.take(COLLAPSED_COUNT)
        } else {
            allReviewedItems
        }

        adapter = VocabularyWordRowAdapter(initialItems) { word ->
            showWordDetailBottomSheet(word)
        }
        rvReviewedWords.adapter = adapter

        btnToggleShowAll.setOnClickListener {
            isExpanded = !isExpanded
            val newItems = if (isExpanded) allReviewedItems else allReviewedItems.take(COLLAPSED_COUNT)
            adapter.update(newItems)
            updateReviewedHeader(tvReviewedCount, btnToggleShowAll)
        }
    }

    private fun showWordDetailBottomSheet(word: VocabularyWord) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val v = layoutInflater.inflate(R.layout.bottom_sheet_vocab_search, null)
        dialog.setContentView(v)

        val etSearch = v.findViewById<android.widget.EditText>(R.id.etSearchWord)
        val rvResults = v.findViewById<RecyclerView>(R.id.rvSearchResults)
        val tvEmpty = v.findViewById<TextView>(R.id.tvSearchEmpty)

        etSearch.setText(word.word)
        etSearch.setSelection(word.word.length)
        tvEmpty.visibility = android.view.View.GONE
        rvResults.visibility = android.view.View.VISIBLE
        rvResults.layoutManager = LinearLayoutManager(this)
        rvResults.adapter = VocabularyWordRowAdapter(listOf(WordRowItem(word, WordStatus.MASTERED))) {}

        dialog.show()
    }

    private fun setupActions() {
        val btnContinueNext = findViewById<AppCompatButton>(R.id.btnContinueNext)
        val btnBackToHub = findViewById<AppCompatButton>(R.id.btnBackToHub)

        btnContinueNext.setOnClickListener {
            // Khởi động phiên học tiếp theo
            FlashcardActivity.start(this, scopeType, scopeId, sessionLimit)
            finish()
        }

        btnBackToHub.setOnClickListener {
            navigateToHub()
        }
    }

    private fun navigateToHub() {
        val intent = Intent(this, VocabularyHubActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        navigateToHub()
    }
}

