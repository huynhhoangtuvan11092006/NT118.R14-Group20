package com.example.ielts.ui.reading.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.ielts.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PassageLocatorBottomSheet : BottomSheetDialogFragment() {

    private var currentTextSizeSp: Float = 14f

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                bottomSheet.layoutParams.height = (resources.displayMetrics.heightPixels * 0.88).toInt()
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_passage_locator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvPassageBadge = view.findViewById<TextView>(R.id.tvLocatorPassageBadge)
        val tvPassageTitle = view.findViewById<TextView>(R.id.tvLocatorPassageTitle)
        val tvQuestionBadge = view.findViewById<TextView>(R.id.tvLocatorQuestionBadge)
        val tvPrompt = view.findViewById<TextView>(R.id.tvLocatorPrompt)
        val tvExplanation = view.findViewById<TextView>(R.id.tvLocatorExplanation)
        val tvPassageContent = view.findViewById<TextView>(R.id.tvLocatorPassageContent)
        val scrollPassage = view.findViewById<ScrollView>(R.id.scrollLocatorPassage)
        val btnFontDecrease = view.findViewById<View>(R.id.btnLocatorFontDecrease)
        val btnFontIncrease = view.findViewById<View>(R.id.btnLocatorFontIncrease)
        val btnClose = view.findViewById<View>(R.id.btnLocatorClose)

        val passageNumber = arguments?.getInt(ARG_PASSAGE_NUM) ?: 1
        val passageTitle = arguments?.getString(ARG_PASSAGE_TITLE) ?: "Reading Passage"
        val passageContent = arguments?.getString(ARG_PASSAGE_CONTENT) ?: ""
        val questionNum = arguments?.getInt(ARG_QUESTION_NUM) ?: 1
        val prompt = arguments?.getString(ARG_PROMPT) ?: ""
        val correctAnswer = arguments?.getString(ARG_CORRECT_ANSWER) ?: ""
        val explanation = arguments?.getString(ARG_EXPLANATION) ?: ""
        val evidenceQuote = arguments?.getString(ARG_EVIDENCE_QUOTE) ?: ""

        tvPassageBadge.text = "READING PASSAGE $passageNumber"
        tvPassageTitle.text = passageTitle
        tvQuestionBadge.text = "Câu $questionNum • Đáp án đúng: $correctAnswer"
        tvPrompt.text = prompt
        tvExplanation.text = "💡 $explanation"

        btnClose.setOnClickListener {
            dismiss()
        }

        btnFontIncrease.setOnClickListener {
            if (currentTextSizeSp < 22f) {
                currentTextSizeSp += 1.5f
                tvPassageContent.textSize = currentTextSizeSp
            }
        }

        btnFontDecrease.setOnClickListener {
            if (currentTextSizeSp > 12f) {
                currentTextSizeSp -= 1.5f
                tvPassageContent.textSize = currentTextSizeSp
            }
        }

        // Format passage content
        val formatted = passageContent
            .replace("\\n", "\n")
            .replace("[Paragraph A]", "\nA. ")
            .replace("[Paragraph B]", "\n\nB. ")
            .replace("[Paragraph C]", "\n\nC. ")
            .replace("[Paragraph D]", "\n\nD. ")
            .replace("[Paragraph E]", "\n\nE. ")
            .replace("[Paragraph F]", "\n\nF. ")
            .replace("[Paragraph G]", "\n\nG. ")

        val spannable = SpannableStringBuilder(formatted)
        var highlightOffset = -1

        // Locate quote
        if (evidenceQuote.isNotBlank()) {
            val idx = formatted.indexOf(evidenceQuote, ignoreCase = true)
            if (idx >= 0) {
                highlightOffset = idx
                val endIdx = idx + evidenceQuote.length
                spannable.setSpan(
                    BackgroundColorSpan(Color.parseColor("#FEF08A")), // Soft Amber Highlight
                    idx,
                    endIdx,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    idx,
                    endIdx,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#92400E")), // Deep Amber Brown text
                    idx,
                    endIdx,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        // If exact quote not found, fallback to searching first significant word
        if (highlightOffset == -1 && evidenceQuote.isNotBlank()) {
            val firstWord = evidenceQuote.split(" ").firstOrNull { it.length > 4 }
            if (firstWord != null) {
                val idx = formatted.indexOf(firstWord, ignoreCase = true)
                if (idx >= 0) {
                    highlightOffset = idx
                    spannable.setSpan(
                        BackgroundColorSpan(Color.parseColor("#FEF08A")),
                        idx,
                        minOf(idx + 60, formatted.length),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        tvPassageContent.text = spannable

        // Auto-scroll to highlighted location
        if (highlightOffset >= 0) {
            tvPassageContent.post {
                val layout = tvPassageContent.layout
                if (layout != null) {
                    val line = layout.getLineForOffset(highlightOffset)
                    val lineTop = layout.getLineTop(line)
                    scrollPassage.smoothScrollTo(0, maxOf(0, lineTop - 120))
                }
            }
        }
    }

    companion object {
        const val TAG = "PassageLocatorBottomSheet"
        private const val ARG_PASSAGE_NUM = "ARG_PASSAGE_NUM"
        private const val ARG_PASSAGE_TITLE = "ARG_PASSAGE_TITLE"
        private const val ARG_PASSAGE_CONTENT = "ARG_PASSAGE_CONTENT"
        private const val ARG_QUESTION_NUM = "ARG_QUESTION_NUM"
        private const val ARG_PROMPT = "ARG_PROMPT"
        private const val ARG_CORRECT_ANSWER = "ARG_CORRECT_ANSWER"
        private const val ARG_EXPLANATION = "ARG_EXPLANATION"
        private const val ARG_EVIDENCE_QUOTE = "ARG_EVIDENCE_QUOTE"

        fun newInstance(
            passageNumber: Int,
            passageTitle: String,
            passageContent: String,
            questionNum: Int,
            prompt: String,
            correctAnswer: String,
            explanation: String,
            evidenceQuote: String
        ): PassageLocatorBottomSheet {
            return PassageLocatorBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PASSAGE_NUM, passageNumber)
                    putString(ARG_PASSAGE_TITLE, passageTitle)
                    putString(ARG_PASSAGE_CONTENT, passageContent)
                    putInt(ARG_QUESTION_NUM, questionNum)
                    putString(ARG_PROMPT, prompt)
                    putString(ARG_CORRECT_ANSWER, correctAnswer)
                    putString(ARG_EXPLANATION, explanation)
                    putString(ARG_EVIDENCE_QUOTE, evidenceQuote)
                }
            }
        }
    }
}
