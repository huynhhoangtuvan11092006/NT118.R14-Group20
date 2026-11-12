package com.example.ielts.ui.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val reminderManager = StudyReminderManager.getInstance(context)
            if (reminderManager.isReminderEnabled) {
                StudyReminderScheduler.scheduleDailyReminder(context)
            }
        }
    }
}
