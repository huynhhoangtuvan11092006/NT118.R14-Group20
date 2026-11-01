
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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
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
        cbRemember.buttonTintList = null
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
            finish()
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

        // Xử lý ghi nhớ email nếu được tick
        preferences.edit().apply {
            if (cbRemember.isChecked) {
                putString("saved_email", email)
            } else {
                remove("saved_email")
            }
            apply()
        }

        // Bật trạng thái loading trên nút bấm
        btnLogin.isEnabled = false
        btnLogin.text = "Đang đăng nhập..."

        // Gọi API Đăng nhập qua Coroutines
        lifecycleScope.launch {
            try {
                val request = com.example.ielts.data.remote.dto.LoginRequest(
                    email = email,
                    password = password
                )

                val response = com.example.ielts.data.remote.ApiClient.apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!

                    // Lưu Access Token & Refresh Token & Thông tin User vào SharedPreferences
                    val prefs = getSharedPreferences(com.example.ielts.utils.Constants.PREF_NAME, MODE_PRIVATE)
                    prefs.edit().apply {
                        putString(com.example.ielts.utils.Constants.KEY_AUTH_TOKEN, authData.accessToken)
                        putString(com.example.ielts.utils.Constants.KEY_REFRESH_TOKEN, authData.refreshToken)
                        putString(com.example.ielts.utils.Constants.KEY_USER_NAME, authData.user.fullName)
                        putString(com.example.ielts.utils.Constants.KEY_USER_EMAIL, authData.user.email)
                        putBoolean(com.example.ielts.utils.Constants.KEY_IS_LOGGED_IN, true)
                        authData.user.targetBand?.let {
                            putFloat(com.example.ielts.utils.Constants.KEY_TARGET_BAND, it.toFloat())
                        }
                        apply()
                    }

                    Toast.makeText(
                        this@LoginActivity,
                        "Đăng nhập thành công! Chào mừng ${authData.user.fullName} 👋",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Điều hướng sang Dashboard chính (MainActivity)
                    val intent = Intent(this@LoginActivity, com.example.ielts.MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val statusCode = response.code()
                    val errorRaw = response.errorBody()?.string() ?: ""
                    var userMessage = "Tài khoản hoặc mật khẩu không chính xác!"

                    try {
                        val json = org.json.JSONObject(errorRaw)
                        if (json.has("message")) {
                            userMessage = json.getString("message")
                        }
                    } catch (_: Exception) {}

                    if (statusCode == 401 || statusCode == 400 || statusCode == 404) {
                        edtPassword.error = userMessage
                        edtPassword.requestFocus()
                        Toast.makeText(this@LoginActivity, userMessage, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@LoginActivity, "Đăng nhập thất bại ($statusCode): $userMessage", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Không thể kết nối máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra Server"}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnLogin.isEnabled = true
                btnLogin.text = "Đăng nhập ngay  ➔"
            }
        }
    }

    private fun showNotIntegratedMessage(provider: String) {
        Toast.makeText(
            this,
            "Đăng nhập bằng $provider chưa được tích hợp.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
