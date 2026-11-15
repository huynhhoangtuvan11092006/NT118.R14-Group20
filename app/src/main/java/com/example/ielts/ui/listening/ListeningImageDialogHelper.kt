package com.example.ielts.ui.listening

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.example.ielts.R
import com.google.android.material.button.MaterialButton

object ListeningImageDialogHelper {
    fun show(context: Context, imageResId: Int, title: String? = null) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_listening_image_zoom, null)
        dialog.setContentView(view)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val tvTitle = view.findViewById<TextView>(R.id.tvZoomTitle)
        val btnClose = view.findViewById<ImageView>(R.id.btnCloseZoom)
        val btnCloseBottom = view.findViewById<MaterialButton>(R.id.btnCloseBottom)
        val ivZoom = view.findViewById<ImageView>(R.id.ivZoomImage)

        tvTitle.text = title ?: "Tài liệu đề thi"
        ivZoom.setImageResource(imageResId)

        btnClose.setOnClickListener { dialog.dismiss() }
        btnCloseBottom.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}

