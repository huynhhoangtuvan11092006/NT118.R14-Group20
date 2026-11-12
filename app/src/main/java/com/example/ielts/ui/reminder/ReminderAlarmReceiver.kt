package com.example.ielts.ui.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

/**
 * Receiver nhận tín hiệu báo thức chính xác (Exact Alarm) từ Android AlarmManager,
 * đảm bảo thông báo nổ ra ngay lập tức đúng từng phút từng giây kể cả khi tắt màn hình hay tắt app.
 */
class ReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderAlarmReceiver", "AlarmManager đã kích hoạt thông báo nhắc nhở học tập!")

        val reminderManager = StudyReminderManager.getInstance(context)
        if (!reminderManager.isReminderEnabled) {
            return
        }

        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (!reminderManager.selectedDays.contains(currentDayOfWeek)) {
            // Hôm nay không nằm trong danh sách ngày hẹn, lên lịch lại cho ngày kế tiếp
            StudyReminderScheduler.scheduleDailyReminder(context)
            return
        }

        // Bắn thông báo nhắc nhở ngay lập tức
        NotificationHelper.showStudyReminder(context)

        // Lập lịch lại cho ngày hôm sau
        StudyReminderScheduler.scheduleDailyReminder(context)
    }
}
