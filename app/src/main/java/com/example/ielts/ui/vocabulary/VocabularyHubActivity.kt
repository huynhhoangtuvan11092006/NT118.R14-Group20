package com.example.ielts.ui.vocabulary

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.MainActivity
import com.example.ielts.R
import com.example.ielts.data.datasource.VocabularyDataSource
import com.example.ielts.data.datasource.VocabularyWord
import com.example.ielts.data.local.VocabularyProgressManager
import com.example.ielts.data.local.WordProgress
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.practice.PracticeHubActivity
import com.example.ielts.ui.vocabulary.adapter.VocabularyWordRowAdapter
import com.example.ielts.ui.vocabulary.adapter.WordRowItem
import com.example.ielts.ui.vocabulary.adapter.WordStatus
import com.example.ielts.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Locale

class VocabularyHubActivity : AppCompatActivity() {

    private enum class Filter { ALL, BAND_6, BAND_75, COLLOCATIONS }

    private lateinit var tvDueCount: TextView
    private lateinit var tvMasteredCount: TextView
    private lateinit var tvLearningCount: TextView
    private lateinit var layoutTopicHeader: View
    private lateinit var tvTopicCount: TextView
    private lateinit var layoutTopicGrid: LinearLayout
    private lateinit var tvDeckHeader: View
    private lateinit var layoutDeckList: LinearLayout
    private lateinit var tvEmptyFilter: View

    private lateinit var chipAll: TextView
    private lateinit var chipBand6: TextView
    private lateinit var chipBand75: TextView
    private lateinit var chipColloc: TextView

