package com.example.ielts.ui.streak

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.ielts.R
import com.example.ielts.data.local.DayStreakStatus
import com.example.ielts.data.local.StreakData
import com.example.ielts.data.local.StreakManager
import com.example.ielts.ui.practice.PracticeHubActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StreakDetailBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_streak_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        val data = StreakManager.getStreakData(context)

        // Close button
        view.findViewById<View>(R.id.btnCloseStreakSheet)?.setOnClickListener {
            dismiss()
        }
        view.findViewById<View>(R.id.btnDismissStreakSheet)?.setOnClickListener {
            dismiss()
        }

        // Streak Count
        view.findViewById<TextView>(R.id.tvStreakBigNumber)?.text = data.currentStreak.toString()

        // Encouragement Text
        val tvEncouragement = view.findViewById<TextView>(R.id.tvStreakEncouragement)
        if (data.isActiveToday) {
            tvEncouragement?.text = "Bạn đã hoàn thành mục tiêu hôm nay! Ngọn lửa đang bùng cháy rực rỡ 🔥"
        } else {
            tvEncouragement?.text = "Hôm nay bạn chưa học bài nào. Hãy luyện tập ngay để không bị đứt chuỗi nhé!"
        }

        // Daily goal badge
        val tvDailyGoal = view.findViewById<TextView>(R.id.tvDailyGoalMetBadge)
        if (data.isActiveToday) {
            tvDailyGoal?.text = "✓ Đạt chỉ tiêu ngày"
            tvDailyGoal?.setBackgroundResource(R.drawable.bg_pill_soft_emerald)
            tvDailyGoal?.setTextColor(ContextCompat.getColor(context, R.color.badge_emerald_text))
        } else {
            tvDailyGoal?.text = "Chưa hoàn thành"
            tvDailyGoal?.setBackgroundResource(R.drawable.bg_pill_soft_orange)
            tvDailyGoal?.setTextColor(ContextCompat.getColor(context, R.color.badge_orange_text))
        }

        // Bind 7 Days
        val containerWeekly = view.findViewById<LinearLayout>(R.id.layoutWeeklyDaysContainer)
        containerWeekly?.removeAllViews()

        val inflater = LayoutInflater.from(context)
        for (day in data.weeklyDays) {
            val itemView = inflater.inflate(R.layout.item_streak_day, containerWeekly, false)
            bindDayItem(itemView, day)
            containerWeekly?.addView(itemView)
        }

        // Freeze Shield
        val tvFreezeCount = view.findViewById<TextView>(R.id.tvFreezeCountBadge)
        tvFreezeCount?.text = "${data.streakFreezeCount}/2"

        val tvFreezeLabel = view.findViewById<TextView>(R.id.tvFreezeStatusLabel)
        val btnToggle = view.findViewById<View>(R.id.btnToggleFreezePill)
        if (data.isFreezeEnabled) {
            tvFreezeLabel?.text = "Đã bật"
        } else {
            tvFreezeLabel?.text = "Đang tắt"
        }

        btnToggle?.setOnClickListener {
            val nextState = !data.isFreezeEnabled
            StreakManager.toggleFreeze(context, nextState)
            tvFreezeLabel?.text = if (nextState) "Đã bật" else "Đang tắt"
        }

        // Action button "Luyện bài ngay để giữ chuỗi 🔥"
        view.findViewById<View>(R.id.btnPracticeNowToKeepStreak)?.setOnClickListener {
            dismiss()
            val intent = Intent(context, PracticeHubActivity::class.java)
            startActivity(intent)
        }
    }

    private fun bindDayItem(view: View, day: DayStreakStatus) {
        val tvLabel = view.findViewById<TextView>(R.id.tvDayLabel)
        val circle = view.findViewById<FrameLayout>(R.id.layoutDayCircle)
        val ivIcon = view.findViewById<ImageView>(R.id.ivDayIcon)
        val tvStatus = view.findViewById<TextView>(R.id.tvDayStatusText)

        tvLabel.text = day.dayLabel

        when {
            day.isCompleted -> {
                circle.setBackgroundResource(R.drawable.bg_pill_day_active)
                ivIcon.setImageResource(R.drawable.ic_fire)
                ivIcon.visibility = View.VISIBLE
                tvStatus.text = "Xong"
                tvStatus.setTextColor(ContextCompat.getColor(view.context, R.color.badge_orange_text))
            }
            day.isFrozen -> {
                circle.setBackgroundResource(R.drawable.bg_pill_soft_blue)
                ivIcon.setImageResource(R.drawable.ic_shield_freeze)
                ivIcon.visibility = View.VISIBLE
                tvStatus.text = "Bảo lưu"
                tvStatus.setTextColor(ContextCompat.getColor(view.context, R.color.primary_blue))
            }
            day.isToday -> {
                circle.setBackgroundResource(R.drawable.bg_pill_day_today_halo)
                ivIcon.setImageResource(R.drawable.ic_fire)
                ivIcon.visibility = View.VISIBLE
                tvStatus.text = "Nay"
                tvStatus.setTextColor(ContextCompat.getColor(view.context, R.color.badge_orange_text))
            }
            else -> {
                circle.setBackgroundResource(R.drawable.bg_pill_day_future)
                ivIcon.visibility = View.GONE
                tvStatus.text = "Chưa"
                tvStatus.setTextColor(ContextCompat.getColor(view.context, R.color.text_secondary))
            }
        }
    }

    companion object {
        const val TAG = "StreakDetailBottomSheet"

        fun newInstance(): StreakDetailBottomSheet {
            return StreakDetailBottomSheet()
        }
    }
}

