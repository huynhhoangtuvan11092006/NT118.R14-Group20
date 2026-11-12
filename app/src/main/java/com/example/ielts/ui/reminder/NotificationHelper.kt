package com.example.ielts.ui.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ielts.R
import com.example.ielts.data.local.StreakManager
import com.example.ielts.ui.practice.PracticeHubActivity

object NotificationHelper {

    const val CHANNEL_ID = "study_reminder_channel"
    private const val CHANNEL_NAME = "Nhắc nhở học tập IELTS"
    private const val CHANNEL_DESC = "Thông báo nhắc nhở luyện tập hàng ngày để duy trì chuỗi Streak"
    const val NOTIFICATION_ID = 202611

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableLights(true)
                lightColor = Color.parseColor("#2563EB")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showStudyReminder(context: Context, isTest: Boolean = false) {
        createNotificationChannel(context)

        val streakData = StreakManager.getStreakData(context)
        val streak = streakData.currentStreak
        val isActiveToday = streakData.isActiveToday

        val title: String
        val content: String

        if (isTest) {
            title = if (streak > 0) "🔥 Đừng để chuỗi Streak $streak ngày vụt mất!" else "⏰ Đã đến giờ luyện IELTS hôm nay!"
            content = "🔔 Thông báo thử nghiệm: Hệ thống nhắc nhở của BandUp IELTS đang hoạt động hoàn hảo!"
        } else if (isActiveToday) {
            title = "🔥 Chuỗi $streak ngày đang bùng cháy!"
            content = "Hôm nay bạn đã luyện tập rất tốt! Dành thêm 5 phút ôn lại bài để duy trì phản xạ và tiến gần mục tiêu Band 7.0+ nhé."
        } else {
            title = if (streak > 0) "🔥 Đừng để chuỗi Streak $streak ngày vụt mất!" else "⏰ Đã đến giờ luyện IELTS hôm nay!"
            content = "Hôm nay bạn chưa hoàn thành bài luyện tập nào. Chỉ mất 5 phút ôn từ vựng hoặc luyện viết để giữ ngọn lửa của bạn!"
        }

        // Intent khi nhấn vào thông báo: Mở thẳng PracticeHubActivity
        val contentIntent = Intent(context, PracticeHubActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("from_notification", true)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            1001,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 1: "Luyện bài ngay"
        val practiceIntent = Intent(context, PracticeHubActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("from_notification_action", true)
        }
        val practicePendingIntent = PendingIntent.getActivity(
            context,
            1002,
            practiceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action 2: "Nhắc tôi sau 15 phút"
        val snoozeIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ReminderActionReceiver.ACTION_SNOOZE_15_MIN
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            1003,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_streak_flame_mascot)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(Color.parseColor("#2563EB"))
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_play_arrow, "Luyện bài ngay", practicePendingIntent)
            .addAction(R.drawable.ic_alarm, "Nhắc tôi sau 15 phút", snoozePendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
            // Trường hợp chưa được cấp quyền POST_NOTIFICATIONS trên Android 13+
            e.printStackTrace()
        }
    }
}
