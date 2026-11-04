package com.example.ielts.ui.reading

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.MainActivity
import com.example.ielts.R
import com.example.ielts.data.datasource.CambridgeReadingDataSource
import com.example.ielts.data.local.ReadingStatsManager
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.ReadingTestListItemDto
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.practice.PracticeHubActivity
import com.example.ielts.ui.reading.adapter.ReadingTestAdapter
import com.example.ielts.utils.Constants
import kotlinx.coroutines.launch
import java.util.Locale

class ReadingListActivity : AppCompatActivity() {

    private lateinit var rvReadingTests: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: View
    private lateinit var tvResultCount: TextView
    private lateinit var edtSearchTests: EditText

    // Top Stats Card
    private lateinit var tvRecentBand: TextView
    private lateinit var tvRecentBandDiff: TextView
    private lateinit var tvPracticedCount: TextView
    private lateinit var tvPracticedPassages: TextView
    private lateinit var tvAccuracyAvg: TextView
    private lateinit var pbAccuracyAvg: ProgressBar

    // Filter Chips
    private lateinit var chipAll: TextView
    private lateinit var chipCam19: TextView
    private lateinit var chipCam18: TextView
    private lateinit var chipCam17: TextView
    private lateinit var chipCam16: TextView
    private lateinit var chipCam15: TextView
    private lateinit var chipCam14: TextView
    private lateinit var chipCam13: TextView
    private lateinit var chipCam12: TextView
    private lateinit var chipCam11: TextView
    private lateinit var chipPassage: TextView

    private lateinit var adapter: ReadingTestAdapter
    private val allTests = mutableListOf<ReadingTestListItemDto>()
    private var currentFilter: String = "ALL" // ALL, CAM19, CAM18, CAM17, CAM16, CAM15, CAM14, CAM13, CAM12, CAM11, PASSAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading_list)

