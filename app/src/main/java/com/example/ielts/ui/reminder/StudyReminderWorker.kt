package com.example.ielts.ui.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ielts.data.local.StreakManager
import java.util.Calendar

class StudyReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val reminderManager = StudyReminderManager.getInstance(context)

        // Nếu người dùng đã tắt nhắc nhở, dừng
        if (!reminderManager.isReminderEnabled) {
            return Result.success()
        }

        // Kiểm tra xem hôm nay có nằm trong các ngày được chọn không
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (!reminderManager.selectedDays.contains(currentDayOfWeek)) {
            // Không phải ngày hẹn nhắc nhở, nhưng vẫn lên lịch cho ngày mai
            StudyReminderScheduler.scheduleDailyReminder(context)
            return Result.success()
        }

        // Kiểm tra xem hôm nay người dùng đã học bài chưa
        val streakData = StreakManager.getStreakData(context)
        val practicedToday = streakData.isActiveToday

        if (!practicedToday) {
            // Chưa học bài hôm nay -> Bắn thông báo nhắc nhở giữ streak!
            NotificationHelper.showStudyReminder(context)
        }

        // Lập lịch lại cho ngày hôm sau
        StudyReminderScheduler.scheduleDailyReminder(context)

        return Result.success()
    }
}
