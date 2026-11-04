package com.example.ielts.ui.listening

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
import com.example.ielts.data.datasource.CambridgeListeningDataSource
import com.example.ielts.data.local.ListeningStatsManager
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.ListeningTestListItemDto
import com.example.ielts.ui.auth.ProfileActivity
import com.example.ielts.ui.listening.adapter.ListeningTestAdapter
import com.example.ielts.ui.listening.dialog.ListeningStartExamDialog
import com.example.ielts.ui.practice.PracticeHubActivity
import com.example.ielts.utils.Constants
import kotlinx.coroutines.launch
import java.util.Locale

class ListeningListActivity : AppCompatActivity() {

    private lateinit var rvListeningTests: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: View
    private lateinit var tvResultCount: TextView
    private lateinit var edtSearchTests: EditText

    // Top Stats Card
    private lateinit var tvRecentBand: TextView
    private lateinit var tvRecentBandDiff: TextView
    private lateinit var tvPracticedCount: TextView
    private lateinit var tvPracticedSections: TextView
    private lateinit var tvAccuracyAvg: TextView
    private lateinit var pbAccuracyAvg: ProgressBar

    // Filter Chips
    private lateinit var chipAll: TextView
    private lateinit var chipCam19: TextView
    private lateinit var chipCam18: TextView
    private lateinit var chipCam17: TextView
    private lateinit var chipCam16: TextView
    private lateinit var chipSection: TextView

    private lateinit var adapter: ListeningTestAdapter
    private val allTests = mutableListOf<ListeningTestListItemDto>()
    private var currentFilter: String = "ALL" // ALL, CAM19, CAM18, CAM17, CAM16, SECTION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listening_list)

