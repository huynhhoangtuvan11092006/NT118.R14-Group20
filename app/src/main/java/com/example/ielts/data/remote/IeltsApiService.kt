package com.example.ielts.data.remote

import com.example.ielts.data.remote.dto.AuthResponse
import com.example.ielts.data.remote.dto.LoginRequest
import com.example.ielts.data.remote.dto.RegisterRequest
import com.example.ielts.data.remote.dto.UpdateProfileRequest
import com.example.ielts.data.remote.dto.UpdateTargetBandRequest
import com.example.ielts.data.remote.dto.UserDto
import com.example.ielts.data.remote.dto.WritingEvaluationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface IeltsApiService {

    // --- XÁC THỰC & TÀI KHOẢN (AUTH) ---
    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Body request: com.example.ielts.data.remote.dto.LogoutRequest
    ): Response<Unit>

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: com.example.ielts.data.remote.dto.ForgotPasswordRequest
    ): Response<com.example.ielts.data.remote.dto.MessageResponse>

    @POST("api/v1/auth/reset-password")
    suspend fun resetPassword(
        @Body request: com.example.ielts.data.remote.dto.ResetPasswordRequest
    ): Response<com.example.ielts.data.remote.dto.MessageResponse>

    @POST("api/v1/auth/google")
    suspend fun googleAuth(
        @Body request: com.example.ielts.data.remote.dto.GoogleAuthRequest
    ): Response<AuthResponse>


    // --- QUẢN LÝ NGƯỜI DÙNG & HỒ SƠ (USER) ---
    @GET("api/v1/users/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<com.example.ielts.data.remote.dto.UserResponse>

    @PUT("api/v1/users/target-band")
    suspend fun updateTargetBand(
        @Header("Authorization") token: String,
        @Body request: UpdateTargetBandRequest
    ): Response<com.example.ielts.data.remote.dto.UserResponse>

    @PUT("api/v1/users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<com.example.ielts.data.remote.dto.UserResponse>

    // --- CHẤM ĐIỂM WRITING BẰNG AI GEMINI ---
    @POST("api/v1/submissions/writing")
    suspend fun evaluateWriting(
        @Header("Authorization") token: String,
        @Body essayBody: Map<String, String>
    ): Response<WritingEvaluationResponse>
}
