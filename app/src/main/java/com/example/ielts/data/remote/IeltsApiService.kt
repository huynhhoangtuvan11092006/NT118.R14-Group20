package com.example.ielts.data.remote

import com.example.ielts.data.remote.dto.AuthResponse
import com.example.ielts.data.remote.dto.LoginRequest
import com.example.ielts.data.remote.dto.RegisterRequest
import com.example.ielts.data.remote.dto.WritingEvaluationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IeltsApiService {

    // --- XÁC THỰC & TÀI KHOẢN (AUTH) ---
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    // --- CHẤM ĐIỂM WRITING BẰNG AI GEMINI ---
    @POST("api/v1/submissions/writing")
    suspend fun evaluateWriting(
        @Header("Authorization") token: String,
        @Body essayBody: Map<String, String>
    ): Response<WritingEvaluationResponse>
}
