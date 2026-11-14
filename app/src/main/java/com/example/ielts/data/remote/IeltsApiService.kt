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

    // --- CHẤM ĐIỂM WRITING & LỘ TRÌNH HỌC BẰNG AI ---
    @POST("api/v1/ai/evaluate/writing")
    suspend fun evaluateWriting(
        @Header("Authorization") token: String,
        @Body request: com.example.ielts.data.remote.dto.WritingEvaluationRequest
    ): Response<WritingEvaluationResponse>

    @POST("api/v1/ai/evaluate/roadmap")
    suspend fun generateRoadmap(
        @Header("Authorization") token: String,
        @Body request: com.example.ielts.data.remote.dto.AiRoadmapRequest
    ): Response<com.example.ielts.data.remote.dto.AiRoadmapResponse>

    // --- LUYỆN THI READING ---
    @GET("api/v1/tests?type=READING")
    suspend fun getReadingTests(
        @Header("Authorization") token: String? = null
    ): Response<List<com.example.ielts.data.remote.dto.ReadingTestListItemDto>>

    @GET("api/v1/tests/{id}")
    suspend fun getReadingTestDetail(
        @retrofit2.http.Path("id") testId: String
    ): Response<com.example.ielts.data.remote.dto.ReadingTestDetailDto>

    @POST("api/v1/submissions/reading")
    suspend fun submitReading(
        @Header("Authorization") token: String? = null,
        @Body request: com.example.ielts.data.remote.dto.SubmitReadingRequest
    ): Response<com.example.ielts.data.remote.dto.ReadingSubmissionResultDto>

    // --- LUYỆN THI LISTENING ---
    @GET("api/v1/tests/listening")
    suspend fun getListeningTests(
        @Header("Authorization") token: String? = null
    ): Response<List<com.example.ielts.data.remote.dto.ListeningTestListItemDto>>

    @GET("api/v1/tests/listening/{id}")
    suspend fun getListeningTestDetail(
        @retrofit2.http.Path("id") testId: String
    ): Response<com.example.ielts.data.remote.dto.ListeningTestDetailDto>

    @POST("api/v1/submissions/listening")
    suspend fun submitListening(
        @Header("Authorization") token: String? = null,
        @Body request: com.example.ielts.data.remote.dto.SubmitListeningRequest
    ): Response<com.example.ielts.data.remote.dto.SubmitListeningResponse>
}
