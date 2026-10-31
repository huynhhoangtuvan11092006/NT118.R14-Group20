package com.example.ielts.ui.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.MainActivity
import com.example.ielts.R

class SplashActivity : AppCompatActivity() {

    private lateinit var pbSplash: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        pbSplash = findViewById(R.id.pbSplash)

        // Hiệu ứng mượt mà tăng dần thanh tiến trình từ 0 -> 100% trong 2.0 giây
        val animator = ObjectAnimator.ofInt(pbSplash, "progress", 0, 100)
        animator.duration = 2000
        animator.interpolator = DecelerateInterpolator()
        animator.start()

        // Chờ 2.2 giây để hoàn tất khởi động và điều hướng thông minh
        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStateAndNavigate()
        }, 2200)
    }

    private fun checkLoginStateAndNavigate() {
        val prefs = getSharedPreferences("ielts_login_preferences", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false)

        val targetIntent = if (isLoggedIn) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, WelcomeActivity::class.java)
        }

        startActivity(targetIntent)
        finish()
    }
}
