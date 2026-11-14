package com.example.ielts.ui.listening

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.datasource.CambridgeListeningDataSource
import com.example.ielts.data.datasource.CambridgeListeningTestDetailDataSource
import com.example.ielts.data.local.ListeningStatsManager
import com.example.ielts.data.player.AudioPlayerListener
import com.example.ielts.data.player.ListeningAudioPlayerManager
import com.example.ielts.data.remote.dto.ListeningQuestionDetailDto
import com.example.ielts.data.remote.dto.ListeningResultReviewDto
import com.example.ielts.data.remote.dto.ListeningSectionDto
import com.example.ielts.data.remote.dto.ListeningSubmissionResultDto
import com.example.ielts.data.remote.dto.ListeningTestDetailDto
import com.example.ielts.ui.listening.adapter.ListeningResultPaletteAdapter
import com.example.ielts.ui.listening.adapter.ListeningResultReviewAdapter
import com.example.ielts.ui.practice.PracticeHubActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import java.util.Locale
import java.util.regex.Pattern

class ListeningResultActivity : AppCompatActivity(), AudioPlayerListener {

    private lateinit var scrollResultContent: NestedScrollView
    private lateinit var tvFinalBand: TextView
    private lateinit var tvCorrectRatio: TextView
    private lateinit var tvDurationSpent: TextView

    // Section mini score pills
    private lateinit var layoutSec1Score: View
    private lateinit var layoutSec2Score: View
    private lateinit var layoutSec3Score: View
    private lateinit var layoutSec4Score: View
    private lateinit var tvSec1Score: TextView
    private lateinit var tvSec2Score: TextView
    private lateinit var tvSec3Score: TextView
    private lateinit var tvSec4Score: TextView

    // Audio Controller
    private lateinit var audioSeekBar: SeekBar
    private lateinit var tvAudioTime: TextView
    private lateinit var btnPlaybackSpeed: TextView
    private lateinit var btnReplay5: ImageView
    private lateinit var btnPrevSection: ImageView
    private lateinit var btnPlayPause: ImageView
    private lateinit var btnNextSection: ImageView
    private lateinit var btnForward5: ImageView

    // Tabs
    private lateinit var tabQuestionReview: TextView
    private lateinit var tabFullTranscript: TextView
    private lateinit var layoutQuestionReviewView: View
    private lateinit var layoutTranscriptView: View

    // Section Selector Chips & Auto scroll
    private lateinit var chipSec1: TextView
    private lateinit var chipSec2: TextView
    private lateinit var chipSec3: TextView
    private lateinit var chipSec4: TextView
    private lateinit var switchAutoScroll: SwitchCompat

    // Transcript Container & Banner
    private lateinit var tvSectionBannerTitle: TextView
    private lateinit var tvSectionBannerMeta: TextView
    private lateinit var tvSectionPartBadge: TextView
    private lateinit var layoutTranscriptContainer: LinearLayout

    // Question Review
    private lateinit var rvQuestionReviews: RecyclerView
    private lateinit var reviewAdapter: ListeningResultReviewAdapter

    // Sticky Bottom Action Bar
    private lateinit var btnOpenPalette: View
    private lateinit var btnActionPrimary: TextView

    // Top Bar Buttons
    private lateinit var btnBack: View
    private lateinit var btnShare: View
    private lateinit var btnBookmark: View
    private var isBookmarked: Boolean = false

    // State & Data
    private lateinit var audioPlayerManager: ListeningAudioPlayerManager
    private var currentSectionIndex: Int = 0 // 0-based: 0 -> Sec 1, 1 -> Sec 2...
    private var isPlaying: Boolean = false
    private var playbackSpeedIndex: Int = 0
    private val playbackSpeeds = floatArrayOf(1.0f, 1.25f, 1.5f, 0.75f)
    private val speedLabels = arrayOf("1.0x", "1.25x", "1.5x", "0.75x")
    private var isUserTrackingSeekBar: Boolean = false

    private var testDetail: ListeningTestDetailDto? = null
    private var reviewQuestions: List<ListeningResultReviewDto> = emptyList()
    private var transcriptParagraphs: List<TranscriptParagraph> = emptyList()
    private val transcriptCardViews: MutableList<View> = mutableListOf()
    private var activeParagraphIndex: Int = -1

    data class TranscriptParagraph(
        val timestampSeconds: Int,
        val timestampStr: String,
        val speaker: String,
        val text: String,
        val questionBadge: String? = null,
        val targetQuestionNumber: Int? = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listening_result)

        initViews()
        initAudioPlayer()
        loadDataFromIntent()
        setupListeners()

        // Default open Section 1 transcript view
        selectSection(0, autoPlay = false)

        // Kích hoạt chuỗi Streak & hiện popup chúc mừng khi hoàn thành bài Listening
        val streakResult = com.example.ielts.data.local.StreakManager.recordActivity(this, "LISTENING")
        com.example.ielts.ui.streak.StreakCelebrationDialog(
            this,
            streakResult.newStreak,
            isFirstToday = streakResult.isFirstActivityToday
        ).show()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnShare = findViewById(R.id.btnShare)
        btnBookmark = findViewById(R.id.btnBookmark)

        scrollResultContent = findViewById(R.id.scrollResultContent)
        tvFinalBand = findViewById(R.id.tvFinalBand)
        tvCorrectRatio = findViewById(R.id.tvCorrectRatio)
        tvDurationSpent = findViewById(R.id.tvDurationSpent)

