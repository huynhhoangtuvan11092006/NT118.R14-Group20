
package com.example.ielts.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var btnSendReset: Button
    private lateinit var tvBackToLogin: TextView
    private lateinit var tvResendEmail: TextView
    private lateinit var btnBack: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        edtEmail = findViewById(R.id.edtEmail)
        btnSendReset = findViewById(R.id.btnSendReset)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)
        tvResendEmail = findViewById(R.id.tvResendEmail)
        btnBack = findViewById(R.id.btnBack)

        btnSendReset.setOnClickListener {
            validateEmail()
        }

        tvBackToLogin.setOnClickListener {
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }

        tvResendEmail.setOnClickListener {
            validateEmail()
        }
    }

    private fun validateEmail() {
        val email = edtEmail.text.toString().trim()

        if (email.isEmpty()) {
            edtEmail.error = "Vui lòng nhập email"
            edtEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = "Email không đúng định dạng"
            edtEmail.requestFocus()
            return
        }

        Toast.makeText(
            this,
            "Email hợp lệ! Chức năng gửi liên kết sẽ được kết nối API sau.",
            Toast.LENGTH_LONG
        ).show()
    }
}
