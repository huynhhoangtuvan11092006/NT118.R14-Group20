package com.example.ielts.ui.streak

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.ielts.R

class StreakCelebrationDialog(
    context: Context,
    private val newStreak: Int,
    private val onDismissAction: (() -> Unit)? = null
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_streak_celebration)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)

        val tvTitle = findViewById<TextView>(R.id.tvCelebrationTitle)
        val tvMessage = findViewById<TextView>(R.id.tvCelebrationMessage)
        val ivFlame = findViewById<ImageView>(R.id.ivCelebrationFlame)
        val btnContinue = findViewById<TextView>(R.id.btnContinueStreakCelebration)
        val btnClose = findViewById<ImageView>(R.id.btnCloseCelebration)
        val btnShare = findViewById<TextView>(R.id.btnShareStreak)

        tvTitle.text = "CHUỖI $newStreak NGÀY!"
        tvMessage.text = "Bạn đã hoàn thành mục tiêu học tập hôm nay. Chuỗi ngày liên tục đã tăng lên $newStreak ngày!"

        // Nice bounce pop animation on flame mascot
        ivFlame.scaleX = 0.5f
        ivFlame.scaleY = 0.5f
        ivFlame.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(600)
            .setInterpolator(OvershootInterpolator(2.5f))
            .start()

        btnContinue.setOnClickListener {
            dismiss()
            onDismissAction?.invoke()
        }

        btnClose.setOnClickListener {
            dismiss()
            onDismissAction?.invoke()
        }

        btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🔥 Tôi vừa đạt chuỗi $newStreak ngày học IELTS liên tục trên ứng dụng BandUp IELTS! Cùng rèn luyện với mình nhé!"
                )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ thành tích Streak"))
        }
    }
}
