package com.example.ielts.data.remote

import com.example.ielts.utils.Constants
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {

    // Bộ chặn in toàn bộ dữ liệu Request và Response ra cửa sổ Logcat để soi lỗi
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Tự động chuyển qua ADB USB tunnel (127.0.0.1:8787) nếu Wi-Fi trên điện thoại bị chặn Captive Portal
    private val usbFallbackInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        try {
            chain.withConnectTimeout(8, TimeUnit.SECONDS).proceed(originalRequest)
        } catch (e: IOException) {
            val fallbackBase = "http://127.0.0.1:8787".toHttpUrlOrNull()
            if (fallbackBase != null && originalRequest.url.host != "127.0.0.1") {
                val newUrl = originalRequest.url.newBuilder()
                    .scheme(fallbackBase.scheme)
                    .host(fallbackBase.host)
                    .port(fallbackBase.port)
                    .build()
                val fallbackRequest = originalRequest.newBuilder().url(newUrl).build()
                chain.withConnectTimeout(15, TimeUnit.SECONDS).proceed(fallbackRequest)
            } else {
                throw e
            }
        }
    }

    // Cấu hình OkHttpClient với thời gian chờ (Timeout) tối đa 60 giây
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(usbFallbackInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
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
