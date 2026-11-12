package com.example.ielts.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.ielts.data.remote.ApiClient
import com.example.ielts.data.remote.dto.UpdateProfileRequest
import com.example.ielts.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class StreakRecordResult(
    val previousStreak: Int,
    val newStreak: Int,
    val isIncreased: Boolean,
    val isFirstActivityToday: Boolean,
    val isFreezeUsed: Boolean,
    val remainingFreezes: Int,
    val longestStreak: Int
)

data class DayStreakStatus(
    val dayLabel: String,      // "T2", "T3", "T4", "T5", "T6", "T7", "CN"
    val dateStr: String,       // "2026-11-12"
    val isCompleted: Boolean,  // Đã học
    val isToday: Boolean,      // Là ngày hôm nay
    val isFrozen: Boolean      // Được bảo lưu bằng khiên
)

data class StreakData(
    val currentStreak: Int,
    val longestStreak: Int,
    val streakFreezeCount: Int,
    val isFreezeEnabled: Boolean,
    val isActiveToday: Boolean,
    val lastActiveDate: String?,
    val weeklyDays: List<DayStreakStatus>
)

object StreakManager {

    private const val PREF_NAME = "streak_pref"
    private const val KEY_CURRENT_STREAK = "current_streak"
    private const val KEY_LONGEST_STREAK = "longest_streak"
    private const val KEY_LAST_ACTIVE_DATE = "last_active_date"
    private const val KEY_FREEZE_COUNT = "streak_freeze_count"
    private const val KEY_FREEZE_ENABLED = "streak_freeze_enabled"
    private const val KEY_COMPLETED_DATES = "completed_dates"
    private const val KEY_FROZEN_DATES = "frozen_dates"

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Lấy toàn bộ thông tin streak hiện tại
     */
    fun getStreakData(context: Context): StreakData {
        val prefs = getPrefs(context)
        val todayStr = getTodayString()

        // Khởi tạo mặc định nếu là người dùng mới: 5 ngày streak ban đầu hoặc 1 ngày, tặng 1 khiên
        val currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 5)
        val longestStreak = prefs.getInt(KEY_LONGEST_STREAK, maxOf(currentStreak, 14))
        val freezeCount = prefs.getInt(KEY_FREEZE_COUNT, 1)
        val freezeEnabled = prefs.getBoolean(KEY_FREEZE_ENABLED, true)
        val lastActiveDate = prefs.getString(KEY_LAST_ACTIVE_DATE, null)

        val completedDates = prefs.getStringSet(KEY_COMPLETED_DATES, emptySet())?.toMutableSet() ?: mutableSetOf()
        val frozenDates = prefs.getStringSet(KEY_FROZEN_DATES, emptySet())?.toMutableSet() ?: mutableSetOf()

        val isActiveToday = completedDates.contains(todayStr)

        val weeklyDays = computeWeeklyDays(todayStr, completedDates, frozenDates)

