
package com.example.ielts.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtFullName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var btnBack: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Ánh xạ các thành phần giao diện
        edtFullName = findViewById(R.id.edtFullName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        cbTerms = findViewById(R.id.cbTerms)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        btnBack = findViewById(R.id.btnBack)

        // Xử lý nút Tạo tài khoản
        btnRegister.setOnClickListener {
            validateRegister()
        }

        // Quay về màn hình Login
        tvLogin.setOnClickListener {
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun validateRegister() {
        val fullName = edtFullName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString()
        val confirmPassword = edtConfirmPassword.text.toString()

        // Kiểm tra họ tên
        if (fullName.isEmpty()) {
            edtFullName.error = "Vui lòng nhập họ và tên"
            edtFullName.requestFocus()
            return
        }

        // Kiểm tra email
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

        // Kiểm tra mật khẩu
        if (password.isEmpty()) {
            edtPassword.error = "Vui lòng nhập mật khẩu"
            edtPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            edtPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
            edtPassword.requestFocus()
            return
        }

        // Kiểm tra xác nhận mật khẩu
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

        // Kiểm tra điều khoản
        if (!cbTerms.isChecked) {
            Toast.makeText(
                this,
                "Vui lòng đồng ý với điều khoản sử dụng",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Dữ liệu hợp lệ, chờ kết nối API đăng ký
        Toast.makeText(
            this,
            "Thông tin hợp lệ! Bước tiếp theo là kết nối API đăng ký.",
            Toast.LENGTH_LONG
        ).show()
    }
}