        layoutSec1Score = findViewById(R.id.layoutSec1Score)
        layoutSec2Score = findViewById(R.id.layoutSec2Score)
        layoutSec3Score = findViewById(R.id.layoutSec3Score)
        layoutSec4Score = findViewById(R.id.layoutSec4Score)
        tvSec1Score = findViewById(R.id.tvSec1Score)
        tvSec2Score = findViewById(R.id.tvSec2Score)
        tvSec3Score = findViewById(R.id.tvSec3Score)
        tvSec4Score = findViewById(R.id.tvSec4Score)

        audioSeekBar = findViewById(R.id.audioSeekBar)
        tvAudioTime = findViewById(R.id.tvAudioTime)
        btnPlaybackSpeed = findViewById(R.id.btnPlaybackSpeed)
        btnReplay5 = findViewById(R.id.btnReplay5)
        btnPrevSection = findViewById(R.id.btnPrevSection)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnNextSection = findViewById(R.id.btnNextSection)
        btnForward5 = findViewById(R.id.btnForward5)

        tabQuestionReview = findViewById(R.id.tabQuestionReview)
        tabFullTranscript = findViewById(R.id.tabFullTranscript)
        layoutQuestionReviewView = findViewById(R.id.layoutQuestionReviewView)
        layoutTranscriptView = findViewById(R.id.layoutTranscriptView)

        chipSec1 = findViewById(R.id.chipSec1)
        chipSec2 = findViewById(R.id.chipSec2)
        chipSec3 = findViewById(R.id.chipSec3)
        chipSec4 = findViewById(R.id.chipSec4)
        switchAutoScroll = findViewById(R.id.switchAutoScroll)

        tvSectionBannerTitle = findViewById(R.id.tvSectionBannerTitle)
        tvSectionBannerMeta = findViewById(R.id.tvSectionBannerMeta)
        tvSectionPartBadge = findViewById(R.id.tvSectionPartBadge)
        layoutTranscriptContainer = findViewById(R.id.layoutTranscriptContainer)

