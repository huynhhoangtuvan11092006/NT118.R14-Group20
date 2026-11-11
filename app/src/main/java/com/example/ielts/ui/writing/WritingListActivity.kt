package com.example.ielts.ui.writing

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.MainActivity
import com.example.ielts.R
import com.example.ielts.data.datasource.CambridgeWritingDataSource
import com.example.ielts.data.datasource.WritingTestItem
import com.example.ielts.data.local.WritingStatsManager
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.practice.PracticeHubActivity
import com.example.ielts.ui.writing.adapter.WritingTestAdapter
import com.example.ielts.utils.Constants
import java.util.Locale

class WritingListActivity : AppCompatActivity() {

    private lateinit var rvWritingTests: RecyclerView
    private lateinit var edtSearchWriting: EditText
    private lateinit var tvResultCount: TextView
    private lateinit var layoutEmpty: View

    // Top Stats Card
    private lateinit var tvRecentBand: TextView
    private lateinit var tvRecentBandDiff: TextView
    private lateinit var tvEssaysCount: TextView
    private lateinit var tvTargetBand: TextView

    // Segmented Tabs
    private lateinit var tabTaskAll: TextView
    private lateinit var tabTask1: TextView
    private lateinit var tabTask2: TextView

    // Filter Chips
    private lateinit var chipFilterAll: TextView
    private lateinit var chipLineGraph: TextView
    private lateinit var chipBarChart: TextView
    private lateinit var chipAgreeDisagree: TextView
    private lateinit var chipDiscussion: TextView

