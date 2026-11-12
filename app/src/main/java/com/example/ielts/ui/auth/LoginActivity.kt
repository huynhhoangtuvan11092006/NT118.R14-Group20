
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.ielts.MainActivity
import com.example.ielts.R
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.GoogleAuthRequest
import com.example.ielts.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnTogglePassword: TextView
    private lateinit var cbRemember: CheckBox
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvRegister: TextView

    private var isPasswordVisible = false
    private var googleSignInClient: GoogleSignInClient? = null

    private val preferences by lazy {
        getSharedPreferences("ielts_login_preferences", MODE_PRIVATE)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (!idToken.isNullOrEmpty()) {
                performGoogleAuth(idToken)
            } else {
                Toast.makeText(
                    this,
                    "Không nhận được ID Token từ Google. Hãy kiểm tra cấu hình GOOGLE_WEB_CLIENT_ID",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: ApiException) {
            if (e.statusCode == 12501) {
                return@registerForActivityResult
            }
            val errorMsg = when (e.statusCode) {
                10 -> "Lỗi cấu hình Google (Code 10: DEVELOPER_ERROR). Hãy kiểm tra lại Web Client ID và mã SHA-1 trong Google Cloud Console."
                else -> "Đăng nhập Google thất bại (Mã lỗi: ${e.statusCode})"
            }
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi đăng nhập Google: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
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

        // Khôi phục Email nếu người dùng vừa đặt lại mật khẩu hoặc đã chọn ghi nhớ
        val prefilledEmail = intent.getStringExtra("prefilled_email")
        if (!prefilledEmail.isNullOrEmpty()) {
            edtEmail.setText(prefilledEmail)
            edtPassword.requestFocus()
        } else {
            val savedEmail = preferences.getString("saved_email", "")
            if (!savedEmail.isNullOrEmpty()) {
                edtEmail.setText(savedEmail)
                cbRemember.isChecked = true
            }
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

        // Nút đăng nhập bằng Google
        findViewById<View?>(R.id.btnGoogle)?.setOnClickListener {
            handleGoogleSignInClick()
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
                        authData.user.expectedExamDate?.let { isoDate ->
                            try {
                                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                                val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                val d = inputFormat.parse(isoDate.substring(0, 10))
                                if (d != null) {
                                    putString("exam_date", outputFormat.format(d))
                                }
                            } catch (_: Exception) {}
                        }
                        val cloudStreak = authData.user.dailyStudyMinutes ?: authData.user.streakDays ?: 6
                        putInt("streak_days", cloudStreak)
                        apply()
                    }

                    // Đồng bộ Streak vào StreakManager trên thiết bị mới
                    val restoredStreak = authData.user.dailyStudyMinutes ?: authData.user.streakDays ?: 6
                    com.example.ielts.data.local.StreakManager.syncFromCloud(this@LoginActivity, restoredStreak)

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

    private fun handleGoogleSignInClick() {
        val webClientId = Constants.GOOGLE_WEB_CLIENT_ID
        if (webClientId.isBlank() || webClientId == "YOUR_GOOGLE_WEB_CLIENT_ID") {
            showGoogleConfigDialog()
            return
        }

        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()

            val client = GoogleSignIn.getClient(this, gso)
            googleSignInClient = client
            client.signOut().addOnCompleteListener {
                googleSignInLauncher.launch(client.signInIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể khởi động Google Sign-In: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showGoogleConfigDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cấu hình Google Sign-In")
            .setMessage(
                "Chưa cấu hình GOOGLE_WEB_CLIENT_ID trong Constants.kt.\n\n" +
                "Khi bạn dán Client ID thật, ứng dụng sẽ mở hộp thoại chọn tài khoản Google.\n\n" +
                "Bạn có muốn thử nghiệm đăng nhập nhanh bằng tài khoản Google mô phỏng (Dev Mode) không?"
            )
            .setPositiveButton("Thử Dev Mode") { _, _ ->
                performGoogleAuth("mock_dev_user@gmail.com")
            }
            .setNegativeButton("Đóng", null)
            .show()
    }

    private fun performGoogleAuth(idToken: String) {
        btnLogin.isEnabled = false
        Toast.makeText(this, "Đang xác thực với Google...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            try {
                val request = GoogleAuthRequest(idToken = idToken)
                val response = ApiClient.apiService.googleAuth(request)

                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!

                    val prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                    prefs.edit().apply {
                        putString(Constants.KEY_AUTH_TOKEN, authData.accessToken)
                        putString(Constants.KEY_REFRESH_TOKEN, authData.refreshToken)
                        putString(Constants.KEY_USER_NAME, authData.user.fullName)
                        putString(Constants.KEY_USER_EMAIL, authData.user.email)
                        putBoolean(Constants.KEY_IS_LOGGED_IN, true)
                        authData.user.targetBand?.let {
                            putFloat(Constants.KEY_TARGET_BAND, it.toFloat())
                        }
                        authData.user.expectedExamDate?.let { isoDate ->
                            try {
                                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                                val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                val d = inputFormat.parse(isoDate.substring(0, 10))
                                if (d != null) {
                                    putString("exam_date", outputFormat.format(d))
                                }
                            } catch (_: Exception) {}
                        }
                        val cloudStreak = authData.user.dailyStudyMinutes ?: authData.user.streakDays ?: 6
                        putInt("streak_days", cloudStreak)
                        apply()
                    }

                    // Đồng bộ Streak vào StreakManager trên thiết bị mới
                    val restoredStreak = authData.user.dailyStudyMinutes ?: authData.user.streakDays ?: 6
                    com.example.ielts.data.local.StreakManager.syncFromCloud(this@LoginActivity, restoredStreak)

                    Toast.makeText(
                        this@LoginActivity,
                        "Đăng nhập Google thành công! Chào mừng ${authData.user.fullName} 👋",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (authData.isNewUser == true || authData.user.targetBand == null) {
                        val intent = Intent(this@LoginActivity, TargetBandActivity::class.java).apply {
                            putExtra("FULL_NAME", authData.user.fullName)
                            putExtra("EMAIL", authData.user.email)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    }
                    finish()
                } else {
                    val statusCode = response.code()
                    val errorRaw = response.errorBody()?.string() ?: ""
                    var userMessage = "Đăng nhập Google thất bại ($statusCode)"
                    try {
                        val json = JSONObject(errorRaw)
                        if (json.has("message")) {
                            userMessage = json.getString("message")
                        }
                    } catch (_: Exception) {}
                    Toast.makeText(this@LoginActivity, userMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Không thể kết nối máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra Server"}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnLogin.isEnabled = true
            }
        }
    }
}
