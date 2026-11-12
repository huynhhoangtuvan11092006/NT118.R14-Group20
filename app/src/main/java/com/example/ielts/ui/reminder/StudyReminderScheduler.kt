package com.example.ielts.ui.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object StudyReminderScheduler {

    private const val TAG = "StudyReminderScheduler"
    private const val UNIQUE_WORK_DAILY_REMINDER = "unique_work_study_reminder"
    private const val UNIQUE_WORK_SNOOZE_REMINDER = "unique_work_study_snooze"
    private const val ALARM_REQUEST_CODE = 202611
    private const val ALARM_SNOOZE_REQUEST_CODE = 202612

    /**
     * Lập lịch thông báo chạy ngầm kép:
     * 1. AlarmManager (setExactAndAllowWhileIdle): Đảm bảo thông báo nổ ra CHÍNH XÁC TỪNG GIÂY đúng giờ hẹn,
     *    đánh thức CPU ngay cả khi màn hình tắt hoặc app bị đóng.
     * 2. WorkManager: Cơ chế dự phòng bền vững của Android OS.
     */
    fun scheduleDailyReminder(context: Context) {
        val reminderManager = StudyReminderManager.getInstance(context)

        if (!reminderManager.isReminderEnabled) {
            cancelReminder(context)
            return
        }

        val hour = reminderManager.reminderHour
        val minute = reminderManager.reminderMinute

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Nếu mốc giờ được hẹn hôm nay đã trôi qua, lập lịch cho ngày kế tiếp
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val targetMillis = target.timeInMillis
        val initialDelayMillis = targetMillis - now.timeInMillis

        Log.d(TAG, "Đã lập lịch nhắc nhở lúc ${hour}:${minute} (còn ${initialDelayMillis / 1000}s nữa)")

        // 1. AlarmManager chính xác tuyệt đối
        scheduleExactAlarm(context, targetMillis, ALARM_REQUEST_CODE)

        // 2. WorkManager dự phòng
        val reminderWorkRequest = OneTimeWorkRequestBuilder<StudyReminderWorker>()
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .addTag("study_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_WORK_DAILY_REMINDER,
            ExistingWorkPolicy.REPLACE,
            reminderWorkRequest
        )
    }

    /**
     * Lập lịch nhắc lại sau 15 phút (Khi bấm nút "Nhắc tôi sau 15 phút" trên Notification).
     */
    fun scheduleSnoozeReminder(context: Context) {
        val snoozeTimeMillis = System.currentTimeMillis() + 15 * 60 * 1000L

        // 1. AlarmManager báo đúng 15 phút
        scheduleExactAlarm(context, snoozeTimeMillis, ALARM_SNOOZE_REQUEST_CODE, isSnooze = true)

        // 2. WorkManager dự phòng
        val snoozeWorkRequest = OneTimeWorkRequestBuilder<StudyReminderWorker>()
            .setInitialDelay(15, TimeUnit.MINUTES)
            .addTag("study_reminder_snooze")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_WORK_SNOOZE_REMINDER,
            ExistingWorkPolicy.REPLACE,
            snoozeWorkRequest
        )
    }

    private fun scheduleExactAlarm(
        context: Context,
        triggerAtMillis: Long,
        requestCode: Int,
        isSnooze: Boolean = false
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra("is_snooze", isSnooze)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Trường hợp quyền SCHEDULE_EXACT_ALARM bị hạn chế ở một số ROM tùy biến
            Log.w(TAG, "Không thể setExactAndAllowWhileIdle, dùng setAndAllowWhileIdle: ${e.message}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        }
    }

    /**
     * Hủy bỏ mọi lịch nhắc nhở khi người dùng tắt switch.
     */
    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, ReminderAlarmReceiver::class.java)
        val p1 = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val p2 = PendingIntent.getBroadcast(
            context,
            ALARM_SNOOZE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(p1)
        alarmManager?.cancel(p2)

        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(UNIQUE_WORK_DAILY_REMINDER)
        workManager.cancelUniqueWork(UNIQUE_WORK_SNOOZE_REMINDER)
    }

    /**
     * Gửi ngay 1 thông báo thử nghiệm để test trên máy.
     */
    fun triggerInstantTest(context: Context) {
        NotificationHelper.showStudyReminder(context, isTest = true)
    }
}
