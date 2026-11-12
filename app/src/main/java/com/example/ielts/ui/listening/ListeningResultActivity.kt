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

        // Canonical Cambridge 18 Test 1 Listening answers & evidence
        val defaultAnswers = listOf(
            "7YZ", "24", "dentist", "parking", "Claxby",
            "late", "evening", "supermarket", "pollution", "storage",
            "C", "A", "A", "B", "E",
            "B", "G", "D", "A", "F",
            "C", "B", "B", "A", "C",
            "A", "B", "C", "A", "C",
            "fences", "family", "helicopters", "stress", "sides",
            "breathing", "feet", "employment", "weapons", "tourism"
        )

        val defaultQuotes = listOf(
            "It's DW3Q 7YZ.", "It's the 24th of April, isn't it?", "the main reason I came here was to go to the dentist.",
            "parking was so difficult to find and it costs so much.", "At the bus stop on Claxby Street.",
            "this morning it was late. Only about 10 minutes, but still.", "any time I've been in town in the evening... wait a long time",
            "Mainly just to go to the supermarket.", "because of all the pollution.", "it doesn't have any storage.",
            "Some of the seats are placed very close together.", "The age of volunteers is really less important than other factors.",
            "we ensure it is continuous throughout your journey with us.", "your level of commitment", "your availability during weekends",
            "For fundraising, original and new ideas are particularly welcome.", "volunteers need a good level of fitness.",
            "an understanding of food and diet is essential.", "having experience on stage helps captivate young audiences.",
            "having a good memory for emergency procedures is vital.",
            "the students sitting next to me were talking loudly.", "there is much more variety in the job market than we had realised.",
            "quite hard for us to follow at points.", "school careers advice was delivered far too late",
            "differ on why we would like a career in fashion.", "realistic about the practice",
            "paying too much attention to how she looked", "expecting to become well known right away",
            "reasons why people return items", "fashion designs customers want but can't find",
            "extensive damage to fences in the reserve.", "an entire family unit was selected",
            "employed helicopters to herd the animals", "minimise physiological stress",
            "positioned on their sides to prevent their immense weight from crushing lungs",
            "breathing was monitored constantly throughout transit", "circumference of their feet",
            "created valuable employment opportunities", "sharp decline in poachers and confiscated weapons",
            "eco-tourism flourished, providing a significant boost to GDP"
        )

        val timestamps = listOf(
            34, 73, 94, 118, 142, 162, 183, 209, 241, 260,
            10, 25, 42, 60, 60, 85, 105, 125, 145, 165,
            12, 24, 40, 55, 70, 88, 105, 105, 130, 130,
            18, 32, 48, 65, 80, 98, 112, 130, 146, 162
        )

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

