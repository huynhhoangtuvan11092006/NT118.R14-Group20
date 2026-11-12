package com.example.ielts.ui.reminder

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

/**
 * Quản lý cấu hình cài đặt nhắc nhở học tập (Lưu SharedPreferences).
 */
class StudyReminderManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "study_reminder_prefs"
        private const val KEY_ENABLED = "key_reminder_enabled"
        private const val KEY_HOUR = "key_reminder_hour"
        private const val KEY_MINUTE = "key_reminder_minute"
        private const val KEY_DAYS = "key_reminder_days"

        @Volatile
        private var INSTANCE: StudyReminderManager? = null

        fun getInstance(context: Context): StudyReminderManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StudyReminderManager(context).also { INSTANCE = it }
            }
        }

        // 7 ngày trong tuần theo chuẩn Calendar (1=CN, 2=T2, 3=T3, 4=T4, 5=T5, 6=T6, 7=T7)
        val ALL_DAYS = setOf(
            Calendar.MONDAY,    // 2
            Calendar.TUESDAY,   // 3
            Calendar.WEDNESDAY, // 4
            Calendar.THURSDAY,  // 5
            Calendar.FRIDAY,    // 6
            Calendar.SATURDAY,  // 7
            Calendar.SUNDAY     // 1
        )
    }

    var isReminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var reminderHour: Int
        get() = prefs.getInt(KEY_HOUR, 20)
        set(value) = prefs.edit().putInt(KEY_HOUR, value).apply()

    var reminderMinute: Int
        get() = prefs.getInt(KEY_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_MINUTE, value).apply()

    var selectedDays: Set<Int>
        get() {
            val stringSet = prefs.getStringSet(KEY_DAYS, null)
            return if (stringSet.isNullOrEmpty()) {
                ALL_DAYS
            } else {
                stringSet.mapNotNull { it.toIntOrNull() }.toSet()
            }
        }
        set(value) {
            val stringSet = value.map { it.toString() }.toSet()
            prefs.edit().putStringSet(KEY_DAYS, stringSet).apply()
        }

    fun getFormattedTime(): String {
        val h = reminderHour
        val m = reminderMinute
        return String.format("%02d:%02d", h, m)
    }

    fun getPeriodLabel(): String {
        val h = reminderHour
        return when {
            h in 5..10 -> "Buổi sáng ☀️"
            h in 11..13 -> "Buổi trưa 🌤️"
            h in 14..17 -> "Buổi chiều ⛅"
            h in 18..21 -> "Buổi tối 🌙"
            else -> "Đêm muộn 🪐"
        }
    }
}
