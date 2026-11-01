package com.example.ielts.utils

object Constants {
    // Địa chỉ Backend chạy trên máy tính (PC) kết nối qua ADB reverse tunnel
    const val BASE_URL = "http://127.0.0.1:3000/"

    // SharedPreferences keys
    const val PREF_NAME = "ielts_user_prefs"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_TARGET_BAND = "target_band"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
}
