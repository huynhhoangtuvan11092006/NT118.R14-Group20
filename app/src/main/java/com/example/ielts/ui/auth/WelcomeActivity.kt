package com.example.ielts.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.R

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnStart = findViewById<android.view.View>(R.id.btnStart)
        val btnExistingAccount = findViewById<Button>(R.id.btnExistingAccount)

        // Bắt đầu ngay ➔ Sang Đăng ký tài khoản (RegisterActivity)
        btnStart.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Tôi đã có tài khoản ➔ Sang Đăng nhập (LoginActivity)
        btnExistingAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
