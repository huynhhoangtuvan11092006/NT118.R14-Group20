package com.example.ielts.ui.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var btnEmailClear: ImageView
    private lateinit var btnSendReset: Button
    private lateinit var tvResendCountdown: TextView
    private lateinit var tvBackToLogin: TextView
    private lateinit var btnBack: View

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Ánh xạ các thành phần giao diện
        edtEmail = findViewById(R.id.edtEmail)
        btnEmailClear = findViewById(R.id.btnEmailClear)
        btnSendReset = findViewById(R.id.btnSendReset)
        tvResendCountdown = findViewById(R.id.tvResendCountdown)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)
        btnBack = findViewById(R.id.btnBack)

        // Nút xóa nhanh nội dung email
        btnEmailClear.setOnClickListener {
            edtEmail.setText("")
        }

        // Tự động ẩn/hiện nút clear khi gõ
        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnEmailClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Nút Back
        btnBack.setOnClickListener {
            finish()
        }

        // Quay lại Đăng nhập
        tvBackToLogin.setOnClickListener {
            finish()
        }

        // Nút Gửi mã xác nhận
        btnSendReset.setOnClickListener {
            handleSendResetCode()
        }

        // Bấm vào dòng gửi lại mã khi hết giờ đếm ngược
        tvResendCountdown.setOnClickListener {
            if (!isTimerRunning) {
                handleSendResetCode()
            }
        }
    }

    private fun handleSendResetCode() {
        val email = edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            edtEmail.error = "Vui lòng nhập địa chỉ email"
            edtEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = "Email không đúng định dạng (vd: name@gmail.com)"
            edtEmail.requestFocus()
            return
        }

        // Bắt đầu đếm ngược 45 giây
        startResendTimer()

        // Hiển thị Dialog thông báo thành công chuẩn trải nghiệm
        AlertDialog.Builder(this)
            .setTitle("Đã gửi mã xác nhận ✉️")
            .setMessage("Mã OTP xác thực 6 chữ số đã được gửi đến email:\n$email\n\nVui lòng kiểm tra hộp thư đến (hoặc hòm thư Spam) để hoàn tất đặt lại mật khẩu.")
            .setPositiveButton("Đã hiểu") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Về Đăng nhập") { _, _ ->
                finish()
            }
            .show()
    }

    private fun startResendTimer() {
        countDownTimer?.cancel()
        isTimerRunning = true

        countDownTimer = object : CountDownTimer(45000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                tvResendCountdown.text = "Chưa nhận được mã? Gửi lại sau (${secondsLeft}s)"
                tvResendCountdown.setTextColor(resources.getColor(R.color.text_secondary, theme))
            }

            override fun onFinish() {
                isTimerRunning = false
                tvResendCountdown.text = "Chưa nhận được mã? Gửi lại ngay"
                tvResendCountdown.setTextColor(resources.getColor(R.color.primary_blue, theme))
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
