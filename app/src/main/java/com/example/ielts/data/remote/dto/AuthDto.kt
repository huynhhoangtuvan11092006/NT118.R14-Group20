package com.example.ielts.data.remote.dto

// DTO gửi lên khi Đăng nhập
data class LoginRequest(
    val email: String,
    val password: String
)

// DTO gửi lên khi Đăng ký
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
)

// DTO thông tin người dùng trả về từ Server
data class UserDto(
    val id: String,
    val email: String,
    val fullName: String,
    val avatarUrl: String? = null,
    val targetBand: Float? = null,
    val currentBand: Float? = null,
    val expectedExamDate: String? = null,
    val dailyStudyMinutes: Int? = null,
    val streakDays: Int? = null,
    val createdAt: String? = null
)

// DTO bọc phản hồi trả về từ User API: { "user": { ... } }
data class UserResponse(
    val user: UserDto
)

// DTO phản hồi khi Đăng ký hoặc Đăng nhập thành công
data class AuthResponse(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Int = 900,
    val isNewUser: Boolean? = null
)

// DTO gửi lên khi Xác thực bằng Google
data class GoogleAuthRequest(
    val idToken: String
)

// DTO gửi lên khi Đăng xuất
data class LogoutRequest(
    val refreshToken: String
)

// DTO gửi lên khi cập nhật Target Band
data class UpdateTargetBandRequest(
    val targetBand: Float
)

// DTO gửi lên khi cập nhật thông tin Hồ sơ
data class UpdateProfileRequest(
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val currentBand: Float? = null,
    val expectedExamDate: String? = null,
    val dailyStudyMinutes: Int? = null
)

// DTO gửi lên khi Quên mật khẩu
data class ForgotPasswordRequest(
    val email: String
)

// DTO gửi lên khi Đặt lại mật khẩu bằng OTP
data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)

// DTO phản hồi thông báo từ máy chủ
data class MessageResponse(
    val message: String,
    val devOtp: String? = null
)

// DTO đồng bộ tiến độ học tập (Streak, Reading, Listening, Writing, Vocabulary SRS) lên Cloud
data class SyncProgressRequest(
    val progressJson: String,
    val currentStreak: Int = 0
)

data class SyncProgressResponse(
    val progressJson: String? = null,
    val currentStreak: Int = 0,
    val updatedAt: String? = null
)


