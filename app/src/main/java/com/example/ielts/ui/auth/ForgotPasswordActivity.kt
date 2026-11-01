package com.example.ielts.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ielts.R
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.ForgotPasswordRequest
import com.example.ielts.data.remote.dto.ResetPasswordRequest
import kotlinx.coroutines.launch
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    // Step 1 Views
    private lateinit var layoutStepEmail: LinearLayout
    private lateinit var edtEmail: EditText
    private lateinit var btnEmailClear: ImageView
    private lateinit var btnSendReset: Button

    // Step 2 Views
    private lateinit var layoutStepReset: LinearLayout
    private lateinit var tvOtpSentNotice: TextView
    private lateinit var edtOtp: EditText
    private lateinit var edtNewPassword: EditText
    private lateinit var btnToggleNewPassword: TextView
    private lateinit var edtConfirmNewPassword: EditText
    private lateinit var btnToggleConfirmPassword: TextView
    private lateinit var btnSubmitReset: Button
    private lateinit var btnChangeEmail: TextView

    // Shared Views
    private lateinit var tvResendCountdown: TextView
    private lateinit var tvBackToLogin: TextView
    private lateinit var btnBack: View

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var currentEmail = ""
    private var isNewPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        // Step 1
        layoutStepEmail = findViewById(R.id.layoutStepEmail)
        edtEmail = findViewById(R.id.edtEmail)
        btnEmailClear = findViewById(R.id.btnEmailClear)
        btnSendReset = findViewById(R.id.btnSendReset)

        // Step 2
        layoutStepReset = findViewById(R.id.layoutStepReset)
        tvOtpSentNotice = findViewById(R.id.tvOtpSentNotice)
        edtOtp = findViewById(R.id.edtOtp)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        btnToggleNewPassword = findViewById(R.id.btnToggleNewPassword)
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword)
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword)
        btnSubmitReset = findViewById(R.id.btnSubmitReset)
        btnChangeEmail = findViewById(R.id.btnChangeEmail)

        // Shared
        tvResendCountdown = findViewById(R.id.tvResendCountdown)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupListeners() {
        btnEmailClear.setOnClickListener {
            edtEmail.setText("")
        }

        edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnEmailClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnBack.setOnClickListener { finish() }
        tvBackToLogin.setOnClickListener { finish() }

        btnSendReset.setOnClickListener {
            handleSendOtp()
        }

        btnToggleNewPassword.setOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible
            togglePasswordVisibility(edtNewPassword, btnToggleNewPassword, isNewPasswordVisible)
        }

        btnToggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(edtConfirmNewPassword, btnToggleConfirmPassword, isConfirmPasswordVisible)
        }

        btnSubmitReset.setOnClickListener {
            handleSubmitReset()
        }

        btnChangeEmail.setOnClickListener {
            layoutStepReset.visibility = View.GONE
            layoutStepEmail.visibility = View.VISIBLE
            tvResendCountdown.visibility = View.GONE
            countDownTimer?.cancel()
            isTimerRunning = false
        }

        tvResendCountdown.setOnClickListener {
            if (!isTimerRunning && currentEmail.isNotEmpty()) {
                handleSendOtp(isResend = true)
            }
        }
    }

    private fun togglePasswordVisibility(editText: EditText, toggleButton: TextView, isVisible: Boolean) {
        val cursorPosition = editText.selectionStart
        editText.inputType = if (isVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        editText.setSelection(cursorPosition.coerceIn(0, editText.text.length))
        toggleButton.text = if (isVisible) "Ẩn" else "Hiện"
    }

    private fun handleSendOtp(isResend: Boolean = false) {
        val email = if (isResend) currentEmail else edtEmail.text.toString().trim()

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

        currentEmail = email

        btnSendReset.isEnabled = false
        btnSendReset.text = "Đang gửi mã..."

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.forgotPassword(ForgotPasswordRequest(email))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    layoutStepEmail.visibility = View.GONE
                    layoutStepReset.visibility = View.VISIBLE
                    tvResendCountdown.visibility = View.VISIBLE

                    tvOtpSentNotice.text = "✉️ Mã xác nhận 6 số đã được gửi đến:\n$email"
                    startResendTimer()

                    val message = if (!body.devOtp.isNullOrEmpty()) {
                        "${body.message}\n(Mã xác nhận thử nghiệm: ${body.devOtp})"
                    } else {
                        body.message
                    }

                    AlertDialog.Builder(this@ForgotPasswordActivity)
                        .setTitle("Gửi mã thành công! ✉️")
                        .setMessage(message)
                        .setPositiveButton("Tiếp tục") { dialog, _ ->
                            dialog.dismiss()
                            edtOtp.requestFocus()
                        }
                        .show()
                } else {
                    val rawError = response.errorBody()?.string() ?: ""
                    var userMessage = "Không thể gửi mã xác nhận"
                    try {
                        val json = JSONObject(rawError)
                        if (json.has("message")) userMessage = json.getString("message")
                    } catch (_: Exception) {}

                    edtEmail.error = userMessage
                    edtEmail.requestFocus()
                    Toast.makeText(this@ForgotPasswordActivity, userMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Lỗi kết nối máy chủ: ${e.localizedMessage ?: "Vui lòng thử lại"}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnSendReset.isEnabled = true
                btnSendReset.text = "Gửi mã xác nhận ➔"
            }
        }
    }

    private fun handleSubmitReset() {
        val otp = edtOtp.text.toString().trim()
        val newPassword = edtNewPassword.text.toString()
        val confirmPassword = edtConfirmNewPassword.text.toString()

        if (otp.length != 6) {
            edtOtp.error = "Vui lòng nhập đúng 6 chữ số OTP"
            edtOtp.requestFocus()
            return
        }

        if (newPassword.length < 8) {
            edtNewPassword.error = "Mật khẩu mới phải có tối thiểu 8 ký tự"
            edtNewPassword.requestFocus()
            return
        }

        if (newPassword != confirmPassword) {
            edtConfirmNewPassword.error = "Mật khẩu xác nhận không khớp"
            edtConfirmNewPassword.requestFocus()
            return
        }

        btnSubmitReset.isEnabled = false
        btnSubmitReset.text = "Đang đổi mật khẩu..."

        lifecycleScope.launch {
            try {
                val request = ResetPasswordRequest(
                    email = currentEmail,
                    otp = otp,
                    newPassword = newPassword
                )
                val response = ApiClient.apiService.resetPassword(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "🎉 ${body.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Quay lại màn hình Login kèm theo Email đã điền sẵn
                    val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java).apply {
                        putExtra("prefilled_email", currentEmail)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val rawError = response.errorBody()?.string() ?: ""
                    var userMessage = "Đặt lại mật khẩu thất bại"
                    try {
                        val json = JSONObject(rawError)
                        if (json.has("message")) userMessage = json.getString("message")
                    } catch (_: Exception) {}

                    edtOtp.error = userMessage
                    edtOtp.requestFocus()
                    Toast.makeText(this@ForgotPasswordActivity, userMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Lỗi kết nối máy chủ: ${e.localizedMessage ?: "Vui lòng thử lại"}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnSubmitReset.isEnabled = true
                btnSubmitReset.text = "Xác nhận đổi mật khẩu ➔"
            }
        }
    }

    private fun startResendTimer() {
        countDownTimer?.cancel()
        isTimerRunning = true

        countDownTimer = object : CountDownTimer(60000, 1000) {
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
