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

    // Cấu hình OkHttpClient với thời gian chờ (Timeout) tối đa 60 giây
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
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
