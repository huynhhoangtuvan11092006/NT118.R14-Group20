package com.example.ielts.ui.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class ReminderActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SNOOZE_15_MIN = "com.example.ielts.action.SNOOZE_15_MIN"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_SNOOZE_15_MIN) {
            // Đóng notification hiện tại
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(NotificationHelper.NOTIFICATION_ID)

            // Lập lịch lại sau 15 phút với WorkManager
            StudyReminderScheduler.scheduleSnoozeReminder(context)

            Toast.makeText(context, "Đã hẹn nhắc lại sau 15 phút ⏰", Toast.LENGTH_SHORT).show()
        }
    }
}

