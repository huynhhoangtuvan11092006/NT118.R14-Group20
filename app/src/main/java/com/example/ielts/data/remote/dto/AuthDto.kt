package com.example.ielts.data.remote.dto

data class LoginRequest(
    val email: String,
    val passwordHash: String
)

data class RegisterRequest(
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val targetBand: Float
)

data class AuthResponse(
    val token: String,
    val userId: String,
    val email: String,
    val fullName: String,
    val targetBand: Float
)
