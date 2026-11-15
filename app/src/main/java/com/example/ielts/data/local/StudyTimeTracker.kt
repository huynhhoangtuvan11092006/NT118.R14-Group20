package com.example.ielts.data.local

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StudyTimeTracker {

    private const val PREFS_NAME = "study_time_daily_pref"
    private const val KEY_PREFIX_DAY = "daily_seconds_"
    private const val KEY_TOTAL_SECONDS = "total_all_time_seconds"

    private var activeSessionStartMillis: Long = 0L

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getTodayDateStr(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    /**
     * Ghi nhận thêm số giây học thực tế cho một ngày cụ thể (mặc định là hôm nay)
     */
    @Synchronized
    fun recordStudySeconds(context: Context, seconds: Long, dateStr: String? = null) {
        if (seconds <= 0) return
        val targetDate = dateStr ?: getTodayDateStr()
        val prefs = getPrefs(context)
        val dayKey = KEY_PREFIX_DAY + targetDate

        val currentDaySec = prefs.getLong(dayKey, 0L)
        val currentTotalSec = prefs.getLong(KEY_TOTAL_SECONDS, 0L)

        prefs.edit()
            .putLong(dayKey, currentDaySec + seconds)
            .putLong(KEY_TOTAL_SECONDS, currentTotalSec + seconds)
            .apply()

        // Đồng bộ streak nếu đã học từ 1 phút trở lên
        if (currentDaySec + seconds >= 60L) {
            StreakManager.recordActivity(context, "STUDY_SESSION")
        }
    }

    /**
     * Bắt đầu một phiên học trực tiếp trong ứng dụng
     */
    @Synchronized
    fun startActiveSession() {
        activeSessionStartMillis = System.currentTimeMillis()
    }

    /**
     * Cập nhật thời gian học tức thì theo thời gian thực (real-time ticker)
     * Thường gọi định kỳ mỗi 30s - 60s hoặc khi màn hình onPause
     * Trả về tổng số phút học của ngày hôm nay sau khi cập nhật
     */
    @Synchronized
    fun tickActiveSession(context: Context): Int {
        if (activeSessionStartMillis == 0L) {
            activeSessionStartMillis = System.currentTimeMillis()
            return getDailyStudyMinutes(context, getTodayDateStr())
        }

        val now = System.currentTimeMillis()
        val elapsedMillis = now - activeSessionStartMillis
        val elapsedSeconds = elapsedMillis / 1000

        // Chỉ ghi nhận khi trôi qua ít nhất 5 giây
        if (elapsedSeconds >= 5) {
            recordStudySeconds(context, elapsedSeconds)
            activeSessionStartMillis = now
        }

        return getDailyStudyMinutes(context, getTodayDateStr())
    }

    /**
     * Kết thúc phiên học khi rời ứng dụng / đóng bài
     */
    @Synchronized
    fun endActiveSession(context: Context) {
        if (activeSessionStartMillis > 0L) {
            val now = System.currentTimeMillis()
            val elapsedSeconds = (now - activeSessionStartMillis) / 1000
            if (elapsedSeconds >= 5) {
                recordStudySeconds(context, elapsedSeconds)
            }
            activeSessionStartMillis = 0L
        }
    }

    /**
     * Lấy tổng số giây học của một ngày
     */
    fun getDailyStudySeconds(context: Context, dateStr: String): Long {
        return getPrefs(context).getLong(KEY_PREFIX_DAY + dateStr, 0L)
    }

    /**
     * Lấy tổng số phút học của một ngày
     */
    fun getDailyStudyMinutes(context: Context, dateStr: String): Int {
        val sec = getDailyStudySeconds(context, dateStr)
        return (sec / 60).toInt()
    }

    /**
     * Lấy tổng số giây học tích lũy toàn thời gian
     */
    fun getTotalStudySeconds(context: Context): Long {
        return getPrefs(context).getLong(KEY_TOTAL_SECONDS, 0L)
    }
}
