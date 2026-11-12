package com.example.ielts.ui.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object StudyReminderScheduler {

    private const val UNIQUE_WORK_DAILY_REMINDER = "unique_work_study_reminder"
    private const val UNIQUE_WORK_SNOOZE_REMINDER = "unique_work_study_snooze"

    /**
     * Lập lịch thông báo chạy ngầm bằng WorkManager theo đúng giờ người dùng đã cài đặt.
     * Chạy ngay cả khi app bị đóng hoặc thiết bị khởi động lại.
     */
    fun scheduleDailyReminder(context: Context) {
        val reminderManager = StudyReminderManager.getInstance(context)
        val workManager = WorkManager.getInstance(context)

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

        // Nếu mốc giờ hôm nay đã trôi qua, chuyển sang mốc giờ ngày mai
        if (target.before(now) || target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelayMillis = target.timeInMillis - now.timeInMillis

        val reminderWorkRequest = OneTimeWorkRequestBuilder<StudyReminderWorker>()
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .addTag("study_reminder")
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_WORK_DAILY_REMINDER,
            ExistingWorkPolicy.REPLACE,
            reminderWorkRequest
        )
    }

    /**
     * Lập lịch nhắc lại sau 15 phút (Khi bấm nút "Nhắc tôi sau 15 phút" trên Notification).
     */
    fun scheduleSnoozeReminder(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val snoozeWorkRequest = OneTimeWorkRequestBuilder<StudyReminderWorker>()
            .setInitialDelay(15, TimeUnit.MINUTES)
            .addTag("study_reminder_snooze")
            .build()

        workManager.enqueueUniqueWork(
            UNIQUE_WORK_SNOOZE_REMINDER,
            ExistingWorkPolicy.REPLACE,
            snoozeWorkRequest
        )
    }

    /**
     * Hủy bỏ mọi lịch nhắc nhở khi người dùng tắt switch.
     */
    fun cancelReminder(context: Context) {
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
