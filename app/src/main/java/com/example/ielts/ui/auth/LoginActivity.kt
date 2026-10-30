
package com.example.ielts.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.R

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnTogglePassword: TextView
    private lateinit var cbRemember: CheckBox
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvRegister: TextView

    private var isPasswordVisible = false

    private val preferences by lazy {
        getSharedPreferences("ielts_login_preferences", MODE_PRIVATE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Ánh xạ các thành phần từ XML
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        cbRemember = findViewById(R.id.cbRemember)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvRegister = findViewById(R.id.tvRegister)

// Cho phép nhập Email bằng bàn phím
        edtEmail.isFocusable = true
        edtEmail.isFocusableInTouchMode = true
        edtEmail.isClickable = true


        btnLogin = findViewById(R.id.btnLogin)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        cbRemember = findViewById(R.id.cbRemember)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvRegister = findViewById(R.id.tvRegister)

        // Khôi phục Email nếu người dùng đã chọn ghi nhớ
        val savedEmail = preferences.getString("saved_email", "")
        if (!savedEmail.isNullOrEmpty()) {
            edtEmail.setText(savedEmail)
            cbRemember.isChecked = true
        }

        // Hiện hoặc ẩn mật khẩu
        btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Xử lý nút Login
        btnLogin.setOnClickListener {
            validateLogin()
        }

        // Xử lý quên mật khẩu

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


        // Xử lý chuyển sang đăng ký
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Xử lý nút quay lại nếu có trong XML
        findViewById<View?>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        // Các nút đăng nhập bên thứ ba nếu có trong XML
        findViewById<View?>(R.id.btnGoogle)?.setOnClickListener {
            showNotIntegratedMessage("Google")
        }

        findViewById<View?>(R.id.btnApple)?.setOnClickListener {
            showNotIntegratedMessage("Apple")
        }
    }

    private fun togglePasswordVisibility() {
        val cursorPosition = edtPassword.selectionStart

        isPasswordVisible = !isPasswordVisible

        edtPassword.inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        edtPassword.setSelection(
            cursorPosition.coerceIn(0, edtPassword.text.length)
        )

        btnTogglePassword.text = if (isPasswordVisible) "Ẩn" else "Hiện"
    }

    private fun validateLogin() {
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString()

        // Kiểm tra Email trống
        if (email.isEmpty()) {
            edtEmail.error = "Vui lòng nhập Email"
            edtEmail.requestFocus()
            return
        }

        // Kiểm tra định dạng Email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = "Email không đúng định dạng"
            edtEmail.requestFocus()
            return
        }

        // Kiểm tra Password trống
        if (password.isEmpty()) {
            edtPassword.error = "Vui lòng nhập mật khẩu"
            edtPassword.requestFocus()
            return
        }

        // Ghi nhớ hoặc xóa Email đã lưu
        preferences.edit().apply {
            if (cbRemember.isChecked) {
                putString("saved_email", email)
            } else {
                remove("saved_email")
            }
            apply()
        }

        // Chưa kết nối API nên không giả lập đăng nhập thành công
        Toast.makeText(
            this,
            "Dữ liệu hợp lệ. Tiếp theo cần kết nối API đăng nhập.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showNotIntegratedMessage(provider: String) {
        Toast.makeText(
            this,
            "Đăng nhập bằng $provider chưa được tích hợp.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
