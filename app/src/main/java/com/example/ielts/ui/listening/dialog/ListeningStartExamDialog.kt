package com.example.ielts.ui.listening.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.ielts.R
import com.example.ielts.data.remote.dto.ListeningTestListItemDto

class ListeningStartExamDialog(
    context: Context,
    private val testItem: ListeningTestListItemDto,
    private val onConfirmStart: (ListeningTestListItemDto) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_start_listening_exam)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvStartExamTitle = findViewById<TextView>(R.id.tvStartExamTitle)
        val btnConfirmStartExam = findViewById<Button>(R.id.btnConfirmStartExam)
        val btnCancelStartExam = findViewById<Button>(R.id.btnCancelStartExam)

        tvStartExamTitle.text = "${testItem.title} (Listening)"

        btnConfirmStartExam.setOnClickListener {
            dismiss()
            onConfirmStart(testItem)
        }

        btnCancelStartExam.setOnClickListener {
            dismiss()
        }
    }
}
