package com.example.ielts.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var edtProfileName: EditText
    private lateinit var edtProfileEmail: EditText
    private lateinit var edtProfilePhone: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        edtProfileName = findViewById(R.id.edtProfileName)
        edtProfileEmail = findViewById(R.id.edtProfileEmail)
        edtProfilePhone = findViewById(R.id.edtProfilePhone)

        // Nhận thông tin được truyền từ màn hình Target Band
        val band = intent.getDoubleExtra("TARGET_BAND", 7.0)
        val examDate = intent.getStringExtra("EXAM_DATE")
            ?: "Chưa chọn ngày thi"
        val studyTime = intent.getStringExtra("STUDY_TIME")
            ?: "30 phút/ngày"

        findViewById<TextView>(R.id.tvProfileTargetBand).text =
            "Band mục tiêu: %.1f Overall".format(band)

        findViewById<TextView>(R.id.tvProfileExamDate).text =
            "Ngày thi dự kiến: $examDate"

        findViewById<TextView>(R.id.tvProfileStudyTime).text =
            "Mục tiêu học mỗi ngày: $studyTime"

        findViewById<TextView>(R.id.tvCurrentBand).text =
            "Band hiện tại: 6.5"

        findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            saveProfile()
        }

        findViewById<TextView>(R.id.btnLanguage).setOnClickListener {
            Toast.makeText(
                this,
                "Chức năng đổi ngôn ngữ sẽ bổ sung sau.",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<TextView>(R.id.btnMicrophone).setOnClickListener {
            Toast.makeText(
                this,
                "Phần quyền micro sẽ được xử lý khi tích hợp luyện Speaking.",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<TextView>(R.id.btnChangePassword).setOnClickListener {
            startActivity(
                android.content.Intent(
                    this,
                    ForgotPasswordActivity::class.java
                )
            )
        }

        findViewById<TextView>(R.id.btnLogout).setOnClickListener {
            finishAffinity()
            startActivity(
                android.content.Intent(this, WelcomeActivity::class.java)
            )
        }

        findViewById<Switch>(R.id.swNotifications).setOnCheckedChangeListener {
                _, isChecked ->
            val message = if (isChecked) {
                "Đã bật tùy chọn nhắc học."
            } else {
                "Đã tắt tùy chọn nhắc học."
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        findViewById<Switch>(R.id.swDarkMode).setOnCheckedChangeListener {
                _, isChecked ->
            Toast.makeText(
                this,
                if (isChecked) {
                    "Đã bật lựa chọn chế độ ban đêm."
                } else {
                    "Đã tắt lựa chọn chế độ ban đêm."
                },
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveProfile() {
        val name = edtProfileName.text.toString().trim()
        val email = edtProfileEmail.text.toString().trim()
        val phone = edtProfilePhone.text.toString().trim()

        if (name.isEmpty()) {
            edtProfileName.error = "Vui lòng nhập họ và tên"
            edtProfileName.requestFocus()
            return
        }

        if (email.isEmpty() ||
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {
            edtProfileEmail.error = "Vui lòng nhập email hợp lệ"
            edtProfileEmail.requestFocus()
            return
        }

        if (phone.isNotEmpty() && phone.count { it.isDigit() } < 9) {
            edtProfilePhone.error = "Số điện thoại chưa hợp lệ"
            edtProfilePhone.requestFocus()
            return
        }

        // Hiện tại chỉ xác thực dữ liệu trên giao diện.
        // Chưa lưu vào API hoặc cơ sở dữ liệu.
        Toast.makeText(
            this,
            "Thông tin hợp lệ! Chức năng lưu hồ sơ qua API sẽ bổ sung sau.",
            Toast.LENGTH_LONG
        ).show()
    }
}