    private lateinit var adapter: WritingTestAdapter
    private val allTests = mutableListOf<WritingTestItem>()
    private var currentTaskTab: String = "ALL" // "ALL", "TASK_1", "TASK_2"
    private var currentFilterChip: String = "ALL" // "ALL", "LINE_GRAPH", "BAR_CHART", "AGREE", "DISCUSSION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        initViews()
        setupListeners()
        setupRecyclerView()
        loadWritingTests()
    }

    override fun onResume() {
        super.onResume()
        updateStatsHeader()
        applyFilterAndSearch()
    }

    private fun initViews() {
        rvWritingTests = findViewById(R.id.rvWritingTests)
        edtSearchWriting = findViewById(R.id.edtSearchWriting)
        tvResultCount = findViewById(R.id.tvResultCount)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        tvRecentBand = findViewById(R.id.tvRecentBand)
        tvRecentBandDiff = findViewById(R.id.tvRecentBandDiff)
        tvEssaysCount = findViewById(R.id.tvEssaysCount)
        tvTargetBand = findViewById(R.id.tvTargetBand)

        tabTaskAll = findViewById(R.id.tabTaskAll)
        tabTask1 = findViewById(R.id.tabTask1)
        tabTask2 = findViewById(R.id.tabTask2)

        chipFilterAll = findViewById(R.id.chipFilterAll)
        chipLineGraph = findViewById(R.id.chipLineGraph)
        chipBarChart = findViewById(R.id.chipBarChart)
        chipAgreeDisagree = findViewById(R.id.chipAgreeDisagree)
        chipDiscussion = findViewById(R.id.chipDiscussion)
    }

    private fun setupRecyclerView() {
        adapter = WritingTestAdapter(
            testList = emptyList(),
            onStartWriting = { item -> startWritingSession(item) }
        )
        rvWritingTests.layoutManager = LinearLayoutManager(this)
        rvWritingTests.adapter = adapter
    }

    private fun setupListeners() {
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        // Segmented Tabs
        tabTaskAll.setOnClickListener { selectTaskTab("ALL") }
        tabTask1.setOnClickListener { selectTaskTab("TASK_1") }
        tabTask2.setOnClickListener { selectTaskTab("TASK_2") }

        // Filter chips
        chipFilterAll.setOnClickListener { selectFilterChip("ALL") }
        chipLineGraph.setOnClickListener { selectFilterChip("LINE_GRAPH") }
        chipBarChart.setOnClickListener { selectFilterChip("BAR_CHART") }
        chipAgreeDisagree.setOnClickListener { selectFilterChip("AGREE") }
        chipDiscussion.setOnClickListener { selectFilterChip("DISCUSSION") }

        // Search text watcher
        edtSearchWriting.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilterAndSearch()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Bottom Navigation
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.navPractice)?.setOnClickListener {
            val intent = Intent(this, PracticeHubActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.navVocab)?.setOnClickListener {
            startActivity(Intent(this, com.example.ielts.ui.vocabulary.VocabularyHubActivity::class.java))
        }

        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun loadWritingTests() {
        allTests.clear()
        allTests.addAll(CambridgeWritingDataSource.getAllTests())
        updateStatsHeader()
        applyFilterAndSearch()
    }

    private fun updateStatsHeader() {
        val userPrefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val targetBand = userPrefs.getFloat(Constants.KEY_TARGET_BAND, 7.5f)
        tvTargetBand.text = String.format(Locale.US, "%.1f", targetBand)

        val stats = WritingStatsManager.getSummaryStats(this)
        val records = WritingStatsManager.getRecords(this)
        if (records.isNotEmpty()) {
            tvRecentBand.text = String.format(Locale.US, "%.1f", stats.recentBand)
            tvRecentBandDiff.text = stats.bandDiff
            tvRecentBandDiff.setTextColor(Color.parseColor("#BBF7D0"))
        } else {
            tvRecentBand.text = "--"
            tvRecentBandDiff.text = "Chưa có điểm"
            tvRecentBandDiff.setTextColor(Color.parseColor("#E0E7FF"))
        }

        tvEssaysCount.text = "${stats.essaysWritten} bài"
    }

    private fun selectTaskTab(tab: String) {
        currentTaskTab = tab

        val selectedBg = R.drawable.bg_btn_passage_selected
        val unselectedBg = R.drawable.bg_btn_passage_unselected
        val white = Color.WHITE
        val gray = ContextCompat.getColor(this, R.color.text_secondary)

        tabTaskAll.setBackgroundResource(if (tab == "ALL") selectedBg else unselectedBg)
        tabTaskAll.setTextColor(if (tab == "ALL") white else gray)

        tabTask1.setBackgroundResource(if (tab == "TASK_1") selectedBg else unselectedBg)
        tabTask1.setTextColor(if (tab == "TASK_1") white else gray)

        tabTask2.setBackgroundResource(if (tab == "TASK_2") selectedBg else unselectedBg)
        tabTask2.setTextColor(if (tab == "TASK_2") white else gray)

        applyFilterAndSearch()
    }

    private fun selectFilterChip(chip: String) {
        currentFilterChip = chip

        val selectedBg = R.drawable.bg_chip_selected_pill
        val unselectedBg = R.drawable.bg_chip_unselected_pill
        val white = ContextCompat.getColor(this, R.color.white)
        val gray = ContextCompat.getColor(this, R.color.text_secondary)

        fun update(c: TextView, isSelected: Boolean) {
            c.setBackgroundResource(if (isSelected) selectedBg else unselectedBg)
            c.setTextColor(if (isSelected) white else gray)
        }

        update(chipFilterAll, chip == "ALL")
        update(chipLineGraph, chip == "LINE_GRAPH")
        update(chipBarChart, chip == "BAR_CHART")
        update(chipAgreeDisagree, chip == "AGREE")
        update(chipDiscussion, chip == "DISCUSSION")

        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val query = edtSearchWriting.text.toString().trim().lowercase()

        val filtered = allTests.filter { item ->
            // Task Tab Check
            val matchesTab = when (currentTaskTab) {
                "TASK_1" -> item.taskType == "ACADEMIC_TASK_1"
                "TASK_2" -> item.taskType == "TASK_2"
                else -> true
            }

            // Filter Chip Check
            val matchesChip = when (currentFilterChip) {
                "LINE_GRAPH" -> item.topic.contains("Line", ignoreCase = true)
                "BAR_CHART" -> item.topic.contains("Bar", ignoreCase = true)
                "AGREE" -> item.topic.contains("Agree", ignoreCase = true)
                "DISCUSSION" -> item.topic.contains("Discussion", ignoreCase = true) || item.topic.contains("Discuss", ignoreCase = true)
                else -> true
            }

            // Search Query Check
            val matchesQuery = if (query.isEmpty()) true else {
                item.title.lowercase().contains(query) ||
                item.prompt.lowercase().contains(query) ||
                item.topic.lowercase().contains(query) ||
                item.series.lowercase().contains(query)
            }

            matchesTab && matchesChip && matchesQuery
        }

        adapter.updateData(filtered)
        tvResultCount.text = "${filtered.size} đề chuẩn"

        if (filtered.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            rvWritingTests.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            rvWritingTests.visibility = View.VISIBLE
        }
    }

    private fun startWritingSession(item: WritingTestItem) {
        val intent = Intent(this, WritingEditorActivity::class.java).apply {
            putExtra("EXTRA_TEST_ID", item.id)
            putExtra("EXTRA_TASK_TYPE", item.taskType)
            putExtra("EXTRA_TITLE", item.title)
            putExtra("EXTRA_PROMPT", item.prompt)
            putExtra("EXTRA_MIN_WORDS", item.minWords)
            putExtra("EXTRA_DURATION_MINUTES", item.durationMinutes)
            putExtra("EXTRA_SOURCE_MATERIAL", item.sourceMaterial)
            item.imageResId?.let { putExtra("EXTRA_IMAGE_RES_ID", it) }
        }
        startActivity(intent)
    }
}