    private var currentFilter = Filter.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary_hub)

        bindViews()
        setupListeners()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        refreshAll()
    }

    private fun bindViews() {
        tvDueCount = findViewById(R.id.tvDueCount)
        tvMasteredCount = findViewById(R.id.tvMasteredCount)
        tvLearningCount = findViewById(R.id.tvLearningCount)
        layoutTopicHeader = findViewById(R.id.layoutTopicHeader)
        tvTopicCount = findViewById(R.id.tvTopicCount)
        layoutTopicGrid = findViewById(R.id.layoutTopicGrid)
        tvDeckHeader = findViewById(R.id.tvDeckHeader)
        layoutDeckList = findViewById(R.id.layoutDeckList)
        tvEmptyFilter = findViewById(R.id.tvEmptyFilter)
        chipAll = findViewById(R.id.chipAll)
        chipBand6 = findViewById(R.id.chipBand6)
        chipBand75 = findViewById(R.id.chipBand75)
        chipColloc = findViewById(R.id.chipColloc)
    }

    private fun setupListeners() {
        findViewById<View>(R.id.btnReviewNow)?.setOnClickListener {
            val stats = VocabularyProgressManager.summaryStats(this, VocabularyDataSource.allWords)
            if (stats.learning == 0 && stats.mastered == 0 && stats.dueToday == 0) {
                Toast.makeText(this, "Bạn chưa có từ vựng nào để ôn tập. Hãy chọn một chủ đề bên dưới để bắt đầu học nhé!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startSession(VocabularyDataSource.SCOPE_ALL, null, VocabularyProgressManager.SESSION_SIZE, isReview = true)
        }
        val quickAction = View.OnClickListener {
            startSession(VocabularyDataSource.SCOPE_ALL, null, QUICK_SESSION_SIZE)
        }
        findViewById<View>(R.id.bannerQuickFlashcard)?.setOnClickListener(quickAction)
        findViewById<View>(R.id.btnQuickStart)?.setOnClickListener(quickAction)

        chipAll.setOnClickListener { selectFilter(Filter.ALL) }
        chipBand6.setOnClickListener { selectFilter(Filter.BAND_6) }
        chipBand75.setOnClickListener { selectFilter(Filter.BAND_75) }
        chipColloc.setOnClickListener { selectFilter(Filter.COLLOCATIONS) }
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
        }
        findViewById<View>(R.id.navRoadmap)?.setOnClickListener {
            Toast.makeText(this, "Lộ trình học cá nhân hóa IELTS", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.navPractice)?.setOnClickListener {
            startActivity(Intent(this, PracticeHubActivity::class.java))
        }
        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun selectFilter(filter: Filter) {
        currentFilter = filter
        val chips = mapOf(
            Filter.ALL to chipAll,
            Filter.BAND_6 to chipBand6,
            Filter.BAND_75 to chipBand75,
            Filter.COLLOCATIONS to chipColloc
        )
        for ((f, chip) in chips) {
            val active = f == filter
            chip.setBackgroundResource(if (active) R.drawable.bg_chip_selected_pill_blue else R.drawable.bg_chip_unselected_pill)
            chip.setTextColor(if (active) Color.WHITE else Color.parseColor("#131B2E"))
            chip.setTypeface(null, if (active) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        }
        refreshLists()
    }

    private fun refreshAll() {
        val stats = VocabularyProgressManager.summaryStats(this, VocabularyDataSource.allWords)
        tvDueCount.text = stats.dueToday.toString()
        tvMasteredCount.text = "Đã thuộc: ${stats.mastered} từ"
        tvLearningCount.text = "Đang học: ${stats.learning} từ"
        refreshLists()
    }

    private fun matchesFilter(word: VocabularyWord): Boolean = when (currentFilter) {
        Filter.ALL -> true
        Filter.BAND_6 -> !word.isAdvanced
        Filter.BAND_75 -> word.isAdvanced
        Filter.COLLOCATIONS -> false
    }

    private fun refreshLists() {
        buildTopicGrid()
        buildDeckList()
    }

    private fun buildTopicGrid() {
        layoutTopicGrid.removeAllViews()
        val showTopics = currentFilter != Filter.COLLOCATIONS
        layoutTopicHeader.visibility = if (showTopics) View.VISIBLE else View.GONE
        layoutTopicGrid.visibility = if (showTopics) View.VISIBLE else View.GONE

        val visibleTopics = VocabularyDataSource.topics.filter { t ->
            VocabularyDataSource.wordsOfTopic(t.id).any { matchesFilter(it) }
        }
        tvTopicCount.text = "${visibleTopics.size} chủ đề"
        tvEmptyFilter.visibility = if (showTopics && visibleTopics.isEmpty()) View.VISIBLE else View.GONE

        val inflater = LayoutInflater.from(this)
        val gap = (12 * resources.displayMetrics.density).toInt()

        visibleTopics.chunked(2).forEach { pair ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = gap }
            }
            pair.forEachIndexed { index, topic ->
                if (index == 1) row.addView(View(this), LinearLayout.LayoutParams(gap, 1))
                row.addView(buildTopicCard(inflater, row, topic.id))
            }
            if (pair.size == 1) {
                row.addView(View(this), LinearLayout.LayoutParams(gap, 1))
                row.addView(View(this), LinearLayout.LayoutParams(0, 1, 1f))
            }
            layoutTopicGrid.addView(row)
        }
    }

    private fun buildTopicCard(inflater: LayoutInflater, parent: ViewGroup, topicId: String): View {
        val topic = VocabularyDataSource.getTopic(topicId)!!
        val pool = VocabularyDataSource.wordsOfTopic(topicId).filter { matchesFilter(it) }
        val percent = VocabularyProgressManager.progressPercent(this, pool)
        val lessons = (pool.size + 5) / 6

        val card = inflater.inflate(R.layout.item_vocab_topic_card, parent, false)
        val accent = Color.parseColor(topic.accentColor)

        card.findViewById<ImageView>(R.id.imgTopicIcon).apply {
            setImageResource(topic.iconRes)
            imageTintList = ColorStateList.valueOf(accent)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor(topic.tileColor))
        }
        card.findViewById<TextView>(R.id.tvTopicTitle).text = topic.title
        card.findViewById<TextView>(R.id.tvTopicMeta).text = "${pool.size} từ • $lessons bài học"
        card.findViewById<TextView>(R.id.tvTopicPercent).text = "$percent%"
        card.findViewById<ProgressBar>(R.id.pbTopic).apply {
            progress = percent
            progressTintList = ColorStateList.valueOf(accent)
        }
        card.setOnClickListener {
            startSession(VocabularyDataSource.SCOPE_TOPIC, topic.id, VocabularyProgressManager.SESSION_SIZE)
        }
        return card
    }

    private fun buildDeckList() {
        layoutDeckList.removeAllViews()
        val inflater = LayoutInflater.from(this)

        val decks = VocabularyDataSource.decks.filter { deck ->
            if (currentFilter == Filter.COLLOCATIONS) deck.isPhraseDeck
            else VocabularyDataSource.wordsOfDeck(deck.id).any { matchesFilter(it) }
        }
        tvDeckHeader.visibility = if (decks.isEmpty()) View.GONE else View.VISIBLE

        decks.forEach { deck ->
            val row = inflater.inflate(R.layout.item_vocab_deck_row, layoutDeckList, false)
            row.findViewById<ImageView>(R.id.imgDeckIcon).apply {
                setImageResource(deck.iconRes)
                imageTintList = ColorStateList.valueOf(Color.parseColor(deck.iconColor))
                backgroundTintList = ColorStateList.valueOf(Color.parseColor(deck.iconTileColor))
            }
            row.findViewById<TextView>(R.id.tvDeckTitle).text = deck.title
            row.findViewById<TextView>(R.id.tvDeckSubtitle).text = VocabularyDataSource.deckSubtitle(deck.id)
            row.findViewById<TextView>(R.id.tvDeckBadge).apply {
                text = deck.badge
                setTextColor(Color.parseColor(deck.badgeTextColor))
                backgroundTintList = ColorStateList.valueOf(Color.parseColor(deck.badgeBgColor))
            }
            row.setOnClickListener {
                startSession(VocabularyDataSource.SCOPE_DECK, deck.id, VocabularyProgressManager.SESSION_SIZE)
            }
            layoutDeckList.addView(row)
        }
    }

    private fun startSession(type: String, id: String?, limit: Int, isReview: Boolean = false) {
        val pool = VocabularyDataSource.resolveScope(type, id)
        val queue = VocabularyProgressManager.buildQueue(
            this, pool, limit, applyDailyNewCap = false, isReviewMode = isReview
        )
        if (queue.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy từ vựng trong phần này.", Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(Intent(this, FlashcardActivity::class.java).apply {
            putExtra(FlashcardActivity.EXTRA_SCOPE_TYPE, type)
            putExtra(FlashcardActivity.EXTRA_SCOPE_ID, id)
            putExtra(FlashcardActivity.EXTRA_LIMIT, limit)
            putExtra(FlashcardActivity.EXTRA_IS_REVIEW, isReview)
        })
    }

    // ===== Search =====
    private fun statusOf(progress: WordProgress): WordStatus {
        val due = progress.seen && progress.nextReviewMillis <= System.currentTimeMillis()
        return when {
            !progress.seen -> WordStatus.NEW
            VocabularyProgressManager.isMastered(progress) -> WordStatus.MASTERED
            due -> WordStatus.REVIEW
            else -> WordStatus.LEARNING
        }
    }

    private fun showSearchSheet() {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_vocab_search, null)
        dialog.setContentView(view)

        val etSearch = view.findViewById<EditText>(R.id.etSearchWord)
        val tvEmpty = view.findViewById<TextView>(R.id.tvSearchEmpty)
        val rv = view.findViewById<RecyclerView>(R.id.rvSearchResults)
        view.findViewById<View>(R.id.btnCloseSearch).setOnClickListener { dialog.dismiss() }

        val progress = VocabularyProgressManager.getAll(this)
        fun toItems(words: List<VocabularyWord>) =
            words.map { WordRowItem(it, statusOf(progress[it.id] ?: WordProgress())) }

        val adapter = VocabularyWordRowAdapter(toItems(VocabularyDataSource.allWords)) { showWordDetail(it) }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s?.toString()?.trim()?.lowercase().orEmpty()
                val result = if (q.isEmpty()) VocabularyDataSource.allWords
                else VocabularyDataSource.allWords.filter {
                    it.word.lowercase().contains(q) || it.meaningVi.lowercase().contains(q)
                }
                adapter.update(toItems(result))
                tvEmpty.visibility = if (result.isEmpty()) View.VISIBLE else View.GONE
            }
        })
        dialog.show()
    }

    private fun showWordDetail(word: VocabularyWord) {
        val posVi = VocabularyDataSource.formatPartOfSpeech(word.pos)
        val body = buildString {
            append(word.phonetic).append("  •  ").append(posVi).append("\n\n")
            append(word.meaningVi).append("\n")
            append(word.definition).append("\n\n")
            append("VD: ").append(word.example).append("\n")
            append("(").append(word.exampleVi).append(")\n\n")
            append("Collocations: ").append(word.collocations)
        }
        AlertDialog.Builder(this)
            .setTitle(word.word.replaceFirstChar { it.uppercase() })
            .setMessage(body)
            .setPositiveButton("Đóng", null)
            .show()
    }

    companion object {
        private const val QUICK_SESSION_SIZE = 10
    }
}