        initViews()
        setupListeners()
        setupRecyclerView()
        updateStatsHeader()
        loadReadingTests()
    }

    override fun onResume() {
        super.onResume()
        updateStatsHeader()
        refreshTestsWithUserProgress()
    }

    private fun initViews() {
        rvReadingTests = findViewById(R.id.rvReadingTests)
        progressBar = findViewById(R.id.progressBar)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        tvResultCount = findViewById(R.id.tvResultCount)
        edtSearchTests = findViewById(R.id.edtSearchTests)

        // Top Stats Card Views
        tvRecentBand = findViewById(R.id.tvRecentBand)
        tvRecentBandDiff = findViewById(R.id.tvRecentBandDiff)
        tvPracticedCount = findViewById(R.id.tvPracticedCount)
        tvPracticedPassages = findViewById(R.id.tvPracticedPassages)
        tvAccuracyAvg = findViewById(R.id.tvAccuracyAvg)
        pbAccuracyAvg = findViewById(R.id.pbAccuracyAvg)

        chipAll = findViewById(R.id.chipAll)
        chipCam19 = findViewById(R.id.chipCam19)
        chipCam18 = findViewById(R.id.chipCam18)
        chipCam17 = findViewById(R.id.chipCam17)
        chipCam16 = findViewById(R.id.chipCam16)
        chipCam15 = findViewById(R.id.chipCam15)
        chipCam14 = findViewById(R.id.chipCam14)
        chipCam13 = findViewById(R.id.chipCam13)
        chipCam12 = findViewById(R.id.chipCam12)
        chipCam11 = findViewById(R.id.chipCam11)
        chipPassage = findViewById(R.id.chipPassage)
    }

    private fun updateStatsHeader() {
        val stats = ReadingStatsManager.getStats(this)

        if (stats.testsPracticed == 0) {
            tvRecentBand.text = "--"
            tvRecentBandDiff.text = "Mới"
            tvRecentBandDiff.setTextColor(Color.parseColor("#64748B"))
        } else {
            tvRecentBand.text = String.format(Locale.US, "%.1f", stats.recentBand)
            tvRecentBandDiff.text = stats.bandDiff
            when {
                stats.bandDiff.startsWith("+") && stats.bandDiff != "+0.0" -> {
                    tvRecentBandDiff.setTextColor(Color.parseColor("#16A34A")) // Green
                }
                stats.bandDiff.startsWith("-") -> {
                    tvRecentBandDiff.setTextColor(Color.parseColor("#DC2626")) // Red
                }
                else -> {
                    tvRecentBandDiff.setTextColor(Color.parseColor("#64748B")) // Slate Gray
                }
            }
        }

        tvPracticedCount.text = "${stats.testsPracticed} đề"
        tvPracticedPassages.text = "${stats.passagesPracticed} passages"
        tvAccuracyAvg.text = "${stats.accuracyAvg}%"
        pbAccuracyAvg.progress = stats.accuracyAvg.coerceIn(0, 100)
    }

    private fun refreshTestsWithUserProgress() {
        var changed = false
        for (i in allTests.indices) {
            val test = allTests[i]
            val savedProgress = ReadingStatsManager.getUserProgressForTest(this, test.id)
            if (savedProgress != null && (test.userProgress == null || test.userProgress?.highestScore != savedProgress.highestScore)) {
                allTests[i] = test.copy(userProgress = savedProgress)
                changed = true
            }
        }
        if (changed) {
            applyFilterAndSearch()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReadingTestAdapter(
            testList = emptyList(),
            onStartTest = { testItem -> onTestSelected(testItem) },
            onViewAnalysis = { testItem -> onViewAnalysisSelected(testItem) }
        )
        rvReadingTests.layoutManager = LinearLayoutManager(this)
        rvReadingTests.adapter = adapter
    }

    private fun setupListeners() {
        // Back button
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            if (isTaskRoot) {
                val intent = Intent(this, PracticeHubActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            finish()
        }

        findViewById<View>(R.id.tvSortOption)?.setOnClickListener {
            Toast.makeText(this, "Sắp xếp: Mới nhất / Điểm cao nhất", Toast.LENGTH_SHORT).show()
        }

        // Filter chips
        chipAll.setOnClickListener { selectFilter("ALL") }
        chipCam19.setOnClickListener { selectFilter("CAM19") }
        chipCam18.setOnClickListener { selectFilter("CAM18") }
        chipCam17.setOnClickListener { selectFilter("CAM17") }
        chipCam16.setOnClickListener { selectFilter("CAM16") }
        chipCam15.setOnClickListener { selectFilter("CAM15") }
        chipCam14.setOnClickListener { selectFilter("CAM14") }
        chipCam13.setOnClickListener { selectFilter("CAM13") }
        chipCam12.setOnClickListener { selectFilter("CAM12") }
        chipCam11.setOnClickListener { selectFilter("CAM11") }
        chipPassage.setOnClickListener { selectFilter("PASSAGE") }

        // Search text watcher
        edtSearchTests.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilterAndSearch()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Bottom Navigation
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.navRoadmap)?.setOnClickListener {
            Toast.makeText(this, "Tab Lộ trình học chi tiết", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.navPractice)?.setOnClickListener {
            val intent = Intent(this, PracticeHubActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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

    private fun selectFilter(filter: String) {
        currentFilter = filter

        val selectedBg = R.drawable.bg_chip_selected_pill
        val unselectedBg = R.drawable.bg_chip_unselected_pill
        val whiteColor = ContextCompat.getColor(this, R.color.white)
        val grayColor = ContextCompat.getColor(this, R.color.text_secondary)

        fun updateChip(chip: TextView, isSelected: Boolean) {
            chip.setBackgroundResource(if (isSelected) selectedBg else unselectedBg)
            chip.setTextColor(if (isSelected) whiteColor else grayColor)
        }

        updateChip(chipAll, filter == "ALL")
        updateChip(chipCam19, filter == "CAM19")
        updateChip(chipCam18, filter == "CAM18")
        updateChip(chipCam17, filter == "CAM17")
        updateChip(chipCam16, filter == "CAM16")
        updateChip(chipCam15, filter == "CAM15")
        updateChip(chipCam14, filter == "CAM14")
        updateChip(chipCam13, filter == "CAM13")
        updateChip(chipCam12, filter == "CAM12")
        updateChip(chipCam11, filter == "CAM11")
        updateChip(chipPassage, filter == "PASSAGE")

        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val query = edtSearchTests.text.toString().trim().lowercase()

        val filtered = allTests.filter { test ->
            // Filter chip check
            val matchesFilter = when (currentFilter) {
                "CAM19" -> test.title.contains("19", ignoreCase = true)
                "CAM18" -> test.title.contains("18", ignoreCase = true)
                "CAM17" -> test.title.contains("17", ignoreCase = true)
                "CAM16" -> test.title.contains("16", ignoreCase = true)
                "CAM15" -> test.title.contains("15", ignoreCase = true)
                "CAM14" -> test.title.contains("14", ignoreCase = true)
                "CAM13" -> test.title.contains("13", ignoreCase = true)
                "CAM12" -> test.title.contains("12", ignoreCase = true)
                "CAM11" -> test.title.contains("11", ignoreCase = true)
                "PASSAGE" -> true // All tests support passage practice
                else -> true
            }

            // Search query check
            val matchesQuery = if (query.isEmpty()) true else {
                test.title.lowercase().contains(query) ||
                (test.summary?.lowercase()?.contains(query) == true) ||
                test.passageTitles.any { it.lowercase().contains(query) }
            }

            matchesFilter && matchesQuery
        }

        adapter.updateData(filtered)
        tvResultCount.text = "Hiện có ${filtered.size} đề thi chuẩn"

        if (filtered.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            rvReadingTests.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            rvReadingTests.visibility = View.VISIBLE
        }
    }

    private fun loadReadingTests() {
        progressBar.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE

        // 1. Luôn nạp toàn bộ danh mục Cambridge chuẩn 11 - 19 ngay lập tức
        allTests.clear()
        addCuratedFallbackTests()
        refreshTestsWithUserProgress()
        applyFilterAndSearch()

        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val token = prefs.getString(Constants.KEY_AUTH_TOKEN, null)
        val authHeader = if (!token.isNullOrEmpty()) {
            if (token.startsWith("Bearer ")) token else "Bearer $token"
        } else null

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getReadingTests(authHeader)
                val fetchedList = if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else emptyList()

                // Ghép nối đề từ backend vào danh mục mà không xóa danh mục 36 đề Cambridge
                for (fetched in fetchedList) {
                    val idx = allTests.indexOfFirst { it.id == fetched.id || it.title == fetched.title }
                    if (idx >= 0) {
                        allTests[idx] = fetched
                    } else {
                        allTests.add(fetched)
                    }
                }

                refreshTestsWithUserProgress()
                applyFilterAndSearch()
            } catch (e: Exception) {
                // Đã có sẵn danh mục Cambridge 36 đề
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun addCuratedFallbackTests() {
        val curated = CambridgeReadingDataSource.getAllTests()
        for (test in curated) {
            val savedProgress = ReadingStatsManager.getUserProgressForTest(this, test.id)
            val progressToUse = savedProgress ?: test.userProgress
            val existingIdx = allTests.indexOfFirst { it.id == test.id }
            if (existingIdx >= 0) {
                allTests[existingIdx] = allTests[existingIdx].copy(userProgress = progressToUse)
            } else {
                allTests.add(test.copy(userProgress = progressToUse))
            }
        }
    }

    private fun onTestSelected(testItem: ReadingTestListItemDto) {
        Toast.makeText(this, "Khởi tạo bài thi: ${testItem.title} (60 phút)...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ReadingPracticeActivity::class.java).apply {
            putExtra("TEST_ID", testItem.id)
            putExtra("TEST_TITLE", testItem.title)
            putExtra("TEST_DURATION", testItem.durationMinutes)
            putExtra("TOTAL_QUESTIONS", testItem.totalQuestions)
        }
        startActivity(intent)
    }

    private fun onViewAnalysisSelected(testItem: ReadingTestListItemDto) {
        val record = ReadingStatsManager.getSavedResultForTest(this, testItem.id)
        val intent = Intent(this, ReadingResultActivity::class.java).apply {
            putExtra("TEST_ID", testItem.id)
            putExtra("TEST_TITLE", testItem.title)
            putExtra("SCORE", record?.score ?: (testItem.userProgress?.highestScore?.toInt() ?: 32))
            putExtra("TOTAL_QUESTIONS", record?.totalQuestions ?: testItem.totalQuestions)
            putExtra("BAND_ESTIMATE", record?.bandEstimate ?: (testItem.userProgress?.bandEstimate?.toDouble() ?: 7.5))
            putExtra("DURATION_SPENT", 2400)
            putExtra("RESULT_JSON", record?.resultJson)
            putExtra("PASSAGES_JSON", record?.passagesJson)
        }
        startActivity(intent)
    }
}