        return StreakData(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            streakFreezeCount = freezeCount,
            isFreezeEnabled = freezeEnabled,
            isActiveToday = isActiveToday,
            lastActiveDate = lastActiveDate,
            weeklyDays = weeklyDays
        )
    }

    /**
     * Ghi nhận hoạt động học tập (Nộp bài Writing, làm Reading/Listening, học từ vựng)
     */
    fun recordActivity(context: Context, activityType: String = "PRACTICE"): StreakRecordResult {
        val prefs = getPrefs(context)
        val today = Calendar.getInstance()
        val todayStr = dateFormat.format(today.time)

        var currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 5)
        var longestStreak = prefs.getInt(KEY_LONGEST_STREAK, 14)
        var freezeCount = prefs.getInt(KEY_FREEZE_COUNT, 1)
        val freezeEnabled = prefs.getBoolean(KEY_FREEZE_ENABLED, true)
        val lastActiveDateStr = prefs.getString(KEY_LAST_ACTIVE_DATE, null)

        val completedDates = prefs.getStringSet(KEY_COMPLETED_DATES, emptySet())?.toMutableSet() ?: mutableSetOf()
        val frozenDates = prefs.getStringSet(KEY_FROZEN_DATES, emptySet())?.toMutableSet() ?: mutableSetOf()

        val previousStreak = currentStreak
        var isIncreased = false
        var isFirstActivityToday = false
        var isFreezeUsed = false

        if (completedDates.contains(todayStr)) {
            // Đã hoàn thành mục tiêu hôm nay rồi -> không tăng streak thêm
            isFirstActivityToday = false
        } else {
            // Lần đầu hoàn thành trong ngày
            isFirstActivityToday = true
            completedDates.add(todayStr)

            if (lastActiveDateStr == null) {
                // Người dùng bắt đầu chuỗi
                currentStreak = maxOf(1, currentStreak + 1)
                isIncreased = true
            } else {
                try {
                    val lastDate = dateFormat.parse(lastActiveDateStr)
                    val daysDiff = getDaysBetween(lastDate, today.time)

                    when {
                        daysDiff <= 1 -> {
                            // Học liên tiếp từ hôm qua (hoặc cùng ngày)
                            currentStreak += 1
                            isIncreased = true
                        }
                        daysDiff == 2L -> {
                            // Lỡ mất 1 ngày hôm qua
                            if (freezeEnabled && freezeCount > 0) {
                                // Tự động dùng 1 khiên bảo vệ
                                freezeCount -= 1
                                isFreezeUsed = true
                                val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                                val yesterdayStr = dateFormat.format(yesterdayCal.time)
                                frozenDates.add(yesterdayStr)

                                currentStreak += 1
                                isIncreased = true
                            } else {
                                // Mất chuỗi -> reset về 1
                                currentStreak = 1
                                isIncreased = false
                            }
                        }
                        else -> {
                            // Nghỉ từ 2 ngày trở lên -> đứt chuỗi
                            currentStreak = 1
                            isIncreased = false
                        }
                    }
                } catch (e: Exception) {
                    currentStreak += 1
                    isIncreased = true
                }
            }

            longestStreak = maxOf(longestStreak, currentStreak)

            // Lưu lại SharedPreferences
            prefs.edit()
                .putInt(KEY_CURRENT_STREAK, currentStreak)
                .putInt(KEY_LONGEST_STREAK, longestStreak)
                .putInt(KEY_FREEZE_COUNT, freezeCount)
                .putString(KEY_LAST_ACTIVE_DATE, todayStr)
                .putStringSet(KEY_COMPLETED_DATES, completedDates)
                .putStringSet(KEY_FROZEN_DATES, frozenDates)
                .apply()

            // Đồng bộ sang preference chung Constants.PREF_NAME cho MainActivity
            context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt("streak_days", currentStreak)
                .apply()

            // Đồng bộ lên Cloud Backend để khi sang máy khác đăng nhập không bị mất
            syncStreakToBackend(context, currentStreak)
        }

        return StreakRecordResult(
            previousStreak = previousStreak,
            newStreak = currentStreak,
            isIncreased = isIncreased,
            isFirstActivityToday = isFirstActivityToday,
            isFreezeUsed = isFreezeUsed,
            remainingFreezes = freezeCount,
            longestStreak = longestStreak
        )
    }

    /**
     * Bật/tắt trạng thái khiên bảo vệ
     */
    fun toggleFreeze(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_FREEZE_ENABLED, enabled).apply()
    }

    /**
     * Đồng bộ dữ liệu Streak từ Cloud về khi đăng nhập trên thiết bị mới
     */
    fun syncFromCloud(context: Context, streakCount: Int, markTodayActive: Boolean = true) {
        if (streakCount <= 0) return
        val prefs = getPrefs(context)
        val currentLocal = prefs.getInt(KEY_CURRENT_STREAK, 0)
        val finalStreak = maxOf(currentLocal, streakCount)
        val longest = maxOf(prefs.getInt(KEY_LONGEST_STREAK, finalStreak), finalStreak)

        val todayStr = getTodayString()
        val completedDates = prefs.getStringSet(KEY_COMPLETED_DATES, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (markTodayActive) {
            completedDates.add(todayStr)
        }

        prefs.edit()
            .putInt(KEY_CURRENT_STREAK, finalStreak)
            .putInt(KEY_LONGEST_STREAK, longest)
            .putString(KEY_LAST_ACTIVE_DATE, todayStr)
            .putStringSet(KEY_COMPLETED_DATES, completedDates)
            .apply()

        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt("streak_days", finalStreak)
            .apply()
    }

    private fun syncStreakToBackend(context: Context, streak: Int) {
        val prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val token = prefs.getString(Constants.KEY_AUTH_TOKEN, null) ?: return
        val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.apiService.updateProfile(
                    token = authHeader,
                    request = UpdateProfileRequest(
                        dailyStudyMinutes = streak
                    )
                )
            } catch (_: Exception) {}
        }
    }

    private fun getTodayString(): String {
        return dateFormat.format(Date())
    }

    private fun getDaysBetween(d1: Date?, d2: Date?): Long {
        if (d1 == null || d2 == null) return 0
        val diff = d2.time - d1.time
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    /**
     * Tính toán danh sách 7 ngày trong tuần hiện tại (Thứ 2 đến Chủ nhật)
     */
    private fun computeWeeklyDays(
        todayStr: String,
        completedDates: Set<String>,
        frozenDates: Set<String>
    ): List<DayStreakStatus> {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY

        // Đưa về Thứ 2 đầu tuần
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysFromMonday = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - Calendar.MONDAY
        cal.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

        val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        val result = mutableListOf<DayStreakStatus>()

        for (i in 0..6) {
            val dateStr = dateFormat.format(cal.time)
            val isToday = (dateStr == todayStr)
            val isCompleted = completedDates.contains(dateStr)
            val isFrozen = frozenDates.contains(dateStr)

            result.add(
                DayStreakStatus(
                    dayLabel = labels[i],
                    dateStr = dateStr,
                    isCompleted = isCompleted,
                    isToday = isToday,
                    isFrozen = isFrozen
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        return result
    }
}

