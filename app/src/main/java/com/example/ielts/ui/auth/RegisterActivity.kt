package com.example.ielts.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ielts.MainActivity
import com.example.ielts.R
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.GoogleAuthRequest
import com.example.ielts.data.remote.dto.RegisterRequest
import com.example.ielts.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
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
    private var googleSignInClient: GoogleSignInClient? = null

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
                else -> "Đăng ký bằng Google thất bại (Mã lỗi: ${e.statusCode})"
            }
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi đăng ký Google: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

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
            handleGoogleSignInClick()
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
                "Bạn có muốn thử nghiệm đăng ký nhanh bằng tài khoản Google mô phỏng (Dev Mode) không?"
            )
            .setPositiveButton("Thử Dev Mode") { _, _ ->
                performGoogleAuth("mock_dev_user@gmail.com")
            }
            .setNegativeButton("Đóng", null)
            .show()
    }

    private fun performGoogleAuth(idToken: String) {
        btnRegister.isEnabled = false
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
                        apply()
                    }

                    Toast.makeText(
                        this@RegisterActivity,
                        "Đăng ký bằng Google thành công! Chào mừng ${authData.user.fullName} 🎉",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (authData.isNewUser == true || authData.user.targetBand == null) {
                        val intent = Intent(this@RegisterActivity, TargetBandActivity::class.java).apply {
                            putExtra("FULL_NAME", authData.user.fullName)
                            putExtra("EMAIL", authData.user.email)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    }
                    finish()
                } else {
                    val statusCode = response.code()
                    val errorRaw = response.errorBody()?.string() ?: ""
                    var userMessage = "Đăng ký bằng Google thất bại ($statusCode)"
                    try {
                        val json = JSONObject(errorRaw)
                        if (json.has("message")) {
                            userMessage = json.getString("message")
                        }
                    } catch (_: Exception) {}
                    Toast.makeText(this@RegisterActivity, userMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Không thể kết nối máy chủ: ${e.localizedMessage ?: "Vui lòng kiểm tra Server"}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnRegister.isEnabled = true
            }
        }
    }
}
