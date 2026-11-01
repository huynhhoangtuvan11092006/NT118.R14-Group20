package com.example.ielts.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ielts.R
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.RegisterRequest
import com.example.ielts.utils.Constants
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtFullName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnTogglePassword: TextView
    private lateinit var btnToggleConfirmPassword: TextView
    private lateinit var cbTerms: CheckBox
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var btnBack: android.view.View

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Ánh xạ các thành phần giao diện
        edtFullName = findViewById(R.id.edtFullName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword)
        cbTerms = findViewById(R.id.cbTerms)
        cbTerms.buttonTintList = null
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        btnBack = findViewById(R.id.btnBack)

        // Đồng bộ ẩn/hiện cả 2 ô mật khẩu cùng một lúc
        btnTogglePassword.setOnClickListener {
            toggleBothPasswords()
        }

        btnToggleConfirmPassword.setOnClickListener {
            toggleBothPasswords()
        }

        // Xử lý nút Tạo tài khoản
        btnRegister.setOnClickListener {
            validateAndRegister()
        }

        // Nút đăng ký Google
        findViewById<android.view.View?>(R.id.btnGoogle)?.setOnClickListener {
            Toast.makeText(this, "Đăng ký bằng Google chưa được tích hợp.", Toast.LENGTH_SHORT).show()
        }

        // Chuyển sang màn hình Đăng nhập (LoginActivity)
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun toggleBothPasswords() {
        isPasswordVisible = !isPasswordVisible

        val passCursor = edtPassword.selectionStart
        val confirmCursor = edtConfirmPassword.selectionStart

        val inputType = if (isPasswordVisible) {
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        edtPassword.inputType = inputType
        edtConfirmPassword.inputType = inputType

        edtPassword.setSelection(passCursor.coerceIn(0, edtPassword.text.length))
        edtConfirmPassword.setSelection(confirmCursor.coerceIn(0, edtConfirmPassword.text.length))

        val toggleLabel = if (isPasswordVisible) "Ẩn" else "Hiện"
        btnTogglePassword.text = toggleLabel
        btnToggleConfirmPassword.text = toggleLabel
    }



    private fun validateAndRegister() {
        val fullName = edtFullName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString()
        val confirmPassword = edtConfirmPassword.text.toString()

        // 1. Kiểm tra họ tên (tối thiểu 2 ký tự theo schema Backend)
        if (fullName.isEmpty()) {
            edtFullName.error = "Vui lòng nhập họ và tên"
            edtFullName.requestFocus()
            return
        }
        if (fullName.length < 2) {
            edtFullName.error = "Họ và tên phải có ít nhất 2 ký tự"
            edtFullName.requestFocus()
            return
        }

        // 2. Kiểm tra email
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

        // 3. Kiểm tra mật khẩu (Backend yêu cầu tối thiểu 8 ký tự)
        if (password.isEmpty()) {
            edtPassword.error = "Vui lòng nhập mật khẩu"
            edtPassword.requestFocus()
            return
        }
        if (password.length < 8) {
            edtPassword.error = "Mật khẩu phải có ít nhất 8 ký tự theo chuẩn bảo mật"
            edtPassword.requestFocus()
            return
        }

        // 4. Kiểm tra xác nhận mật khẩu
        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.error = "Vui lòng xác nhận mật khẩu"
            edtConfirmPassword.requestFocus()
            return
        }
        if (password != confirmPassword) {
            edtConfirmPassword.error = "Mật khẩu xác nhận không khớp"
            edtConfirmPassword.requestFocus()
            return
        }

        // 5. Kiểm tra điều khoản
        if (!cbTerms.isChecked) {
            Toast.makeText(
                this,
                "Vui lòng đồng ý với điều khoản sử dụng",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Bật trạng thái loading trên nút bấm
        btnRegister.isEnabled = false
        btnRegister.text = "Đang tạo tài khoản..."

        // Gọi API Backend qua Coroutines
        lifecycleScope.launch {
            try {
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    fullName = fullName
                )

                val response = ApiClient.apiService.register(request)

                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!

                    // Lưu Access Token & Refresh Token & Thông tin User vào SharedPreferences
                    val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                    prefs.edit().apply {
                        putString(Constants.KEY_AUTH_TOKEN, authData.accessToken)
                        putString(Constants.KEY_REFRESH_TOKEN, authData.refreshToken)
                        putString(Constants.KEY_USER_NAME, authData.user.fullName)
                        putString(Constants.KEY_USER_EMAIL, authData.user.email)
                        apply()
                    }

                    Toast.makeText(
                        this@RegisterActivity,
                        "Đăng ký thành công! Chào mừng ${authData.user.fullName} 🎉",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Chuyển sang màn hình Khảo sát Target Band
                    val intent = Intent(this@RegisterActivity, TargetBandActivity::class.java).apply {
                        putExtra("FULL_NAME", authData.user.fullName)
                        putExtra("EMAIL", authData.user.email)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    // Xử lý lỗi trả về từ Backend
                    val statusCode = response.code()
                    val errorRaw = response.errorBody()?.string() ?: ""
                    var userMessage = "Đăng ký không thành công. Mã lỗi: $statusCode"

                    try {
                        val json = JSONObject(errorRaw)
                        if (json.has("message")) {
                            userMessage = json.getString("message")
                        }
                    } catch (_: Exception) {}

                    if (statusCode == 409) {
                        edtEmail.error = "Email này đã được đăng ký trong hệ thống!"
                        edtEmail.requestFocus()
                        Toast.makeText(this@RegisterActivity, "Email đã tồn tại, vui lòng đăng nhập!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@RegisterActivity, userMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Không thể kết nối máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra Server"}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnRegister.isEnabled = true
                btnRegister.text = "Tạo tài khoản ngay  ➔"
            }
        }
    }
}
