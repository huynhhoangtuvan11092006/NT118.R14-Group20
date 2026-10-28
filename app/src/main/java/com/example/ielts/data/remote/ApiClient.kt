package com.example.ielts.data.remote

import com.example.ielts.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // Bộ chặn in toàn bộ dữ liệu Request và Response ra cửa sổ Logcat để soi lỗi
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cấu hình OkHttpClient với thời gian chờ (Timeout) tối đa 30 giây
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Khởi tạo Retrofit Singleton với Base URL và bộ chuyển đổi Gson
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    // Cung cấp IeltsApiService để toàn bộ ứng dụng sử dụng chung
    val apiService: IeltsApiService by lazy {
        retrofit.create(IeltsApiService::class.java)
    }
}
