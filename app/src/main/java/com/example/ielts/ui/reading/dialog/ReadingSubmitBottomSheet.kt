package com.example.ielts.ui.reading.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.ielts.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReadingSubmitBottomSheet(
    private val completedCount: Int,
    private val flaggedCount: Int,
    private val totalCount: Int = 40,
    private val unansweredList: List<Int>,
    private val remainingSeconds: Int,
    private val onConfirmSubmit: () -> Unit,
    private val onContinueExam: () -> Unit = {}
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_submit_reading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressCircle = view.findViewById<ProgressBar>(R.id.progressCircle)
        val tvCompletedRatio = view.findViewById<TextView>(R.id.tvCompletedRatio)
        val pillCompleted = view.findViewById<TextView>(R.id.pillCompleted)
        val pillFlagged = view.findViewById<TextView>(R.id.pillFlagged)
        val pillUnanswered = view.findViewById<TextView>(R.id.pillUnanswered)
        val layoutWarning = view.findViewById<View>(R.id.layoutWarningUnanswered)
        val tvWarningTitle = view.findViewById<TextView>(R.id.tvWarningTitle)
        val tvRemainingTime = view.findViewById<TextView>(R.id.tvRemainingTime)
        val btnConfirmSubmit = view.findViewById<View>(R.id.btnConfirmSubmit)
        val btnContinueExam = view.findViewById<View>(R.id.btnContinueExam)

        // 1. Progress Ring
        progressCircle.max = totalCount
        progressCircle.progress = completedCount
        tvCompletedRatio.text = "$completedCount/$totalCount"

        // 2. Pills
        val unansweredCount = totalCount - completedCount
        pillCompleted.text = "✓ Đã làm: $completedCount"
        pillFlagged.text = "⚑ Cắm cờ: $flaggedCount"
        pillUnanswered.text = "! Chưa làm: $unansweredCount"

        // 3. Warning Card
        if (unansweredCount > 0) {
            layoutWarning.visibility = View.VISIBLE
            tvWarningTitle.text = "⚠ Bạn còn $unansweredCount câu chưa điền đáp án."
        } else {
            layoutWarning.visibility = View.GONE
        }

        // 4. Remaining Time
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        tvRemainingTime.text = "$minutes phút $seconds giây"

        // 5. Actions
        btnConfirmSubmit.setOnClickListener {
            dismiss()
            onConfirmSubmit()
        }

        btnContinueExam.setOnClickListener {
            dismiss()
            onContinueExam()
        }
    }
}