        initViews()
        setupListeners()
        setupRecyclerView()
        updateStatsHeader()
        loadListeningTests()
    }

    override fun onResume() {
        super.onResume()
        updateStatsHeader()
        refreshTestsWithUserProgress()
    }

    private fun initViews() {
        rvListeningTests = findViewById(R.id.rvListeningTests)
        progressBar = findViewById(R.id.progressBar)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        tvResultCount = findViewById(R.id.tvResultCount)
        edtSearchTests = findViewById(R.id.edtSearchTests)

        // Top Stats Card Views
        tvRecentBand = findViewById(R.id.tvRecentBand)
        tvRecentBandDiff = findViewById(R.id.tvRecentBandDiff)
        tvPracticedCount = findViewById(R.id.tvPracticedCount)
        tvPracticedSections = findViewById(R.id.tvPracticedSections)
        tvAccuracyAvg = findViewById(R.id.tvAccuracyAvg)
        pbAccuracyAvg = findViewById(R.id.pbAccuracyAvg)

        chipAll = findViewById(R.id.chipAll)
        chipCam19 = findViewById(R.id.chipCam19)
        chipCam18 = findViewById(R.id.chipCam18)
        chipCam17 = findViewById(R.id.chipCam17)
        chipCam16 = findViewById(R.id.chipCam16)
        chipSection = findViewById(R.id.chipSection)
    }

    private fun updateStatsHeader() {
        val stats = ListeningStatsManager.getSummaryStats(this)
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
        tvPracticedSections.text = "${stats.sectionsPracticed} sections"
        tvAccuracyAvg.text = "${stats.accuracyAvg}%"
        pbAccuracyAvg.progress = stats.accuracyAvg.coerceIn(0, 100)
    }

    private fun refreshTestsWithUserProgress() {
        var changed = false
        for (i in allTests.indices) {
            val test = allTests[i]
            val savedProgress = ListeningStatsManager.getUserProgressForTest(this, test.id)
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
        adapter = ListeningTestAdapter(
            testList = emptyList(),
            onStartTest = { testItem -> showStartExamDialog(testItem) }
        )
        rvListeningTests.layoutManager = LinearLayoutManager(this)
        rvListeningTests.adapter = adapter
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
            Toast.makeText(this, "Sắp xếp: Mới nhất", Toast.LENGTH_SHORT).show()
        }

        // Filter chips
        chipAll.setOnClickListener { selectFilter("ALL") }
        chipCam19.setOnClickListener { selectFilter("CAM19") }
        chipCam18.setOnClickListener { selectFilter("CAM18") }
        chipCam17.setOnClickListener { selectFilter("CAM17") }
        chipCam16.setOnClickListener { selectFilter("CAM16") }
        chipSection.setOnClickListener { selectFilter("SECTION") }

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
            Toast.makeText(this, "Lộ trình học cá nhân hóa", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.navPractice)?.setOnClickListener {
            val intent = Intent(this, PracticeHubActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.navVocab)?.setOnClickListener {
            Toast.makeText(this, "Kho Flashcard & Từ vựng IELTS", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.navProfile)?.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun selectFilter(filter: String) {
        currentFilter = filter
        val chips = listOf(chipAll, chipCam19, chipCam18, chipCam17, chipCam16, chipSection)
        for (chip in chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_unselected_pill)
            chip.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
        }

        val activeChip = when (filter) {
            "CAM19" -> chipCam19
            "CAM18" -> chipCam18
            "CAM17" -> chipCam17
            "CAM16" -> chipCam16
            "SECTION" -> chipSection
            else -> chipAll
        }

        activeChip.setBackgroundResource(R.drawable.bg_chip_selected_pill_purple)
        activeChip.setTextColor(Color.WHITE)

        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val query = edtSearchTests.text.toString().trim().lowercase(Locale.getDefault())

        val filtered = allTests.filter { test ->
            // Filter chip check
            val titleUpper = test.title.uppercase(Locale.getDefault())
            val matchesFilter = when (currentFilter) {
                "CAM19" -> titleUpper.contains("19")
                "CAM18" -> titleUpper.contains("18")
                "CAM17" -> titleUpper.contains("17")
                "CAM16" -> titleUpper.contains("16")
                "SECTION" -> test.sectionTitles.isNotEmpty()
                else -> true
            }

            // Search query check
            val matchesQuery = if (query.isEmpty()) true else {
                test.title.lowercase(Locale.getDefault()).contains(query) ||
                (test.summary?.lowercase(Locale.getDefault())?.contains(query) == true) ||
                test.sectionTitles.any { it.lowercase(Locale.getDefault()).contains(query) }
            }

            matchesFilter && matchesQuery
        }

        adapter.updateData(filtered)
        tvResultCount.text = "Hiện có ${filtered.size} đề thi chuẩn"

        if (filtered.isEmpty()) {
            layoutEmpty.visibility = View.VISIBLE
            rvListeningTests.visibility = View.GONE
        } else {
            layoutEmpty.visibility = View.GONE
            rvListeningTests.visibility = View.VISIBLE
        }
    }

    private fun loadListeningTests() {
        progressBar.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE

        // 1. Luôn nạp toàn bộ danh mục Cambridge chuẩn ngay lập tức (offline instant)
        allTests.clear()
        allTests.addAll(CambridgeListeningDataSource.getAllTests())
        refreshTestsWithUserProgress()
        applyFilterAndSearch()

        val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
        val token = prefs.getString(Constants.KEY_AUTH_TOKEN, null)
        val authHeader = if (!token.isNullOrEmpty()) {
            if (token.startsWith("Bearer ")) token else "Bearer $token"
        } else null

        // 2. Tải đề từ backend đồng bộ danh sách
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getListeningTests(authHeader)
                val fetchedList = if (response.isSuccessful && response.body() != null) {
                    response.body()!!
                } else emptyList()

                for (fetched in fetchedList) {
                    val idx = allTests.indexOfFirst { it.id == fetched.id || it.title == fetched.title }
                    if (idx >= 0) {
                        allTests[idx] = fetched
                    } else {
                        allTests.add(0, fetched)
                    }
                }
                refreshTestsWithUserProgress()
                applyFilterAndSearch()
            } catch (e: Exception) {
                // Giữ nguyên dữ liệu Cambridge offline
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showStartExamDialog(testItem: ListeningTestListItemDto) {
        val dialog = ListeningStartExamDialog(this, testItem) { selectedTest ->
            onExamConfirmed(selectedTest)
        }
        dialog.show()
    }

    private fun onExamConfirmed(testItem: ListeningTestListItemDto) {
        val intent = Intent(this, ListeningPracticeActivity::class.java).apply {
            putExtra("EXTRA_TEST_ID", testItem.id)
            putExtra("EXTRA_TEST_TITLE", testItem.title)
        }
        startActivity(intent)
    }
}