        rvQuestionReviews = findViewById(R.id.rvQuestionReviews)
        btnOpenPalette = findViewById(R.id.btnOpenPalette)
        btnActionPrimary = findViewById(R.id.btnActionPrimary)
    }

    private fun initAudioPlayer() {
        audioPlayerManager = ListeningAudioPlayerManager(this)
        audioPlayerManager.setListener(this)

        audioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = audioPlayerManager.getDuration()
                    val targetMs = if (duration > 0) (progress.toLong() * duration / 1000L) else 0L
                    tvAudioTime.text = "${formatDuration(targetMs)} / ${formatDuration(duration)}"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserTrackingSeekBar = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserTrackingSeekBar = false
                val progress = seekBar?.progress ?: 0
                val duration = audioPlayerManager.getDuration()
                val targetMs = if (duration > 0) (progress.toLong() * duration / 1000L) else 0L
                audioPlayerManager.seekTo(targetMs)
            }
        })
    }

    private fun loadDataFromIntent() {
        val testId = intent.getStringExtra("TEST_ID") ?: "cam18-listening-test1"
        val testTitle = intent.getStringExtra("TEST_TITLE") ?: "Cam 18 - Listening Test 1"
        val score = intent.getIntExtra("SCORE", 32)
        val total = intent.getIntExtra("TOTAL_QUESTIONS", 40)
        val band = intent.getDoubleExtra("BAND_ESTIMATE", 7.5)
        val durationSpent = intent.getIntExtra("DURATION_SPENT", 1935)
        val resultJson = intent.getStringExtra("RESULT_JSON")

        testDetail = CambridgeListeningTestDetailDataSource.getTestDetail(testId, testTitle)

        // 1. Hero Card Display
        tvFinalBand.text = String.format(Locale.US, "%.1f", band)
        val accuracyPercent = if (total > 0) (score * 100 / total) else 0
        tvCorrectRatio.text = "$score/$total Đúng ($accuracyPercent%)"

        val minutes = durationSpent / 60
        val seconds = durationSpent % 60
        tvDurationSpent.text = "Thời gian: %02d:%02d".format(minutes, seconds)

        // 2. Prepare 40 review questions
        reviewQuestions = parseOrGenerateReviewQuestions(resultJson, score, total)

        // 3. Section breakdown scores
        val sec1Correct = reviewQuestions.filter { it.sectionNumber == 1 }.count { it.isCorrect }
        val sec2Correct = reviewQuestions.filter { it.sectionNumber == 2 }.count { it.isCorrect }
        val sec3Correct = reviewQuestions.filter { it.sectionNumber == 3 }.count { it.isCorrect }
        val sec4Correct = reviewQuestions.filter { it.sectionNumber == 4 }.count { it.isCorrect }

        tvSec1Score.text = "$sec1Correct/10"
        tvSec2Score.text = "$sec2Correct/10"
        tvSec3Score.text = "$sec3Correct/10"
        tvSec4Score.text = "$sec4Correct/10"

        // 4. Setup Question Review RecyclerView
        reviewAdapter = ListeningResultReviewAdapter(reviewQuestions) { item ->
            // Listen at evidence clicked
            val sectionIdx = (item.sectionNumber - 1).coerceIn(0, 3)
            if (currentSectionIndex != sectionIdx) {
                selectSection(sectionIdx, autoPlay = true)
            } else if (!isPlaying) {
                audioPlayerManager.togglePlayPause()
            }
            if (item.timestampSeconds > 0) {
                audioPlayerManager.seekTo(item.timestampSeconds * 1000L)
            }
            Toast.makeText(this, "Đang phát đoạn chứa đáp án câu ${item.position}", Toast.LENGTH_SHORT).show()
        }
        rvQuestionReviews.layoutManager = LinearLayoutManager(this)
        rvQuestionReviews.adapter = reviewAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Kết quả luyện thi IELTS Listening")
                putExtra(Intent.EXTRA_TEXT, "Mình vừa hoàn thành bài IELTS Listening đạt Band ${tvFinalBand.text} (${tvCorrectRatio.text})! 🎯")
            }
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ kết quả"))
        }

        btnBookmark.setOnClickListener {
            isBookmarked = !isBookmarked
            if (isBookmarked) {
                (btnBookmark as ImageView).setImageResource(R.drawable.ic_bookmark)
                (btnBookmark as ImageView).setColorFilter(Color.parseColor("#2563EB"))
                Toast.makeText(this, "Đã lưu kết quả bài thi vào mục Yêu thích", Toast.LENGTH_SHORT).show()
            } else {
                (btnBookmark as ImageView).setImageResource(R.drawable.ic_bookmark_border)
                (btnBookmark as ImageView).setColorFilter(Color.parseColor("#434655"))
                Toast.makeText(this, "Đã bỏ lưu bài thi", Toast.LENGTH_SHORT).show()
            }
        }

        // Section Mini Pills Click
        layoutSec1Score.setOnClickListener { selectSection(0, autoPlay = true) }
        layoutSec2Score.setOnClickListener { selectSection(1, autoPlay = true) }
        layoutSec3Score.setOnClickListener { selectSection(2, autoPlay = true) }
        layoutSec4Score.setOnClickListener { selectSection(3, autoPlay = true) }

        // Audio Controls
        btnPlayPause.setOnClickListener {
            audioPlayerManager.togglePlayPause()
        }

        btnReplay5.setOnClickListener {
            audioPlayerManager.seekRelative(-5000L)
        }

        btnForward5.setOnClickListener {
            audioPlayerManager.seekRelative(5000L)
        }

        btnPrevSection.setOnClickListener {
            if (currentSectionIndex > 0) {
                selectSection(currentSectionIndex - 1, autoPlay = true)
            }
        }

        btnNextSection.setOnClickListener {
            val maxSections = (testDetail?.sections?.size ?: 4) - 1
            if (currentSectionIndex < maxSections) {
                selectSection(currentSectionIndex + 1, autoPlay = true)
            }
        }

        btnPlaybackSpeed.setOnClickListener {
            playbackSpeedIndex = (playbackSpeedIndex + 1) % playbackSpeeds.size
            val newSpeed = playbackSpeeds[playbackSpeedIndex]
            audioPlayerManager.setPlaybackSpeed(newSpeed)
            btnPlaybackSpeed.text = speedLabels[playbackSpeedIndex]
        }

        // 2-Tab Segmented Bar
        tabQuestionReview.setOnClickListener {
            switchToTab(isTranscriptTab = false)
        }

        tabFullTranscript.setOnClickListener {
            switchToTab(isTranscriptTab = true)
        }

        // Section Chips
        chipSec1.setOnClickListener { selectSection(0, autoPlay = true) }
        chipSec2.setOnClickListener { selectSection(1, autoPlay = true) }
        chipSec3.setOnClickListener { selectSection(2, autoPlay = true) }
        chipSec4.setOnClickListener { selectSection(3, autoPlay = true) }

        // Sticky Bottom Action Bar
        btnOpenPalette.setOnClickListener {
            show40QuestionsPalette()
        }

        btnActionPrimary.setOnClickListener {
            val isCurrentlyTranscript = layoutTranscriptView.visibility == View.VISIBLE
            switchToTab(isTranscriptTab = !isCurrentlyTranscript)
        }
    }

    private fun switchToTab(isTranscriptTab: Boolean) {
        if (isTranscriptTab) {
            tabFullTranscript.setBackgroundResource(R.drawable.bg_result_tab_selected)
            tabFullTranscript.setTextColor(Color.WHITE)

            tabQuestionReview.setBackgroundColor(Color.TRANSPARENT)
            tabQuestionReview.setTextColor(Color.parseColor("#434655"))

            layoutTranscriptView.visibility = View.VISIBLE
            layoutQuestionReviewView.visibility = View.GONE
            btnActionPrimary.text = "Về xem câu hỏi"
        } else {
            tabQuestionReview.setBackgroundResource(R.drawable.bg_result_tab_selected)
            tabQuestionReview.setTextColor(Color.WHITE)

            tabFullTranscript.setBackgroundColor(Color.TRANSPARENT)
            tabFullTranscript.setTextColor(Color.parseColor("#434655"))

            layoutQuestionReviewView.visibility = View.VISIBLE
            layoutTranscriptView.visibility = View.GONE
            btnActionPrimary.text = "Transcript toàn bài"
        }
    }

    private fun selectSection(index: Int, autoPlay: Boolean) {
        val sections = testDetail?.sections ?: emptyList()
        if (sections.isEmpty() || index < 0 || index >= sections.size) return

        currentSectionIndex = index
        val section = sections[index]

        // Update Section Mini Pills in Hero Card
        val activePillBg = R.drawable.bg_result_section_pill_active
        val defaultPillBg = R.drawable.bg_result_section_pill
        layoutSec1Score.setBackgroundResource(if (index == 0) activePillBg else defaultPillBg)
        layoutSec2Score.setBackgroundResource(if (index == 1) activePillBg else defaultPillBg)
        layoutSec3Score.setBackgroundResource(if (index == 2) activePillBg else defaultPillBg)
        layoutSec4Score.setBackgroundResource(if (index == 3) activePillBg else defaultPillBg)

        // Update Horizontal Chips
        val activeChipBg = R.drawable.bg_chip_selected_pill_blue
        val defaultChipBg = R.drawable.bg_chip_unselected_pill
        chipSec1.setBackgroundResource(if (index == 0) activeChipBg else defaultChipBg)
        chipSec1.setTextColor(if (index == 0) Color.WHITE else Color.parseColor("#434655"))
        chipSec1.text = if (index == 0) "Sec 1 •" else "Sec 1"

        chipSec2.setBackgroundResource(if (index == 1) activeChipBg else defaultChipBg)
        chipSec2.setTextColor(if (index == 1) Color.WHITE else Color.parseColor("#434655"))
        chipSec2.text = if (index == 1) "Sec 2 •" else "Sec 2"

        chipSec3.setBackgroundResource(if (index == 2) activeChipBg else defaultChipBg)
        chipSec3.setTextColor(if (index == 2) Color.WHITE else Color.parseColor("#434655"))
        chipSec3.text = if (index == 2) "Sec 3 •" else "Sec 3"

        chipSec4.setBackgroundResource(if (index == 3) activeChipBg else defaultChipBg)
        chipSec4.setTextColor(if (index == 3) Color.WHITE else Color.parseColor("#434655"))
        chipSec4.text = if (index == 3) "Sec 4 •" else "Sec 4"

        // Update Banner
        tvSectionBannerTitle.text = section.title
        val qRange = when (section.sectionNumber) {
            1 -> "10 Câu hỏi (01 - 10)"
            2 -> "10 Câu hỏi (11 - 20)"
            3 -> "10 Câu hỏi (21 - 30)"
            else -> "10 Câu hỏi (31 - 40)"
        }
        val durationMins = section.durationSeconds / 60
        val durationSecs = section.durationSeconds % 60
        tvSectionBannerMeta.text = "$qRange • Thời lượng: %02d:%02d".format(durationMins, durationSecs)
        tvSectionPartBadge.text = "Part ${section.sectionNumber}"

        // Build Transcripts
        buildTranscriptParagraphs(section)

        // Play section audio
        if (section.audioUrl.isNotBlank()) {
            audioPlayerManager.playUrl(section.audioUrl, autoPlay = autoPlay)
        }
    }

    private fun buildTranscriptParagraphs(section: ListeningSectionDto) {
        layoutTranscriptContainer.removeAllViews()
        transcriptCardViews.clear()
        activeParagraphIndex = -1

        val raw = section.transcript ?: ""
        val lines = raw.split("\n").filter { it.isNotBlank() }
        val parsedList = mutableListOf<TranscriptParagraph>()

        val pattern = Pattern.compile("^\\[(\\d{2}):(\\d{2})\\]\\s*([^:]+):\\s*(.*)$")

        for (line in lines) {
            val matcher = pattern.matcher(line.trim())
            if (matcher.find()) {
                val mm = matcher.group(1)?.toIntOrNull() ?: 0
                val ss = matcher.group(2)?.toIntOrNull() ?: 0
                val totalSec = mm * 60 + ss
                val timeStr = "%02d:%02d".format(mm, ss)
                val speaker = matcher.group(3)?.trim() ?: "Speaker"
                val text = matcher.group(4)?.trim() ?: ""

                // Detect answer evidence tag like [Q1] or [Q11]
                var badge: String? = null
                var qNum: Int? = null
                val qMatch = Pattern.compile("\\[Q(\\d+)(?:-(\\d+))?\\]").matcher(text)
                if (qMatch.find()) {
                    val q1 = qMatch.group(1)?.toIntOrNull()
                    val q2 = qMatch.group(2)?.toIntOrNull()
                    qNum = q1
                    badge = if (q2 != null) "Q$q1-$q2 Đáp án" else "Q$q1 Đáp án"
                } else {
                    // Fallback check question timestamps
                    val foundQ = reviewQuestions.firstOrNull {
                        it.sectionNumber == section.sectionNumber && Math.abs(it.timestampSeconds - totalSec) <= 4
                    }
                    if (foundQ != null) {
                        qNum = foundQ.position
                        badge = "Q${foundQ.position} Đáp án"
                    }
                }

                val cleanText = text.replace(Regex("\\[Q\\d+(-\\d+)?\\]"), "").trim()

                parsedList.add(
                    TranscriptParagraph(
                        timestampSeconds = totalSec,
                        timestampStr = timeStr,
                        speaker = speaker,
                        text = cleanText,
                        questionBadge = badge,
                        targetQuestionNumber = qNum
                    )
                )
            }
        }

        transcriptParagraphs = parsedList

        val inflater = LayoutInflater.from(this)
        for ((idx, para) in transcriptParagraphs.withIndex()) {
            val cardView = inflater.inflate(R.layout.item_listening_result_transcript_card, layoutTranscriptContainer, false)

            val tvSpeaker = cardView.findViewById<TextView>(R.id.tvTranscriptSpeaker)
            val tvTimestamp = cardView.findViewById<TextView>(R.id.tvTranscriptTimestamp)
            val tvBadge = cardView.findViewById<TextView>(R.id.tvAnswerBadge)
            val tvText = cardView.findViewById<TextView>(R.id.tvTranscriptText)
            val layoutActions = cardView.findViewById<View>(R.id.layoutTranscriptActions)
            val btnJump = cardView.findViewById<View>(R.id.btnJumpToQuestion)
            val tvJumpText = cardView.findViewById<TextView>(R.id.tvJumpQuestionText)

            tvSpeaker.text = para.speaker
            tvTimestamp.text = para.timestampStr
            tvText.text = para.text

            if (para.questionBadge != null) {
                tvBadge.visibility = View.VISIBLE
                tvBadge.text = para.questionBadge
                layoutActions.visibility = View.VISIBLE
                if (para.targetQuestionNumber != null) {
                    tvJumpText.text = "Xem câu hỏi ${para.targetQuestionNumber}"
                    btnJump.setOnClickListener {
                        switchToTab(isTranscriptTab = false)
                        scrollToQuestion(para.targetQuestionNumber)
                    }
                } else {
                    btnJump.visibility = View.GONE
                }
            } else {
                tvBadge.visibility = View.GONE
                layoutActions.visibility = View.GONE
            }

            cardView.setOnClickListener {
                audioPlayerManager.seekTo(para.timestampSeconds * 1000L)
                if (!isPlaying) {
                    audioPlayerManager.togglePlayPause()
                }
                highlightParagraph(idx, smoothScroll = false)
            }

            layoutTranscriptContainer.addView(cardView)
            transcriptCardViews.add(cardView)
        }
    }

    private fun highlightParagraph(activeIdx: Int, smoothScroll: Boolean) {
        if (activeIdx == activeParagraphIndex || activeIdx < 0 || activeIdx >= transcriptCardViews.size) return
        activeParagraphIndex = activeIdx

        for ((idx, view) in transcriptCardViews.withIndex()) {
            val layoutCard = view.findViewById<View>(R.id.layoutTranscriptCard)
            val indicatorLine = view.findViewById<View>(R.id.viewActiveIndicatorLine)
            val imgPlaying = view.findViewById<View>(R.id.imgPlayingIndicator)
            val tvStatus = view.findViewById<View>(R.id.tvPlayingStatus)
            val tvSpeaker = view.findViewById<TextView>(R.id.tvTranscriptSpeaker)
            val layoutActions = view.findViewById<View>(R.id.layoutTranscriptActions)

            if (idx == activeIdx) {
                layoutCard.setBackgroundResource(R.drawable.bg_result_transcript_card_active)
                indicatorLine.visibility = View.VISIBLE
                imgPlaying.visibility = View.VISIBLE
                tvStatus.visibility = View.VISIBLE
                layoutActions.visibility = View.VISIBLE
                tvSpeaker.setTextColor(Color.parseColor("#2563EB"))
            } else {
                layoutCard.setBackgroundResource(R.drawable.bg_result_transcript_card)
                indicatorLine.visibility = View.GONE
                imgPlaying.visibility = View.GONE
                tvStatus.visibility = View.GONE
                tvSpeaker.setTextColor(Color.parseColor("#131B2E"))
                if (transcriptParagraphs.getOrNull(idx)?.questionBadge == null) {
                    layoutActions.visibility = View.GONE
                }
            }
        }

        if (smoothScroll && switchAutoScroll.isChecked) {
            val targetView = transcriptCardViews[activeIdx]
            val targetY = layoutTranscriptContainer.top + targetView.top
            scrollResultContent.smoothScrollTo(0, targetY)
        }
    }

    private fun show40QuestionsPalette() {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_listening_result_palette, null)
        dialog.setContentView(view)

        val tvCorrect = view.findViewById<TextView>(R.id.tvPaletteLegendCorrect)
        val tvIncorrect = view.findViewById<TextView>(R.id.tvPaletteLegendIncorrect)
        val tvUnanswered = view.findViewById<TextView>(R.id.tvPaletteLegendUnanswered)
        val btnClose = view.findViewById<ImageView>(R.id.btnClosePalette)
        val rvGrid = view.findViewById<RecyclerView>(R.id.rvPaletteGrid)

        val correctCount = reviewQuestions.count { it.isCorrect }
        val incorrectCount = reviewQuestions.count { !it.isCorrect && !it.userAnswer.isNullOrBlank() }
        val unansweredCount = reviewQuestions.count { it.userAnswer.isNullOrBlank() }

        tvCorrect.text = "Đúng ($correctCount)"
        tvIncorrect.text = "Sai ($incorrectCount)"
        tvUnanswered.text = "Chưa làm ($unansweredCount)"

        rvGrid.layoutManager = GridLayoutManager(this, 5)
        rvGrid.adapter = ListeningResultPaletteAdapter(reviewQuestions) { qPos ->
            dialog.dismiss()
            switchToTab(isTranscriptTab = false)
            scrollToQuestion(qPos)
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun scrollToQuestion(questionPosition: Int) {
        val index = questionPosition - 1
        if (index < 0 || index >= reviewQuestions.size) return

        rvQuestionReviews.post {
            val targetView = rvQuestionReviews.layoutManager?.findViewByPosition(index)
            if (targetView != null) {
                val targetY = layoutQuestionReviewView.top + rvQuestionReviews.top + targetView.top
                scrollResultContent.smoothScrollTo(0, targetY)
            } else {
                rvQuestionReviews.scrollToPosition(index)
                val estimatedY = layoutQuestionReviewView.top + rvQuestionReviews.top + (index * 220)
                scrollResultContent.smoothScrollTo(0, estimatedY)
            }
        }
    }

    private fun parseOrGenerateReviewQuestions(json: String?, score: Int, total: Int): List<ListeningResultReviewDto> {
        val list = mutableListOf<ListeningResultReviewDto>()

        if (!json.isNullOrBlank()) {
            try {
                val resultDto = Gson().fromJson(json, ListeningSubmissionResultDto::class.java)
                if (resultDto.results.isNotEmpty()) {
                    return resultDto.results
                }
            } catch (_: Exception) {}
        }

        val isTest4 = testDetail?.id?.contains("test4") == true || testDetail?.title?.contains("Test 4") == true
        val isTest3 = testDetail?.id?.contains("test3") == true || testDetail?.title?.contains("Test 3") == true
        val isTest2 = testDetail?.id?.contains("test2") == true || testDetail?.title?.contains("Test 2") == true

        // Answers & evidence
        val defaultAnswers = if (isTest4) {
            listOf(
                "receptionist", "medical", "Chastons", "appointments", "database",
                "experience", "confident", "temporary", "1.15", "parking",
                "B", "C", "B", "C", "C",
                "B", "B", "A", "C", "B",
                "A", "E", "B", "D", "G",
                "E", "B", "C", "F", "A",
                "plot", "poverty", "Europe", "poetry", "drawings",
                "furniture", "lamps", "harbour", "children", "relatives"
            )
        } else if (isTest3) {
            listOf(
                "Marrowfield", "relative", "socialise", "full", "Domestic life",
                "clouds", "timing", "Animal Magic", "movement", "dark",
                "B", "C", "B", "D", "C",
                "B", "B", "C", "A", "A",
                "A", "E", "B", "D", "G",
                "E", "B", "C", "F", "A",
                "technical", "cheap", "thousands", "identification", "tracking",
                "military", "location", "prediction", "database", "trust"
            )
        } else if (isTest2) {
            listOf(
                "training", "discount", "taxi", "service", "English",
                "Wivenhoe", "equipment", "9.75", "deliveries", "Sunday",
                "B", "E", "B", "C", "G",
                "C", "D", "B", "H", "A",
                "C", "A", "B", "B", "A",
                "B", "D", "A", "C", "F",
                "convenient", "suits", "tailor", "profession", "visible",
                "string", "waist", "perfume", "image", "handbag"
            )
        } else {
            listOf(
                "DW30 7YZ", "24th April", "dentist", "parking", "Claxby",
                "late", "evening", "supermarket", "pollution", "storage",
                "C", "A", "A", "B", "E",
                "B", "G", "D", "A", "F",
                "A", "B", "A", "C", "B",
                "A", "B", "E", "A", "C",
                "fences", "family", "helicopters", "stress", "sides",
                "breathing", "feet", "employment", "weapons", "tourism"
            )
        }

        val defaultQuotes = if (isTest4) {
            listOf(
                "It's a receptionist job. I believe you've done similar work before, haven't you?",
                "The job is in Fordham, so it shouldn't be too far for you to travel. You'll be working at the local medical centre.",
                "It's near the railway station, on Chastons Road. That's spelled C-H-A-S-T-O-N-S.",
                "You would also arrange appointments, either in person or over the telephone, and change appointment times when necessary.",
                "Another responsibility would be updating the medical centre's database.",
                "The employer specifically needs somebody with previous experience, and I remembered your work at the sports centre.",
                "They need somebody who can remain calm and communicate confidently with members of the public.",
                "However, it's a temporary position rather than a permanent one.",
                "What time would I finish? At 1:15 p.m.",
                "Is there a car park for employees at the medical centre? Yes, staff can park there.",
                "Dẫn chứng câu 11", "Dẫn chứng câu 12", "Dẫn chứng câu 13", "Dẫn chứng câu 14", "Dẫn chứng câu 15",
                "Dẫn chứng câu 16", "Dẫn chứng câu 17", "Dẫn chứng câu 18", "Dẫn chứng câu 19", "Dẫn chứng câu 20",
                "Dẫn chứng câu 21", "Dẫn chứng câu 22", "Dẫn chứng câu 23", "Dẫn chứng câu 24", "Dẫn chứng câu 25",
                "Dẫn chứng câu 26", "Dẫn chứng câu 27", "Dẫn chứng câu 28", "Dẫn chứng câu 29", "Dẫn chứng câu 30",
                "Dẫn chứng câu 31", "Dẫn chứng câu 32", "Dẫn chứng câu 33", "Dẫn chứng câu 34", "Dẫn chứng câu 35",
                "Dẫn chứng câu 36", "Dẫn chứng câu 37", "Dẫn chứng câu 38", "Dẫn chứng câu 39", "Dẫn chứng câu 40"
            )
        } else if (isTest3) {
            listOf(
                "It's number 52, Marrowfield Street.",
                "Actually, a relative told me about it.",
                "I'd also like to meet other people who share my interest in photography.",
                "I think I'd prefer the full membership.",
                "The theme was Domestic Life. I submitted a photograph of a family sitting around a table and having dinner together.",
                "The judges felt that the photograph would have been better if there had been some clouds in the sky.",
                "They also thought I'd taken the photograph too early. I should have waited longer to capture the most impressive moment.",
                "It was called Animal Magic.",
                "There were quite a lot of photographs of animals, which can be difficult because animals don't always stay still.",
                "I managed to take a nice photograph of a fox, but the problem was that the picture was taken at night and it came out too dark.",
                "You should also avoid collecting mushrooms growing close to busy roads.",
                "You should never eat mushrooms collected by friends or neighbours unless you can identify them yourself.",
                "The fact that animals eat a particular mushroom doesn't mean that it is safe for humans, either.",
                "Some mushrooms that are perfectly safe to eat have bright colours, including red ones.",
                "It's best to go early in the morning. Other people or animals may have collected the mushrooms if you arrive later in the day.",
                "A much better option is to join a group led by someone with experience.",
                "Don't collect every mushroom you find. Only take what you actually need",
                "building developments and the removal of water from natural areas can damage mushroom habitats.",
                "put them in a brown paper bag and keep them in the refrigerator. You should use them within a few days",
                "There are plenty of ways to cook wild mushrooms. You can add them to stir-fries, risotto or pasta dishes.",
                "Their protests weren't successful in stopping industrialisation.",
                "I can understand why those workers felt threatened. They had good reasons to worry about their future.",
                "I also doubt that unemployment will decrease in the near future.",
                "people often predict that technology will give us more free time, but the evidence seems to suggest that working hours are actually increasing.",
                "Although computers have changed the way they work, the number of people employed in accounting has actually increased. Technology allows them to complete their tasks more efficiently",
                "That may be because people have higher incomes and can afford to spend more money on personal services.",
                "administrative employees often have greater responsibilities and a higher professional status.",
                "quarter of the population worked on farms. That's a huge difference from today, when the proportion is less than 0.2 per cent.",
                "As people live longer, demand for care workers is expected to increase.",
                "technology may eventually make many traditional banking tasks unnecessary. It's difficult to predict exactly what will happen to those jobs in the future.",
                "To achieve this, we need both legal regulations and suitable technical systems.",
                "launching satellites has become much less expensive, allowing more organisations to send objects into space.",
                "constellations, of thousands of satellites that operate together rather than relying on individual satellites.",
                "satellites do not always send information identifying themselves back to Earth.",
                "Current satellite-tracking methods are not yet sufficient to guarantee safety and prevent every possible collision.",
                "They may have military or commercial reasons for keeping that information private.",
                "We need to know the characteristics of each object and where it is located at any particular moment.",
                "scientists have to calculate and predict the future paths of objects in orbit.",
                "Another proposal is to create a shared database containing information about objects in space.",
                "Finally, cooperation depends on trust. This needs to exist not only between individual organisations but also at national and international levels."
            )
        } else if (isTest2) {
            listOf(
                "all employees get training, even temporary staff.",
                "you can get a discount at any of their restaurants.",
                "the company will pay for you to get a taxi home.",
                "We had to make sure we gave a really high level of service.",
                "good communication skills, so you'll need to have a certificate in English.",
                "The first one is in Wivenhoe Street.",
                "checking all the procedures for cleaning the equipment are being followed.",
                "to begin with, you'd be getting £9.75",
                "making sure there's enough stock each week and sorting out all the deliveries.",
                "you do get one Sunday off every four weeks",
                "because of the new industrial centre in Nunston, there's a lot of demand for housing for employees",
                "excellent hospital just 15 km away, and a large secondary school even closer",
                "blended in well with the natural features of the landscape, with provision made for protecting trees and wildlife",
                "place where people of all ages can live together happily.",
                "The school will be on that road, at the corner of the second turning to the left.",
                "A large sports centre is planned... western side of the development, just below the road that branches off",
                "The clinic will be just below this, to the right of a street of houses.",
                "row of specially designed houses... community centre will be adjoining this.",
                "There'll be a supermarket between the two entrances... just to the south of these.",
                "road that goes up from the south entrance... curves round to the left... playground will be in that curve",
                "impacted directly and indirectly on political events, as well as having massive social and economic consequences.",
                "surprised there were so many weather stations established by that time.",
                "Reports from the period blamed the haze for an increase in headaches, respiratory issues and asthma attacks.",
                "It was Benjamin Franklin who realised that... But a French naturalist beat him to it.",
                "volcanic ash drifted so swiftly, but not that the effects would go on for so long.",
                "strange weather events were being reported as far away as North America and North Africa.",
                "They were poisoned because they ate vegetation that had been contaminated with fluorine",
                "the famine there led to more people dying than any other country.",
                "included an unusually high percentage of people under the age of 25.",
                "snowbound until March in Virginia... ice floating down the Mississippi, which was unprecedented.",
                "convenient", "suits", "tailor", "profession", "visible",
                "string", "waist", "perfume", "image", "handbag"
            )
        } else {
            listOf(
                "It's DW3Q 7YZ.", "It's the 24th of April, isn't it?", "the main reason I came here was to go to the dentist.",
                "parking was so difficult to find and it costs so much.", "At the bus stop on Claxby Street.",
                "this morning it was late. Only about 10 minutes, but still.", "any time I've been in town in the evening... wait a long time",
                "Mainly just to go to the supermarket.", "because of all the pollution.", "it doesn't have any storage.",
                "people at the back of the room may be a bit squashed", "Other considerations, such as reliability, are crucial in voluntary work and age isn't related to these",
                "training doesn't end when you start working for us. It takes place before, during and after", "What we value is dedication.", "critical that you have enough hours in the day for whatever role we agree is suitable for you",
                "creativity to come up with an imaginative or novel way of fundraising", "useful if you can walk for long periods, sometimes uphill.",
                "good if you know something about nutrition and can give clear instructions.", "helping out at our Story Club for disabled children, especially if you have done some acting.",
                "take in a lot of information and not forget any important steps or details.",
                "hard to see through people's heads, isn't it?", "there's a whole range of … areas of work that we hadn't even thought of",
                "we're only first years, after all. We've got a lot to learn.", "none of them were very inspiring. They could have asked more people like today's speaker",
                "promised myself that I'd go through this course and keep an open mind till the end.", "we'll be unpaid assistants in the industry for quite a long time... I'm prepared for that",
                "more interested in her own appearance than his!", "she should have hidden her negative feelings about him, but she didn't.",
                "garments sold there were being returned because they fell apart in the wash", "an item that no one's stocking but that consumers are looking for",
                "Elephants were routinely knocking down fences around the park", "all belonging to one family. Because relationships are very important",
                "vets and park rangers flew over the park in helicopters", "done as quickly as possible so as to minimise the stress caused.",
                "all the elephants had to be placed on their sides.",
                "keep an eye on their breathing – if there were fewer than six breaths per minute", "Measurements were taken of each elephant's tusks... and also of their feet.",
                "Employment prospects have improved enormously, contributing to rising living standards", "many of them volunteered to give up their weapons",
                "All this has been a big draw for tourism, which contributes five times more than the illegal wildlife trade to GDP"
            )
        }

        val timestamps = if (isTest4) {
            listOf(
                106, 122, 134, 158, 169, 238, 258, 303, 337, 348,
                60, 90, 120, 150, 180, 210, 240, 270, 300, 330,
                60, 90, 120, 150, 180, 210, 240, 270, 300, 330,
                60, 90, 120, 150, 180, 210, 240, 270, 300, 330
            )
        } else if (isTest3) {
            listOf(
                124, 146, 178, 195, 266, 323, 340, 355, 359, 375,
                82, 82, 174, 128, 262, 299, 308, 338, 369, 390,
                76, 84, 122, 111, 193, 228, 254, 277, 283, 305,
                117, 132, 153, 175, 189, 232, 251, 282, 336, 364
            )
        } else if (isTest2) {
            listOf(
                99, 115, 136, 172, 204, 254, 288, 316, 361, 441,
                79, 112, 159, 179, 299, 308, 333, 340, 362, 370,
                106, 134, 163, 200, 226, 242, 313, 330, 353, 368,
                87, 125, 150, 170, 193, 223, 245, 261, 309, 325
            )
        } else {
            listOf(
                121, 154, 173, 192, 211, 273, 297, 308, 332, 345,
                78, 111, 130, 196, 168, 251, 269, 303, 312, 345,
                90, 119, 160, 174, 204, 238, 289, 314, 345, 366,
                143, 195, 210, 242, 256, 277, 289, 316, 336, 381
            )
        }

        val questionsFromDetail = testDetail?.questions ?: emptyList()

        for (i in 1..40) {
            val qDetail = questionsFromDetail.find { it.position == i }
            val secNum = when {
                i <= 10 -> 1
                i <= 20 -> 2
                i <= 30 -> 3
                else -> 4
            }
            val isCorrect = i <= score
            val ans = defaultAnswers.getOrElse(i - 1) { "A" }
            val quote = defaultQuotes.getOrElse(i - 1) { "Dẫn chứng trong đoạn nghe Section $secNum." }
            val timeSec = timestamps.getOrElse(i - 1) { 30 }
            val timeStr = "%02d:%02d".format(timeSec / 60, timeSec % 60)

            list.add(
                ListeningResultReviewDto(
                    questionId = qDetail?.id ?: "q_$i",
                    position = i,
                    userAnswer = if (isCorrect) ans else (if (i % 3 == 0) null else "wrong answer"),
                    correctAnswer = ans,
                    isCorrect = isCorrect,
                    explanation = "Dẫn chứng đối chiếu trực tiếp từ audio tại thời điểm $timeStr.",
                    prompt = qDetail?.prompt ?: "Câu hỏi số $i",
                    sectionNumber = secNum,
                    evidenceQuote = quote,
                    timestampSeconds = timeSec,
                    timestampStr = timeStr
                )
            )
        }

        return list
    }

    private fun formatDuration(millis: Long): String {
        val totalSec = millis / 1000
        val m = totalSec / 60
        val s = totalSec % 60
        return "%02d:%02d".format(m, s)
    }

    // AudioPlayerListener Callbacks
    override fun onProgress(currentMs: Long, durationMs: Long) {
        if (!isUserTrackingSeekBar) {
            val duration = if (durationMs > 0) durationMs else 1L
            val progress = ((currentMs * 1000L) / duration).toInt().coerceIn(0, 1000)
            audioSeekBar.progress = progress
            tvAudioTime.text = "${formatDuration(currentMs)} / ${formatDuration(durationMs)}"
        }

        // Check active paragraph in transcript
        val currentSec = (currentMs / 1000L).toInt()
        val activeIdx = transcriptParagraphs.indexOfLast { it.timestampSeconds <= currentSec }
        if (activeIdx != -1 && activeIdx != activeParagraphIndex) {
            highlightParagraph(activeIdx, smoothScroll = true)
        }
    }

    override fun onStateChanged(isPlaying: Boolean, isBuffering: Boolean) {
        this.isPlaying = isPlaying
        btnPlayPause.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow)
    }

    override fun onPlaybackCompleted() {
        isPlaying = false
        btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        audioSeekBar.progress = 0
    }

    override fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            audioPlayerManager.release()
        } else {
            audioPlayerManager.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerManager.release()
    }
}

