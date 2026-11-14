package com.example.ielts.data.remote

import android.app.Application
import android.content.Context
import com.example.ielts.utils.Constants
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {

    private val refreshLock = Any()

    private fun getAppContext(): Context? {
        return try {
            val clazz = Class.forName("android.app.ActivityThread")
            val method = clazz.getMethod("currentApplication")
            method.invoke(null) as? Application
        } catch (_: Exception) {
            null
        }
    }

    // Bộ chặn in toàn bộ dữ liệu Request và Response ra cửa sổ Logcat để soi lỗi
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Tự động làm mới Access Token (15 phút) bằng Refresh Token (30 ngày) khi gặp lỗi 401
    private val tokenRefreshInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath
        val response = chain.proceed(originalRequest)

        if (response.code == 401 &&
            !path.endsWith("/auth/login") &&
            !path.endsWith("/auth/register") &&
            !path.endsWith("/auth/refresh") &&
            !path.endsWith("/auth/google")
        ) {
            val ctx = getAppContext()
            if (ctx != null) {
                synchronized(refreshLock) {
                    val prefs = ctx.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
                    val refreshToken = prefs.getString(Constants.KEY_REFRESH_TOKEN, null)
                    if (!refreshToken.isNullOrBlank()) {
                        try {
                            val refreshUrl = "${Constants.BASE_URL}api/v1/auth/refresh"
                            val bodyJson = JSONObject().put("refreshToken", refreshToken).toString()
                            val refreshReq = Request.Builder()
                                .url(refreshUrl)
                                .post(bodyJson.toRequestBody("application/json; charset=utf-8".toMediaType()))
                                .build()

                            val refreshClient = OkHttpClient.Builder()
                                .connectTimeout(15, TimeUnit.SECONDS)
                                .readTimeout(15, TimeUnit.SECONDS)
                                .build()

                            refreshClient.newCall(refreshReq).execute().use { refreshResp ->
                                if (refreshResp.isSuccessful) {
                                    val respStr = refreshResp.body?.string() ?: ""
                                    val json = JSONObject(respStr)
                                    val newAccess = json.optString("accessToken", "")
                                    val newRefresh = json.optString("refreshToken", "")
                                    if (newAccess.isNotBlank()) {
                                        prefs.edit().apply {
                                            putString(Constants.KEY_AUTH_TOKEN, newAccess)
                                            if (newRefresh.isNotBlank()) {
                                                putString(Constants.KEY_REFRESH_TOKEN, newRefresh)
                                            }
                                            commit()
                                        }
                                        response.close()
                                        val retriedRequest = originalRequest.newBuilder()
                                            .header("Authorization", "Bearer $newAccess")
                                            .build()
                                        return@Interceptor chain.proceed(retriedRequest)
                                    }
                                }
                            }
                        } catch (_: Exception) {
                        }
                    }
                }
            }
        }
        response
    }

    // Tự động chuyển qua ADB USB tunnel (127.0.0.1:8787) nếu Wi-Fi trên điện thoại bị chặn Captive Portal
    private val usbFallbackInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        try {
            chain.withConnectTimeout(2, TimeUnit.SECONDS).proceed(originalRequest)
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
        .addInterceptor(tokenRefreshInterceptor)
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
