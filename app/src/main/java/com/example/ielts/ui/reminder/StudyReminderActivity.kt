package com.example.ielts.ui.reminder

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.example.ielts.R
import java.util.Calendar

class StudyReminderActivity : AppCompatActivity() {

    private lateinit var reminderManager: StudyReminderManager

    private lateinit var btnBack: ImageButton
    private lateinit var btnHelp: ImageButton
    private lateinit var switchReminder: SwitchCompat
    private lateinit var layoutReminderConfig: LinearLayout

    private lateinit var btnOpenTimePicker: LinearLayout
    private lateinit var tvDisplayTime: TextView
    private lateinit var tvDisplayPeriod: TextView

    private lateinit var btnPresetMorning: AppCompatButton
    private lateinit var btnPresetNoon: AppCompatButton
    private lateinit var btnPresetEvening: AppCompatButton
    private lateinit var btnPresetLate: AppCompatButton

    private lateinit var tvRepeatDaysCount: TextView
    private lateinit var btnTestNotification: LinearLayout
    private lateinit var btnSaveReminder: AppCompatButton

    // Day toggles
    private lateinit var btnDayMon: FrameLayout
    private lateinit var tvDayMon: TextView
    private lateinit var btnDayTue: FrameLayout
    private lateinit var tvDayTue: TextView
    private lateinit var btnDayWed: FrameLayout
    private lateinit var tvDayWed: TextView
    private lateinit var btnDayThu: FrameLayout
    private lateinit var tvDayThu: TextView
    private lateinit var btnDayFri: FrameLayout
    private lateinit var tvDayFri: TextView
    private lateinit var btnDaySat: FrameLayout
    private lateinit var tvDaySat: TextView
    private lateinit var btnDaySun: FrameLayout
    private lateinit var tvDaySun: TextView

    private var currentHour: Int = 20
    private var currentMinute: Int = 0
    private val selectedDays = mutableSetOf<Int>()

    // Launcher xin quyền thông báo trên Android 13+
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Đã cho phép BandUp gửi thông báo học tập! 🔔", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Bạn đã từ chối quyền thông báo. Vui lòng bật lại trong Cài đặt máy để nhận nhắc nhở.",
                    Toast.LENGTH_LONG
                ).show()
                switchReminder.isChecked = false
                updateConfigVisibility(false)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_reminder)

        reminderManager = StudyReminderManager.getInstance(this)

        initViews()
        loadSavedConfig()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnHelp = findViewById(R.id.btnHelp)
        switchReminder = findViewById(R.id.switchReminder)
        layoutReminderConfig = findViewById(R.id.layoutReminderConfig)

        btnOpenTimePicker = findViewById(R.id.btnOpenTimePicker)
        tvDisplayTime = findViewById(R.id.tvDisplayTime)
        tvDisplayPeriod = findViewById(R.id.tvDisplayPeriod)

        btnPresetMorning = findViewById(R.id.btnPresetMorning)
        btnPresetNoon = findViewById(R.id.btnPresetNoon)
        btnPresetEvening = findViewById(R.id.btnPresetEvening)
        btnPresetLate = findViewById(R.id.btnPresetLate)

        tvRepeatDaysCount = findViewById(R.id.tvRepeatDaysCount)
        btnTestNotification = findViewById(R.id.btnTestNotification)
        btnSaveReminder = findViewById(R.id.btnSaveReminder)

        btnDayMon = findViewById(R.id.btnDayMon)
        tvDayMon = findViewById(R.id.tvDayMon)
        btnDayTue = findViewById(R.id.btnDayTue)
        tvDayTue = findViewById(R.id.tvDayTue)
        btnDayWed = findViewById(R.id.btnDayWed)
        tvDayWed = findViewById(R.id.tvDayWed)
        btnDayThu = findViewById(R.id.btnDayThu)
        tvDayThu = findViewById(R.id.tvDayThu)
        btnDayFri = findViewById(R.id.btnDayFri)
        tvDayFri = findViewById(R.id.tvDayFri)
        btnDaySat = findViewById(R.id.btnDaySat)
        tvDaySat = findViewById(R.id.tvDaySat)
        btnDaySun = findViewById(R.id.btnDaySun)
        tvDaySun = findViewById(R.id.tvDaySun)
    }

    private fun loadSavedConfig() {
        switchReminder.isChecked = reminderManager.isReminderEnabled
        updateConfigVisibility(switchReminder.isChecked)

        currentHour = reminderManager.reminderHour
        currentMinute = reminderManager.reminderMinute

        selectedDays.clear()
        selectedDays.addAll(reminderManager.selectedDays)

        updateTimeDisplay()
        updatePresetSelection()
        updateDayTogglesUI()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            saveConfigAndSchedule()
        }

        btnHelp.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Nhắc Nhở Học Tập BandUp 🎯")
                .setMessage(
                    "WorkManager sẽ lập lịch và chạy thông báo ngầm đều đặn ngay cả khi ứng dụng bị tắt.\n\n" +
                    "Nhận thông báo nhắc nhở giúp bạn duy trì thói quen học tiếng Anh mỗi ngày, bảo vệ ngọn lửa Streak và chinh phục mục tiêu Band 7.0+!"
                )
                .setPositiveButton("Đã hiểu", null)
                .show()
        }

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAndRequestNotificationPermission()
            }
            updateConfigVisibility(isChecked)
        }

        // Mở TimePicker khi nhấn vào ô thời gian
        btnOpenTimePicker.setOnClickListener {
            val picker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    currentHour = hourOfDay
                    currentMinute = minute
                    updateTimeDisplay()
                    updatePresetSelection()
                },
                currentHour,
                currentMinute,
                true
            )
            picker.show()
        }

        // Quick Presets
        btnPresetMorning.setOnClickListener {
            setTimePreset(8, 0)
        }

        btnPresetNoon.setOnClickListener {
            setTimePreset(12, 30)
        }

        btnPresetEvening.setOnClickListener {
            setTimePreset(20, 0)
        }

        btnPresetLate.setOnClickListener {
            setTimePreset(21, 30)
        }

        // 7 Days Toggles
        btnDayMon.setOnClickListener { toggleDay(Calendar.MONDAY) }
        btnDayTue.setOnClickListener { toggleDay(Calendar.TUESDAY) }
        btnDayWed.setOnClickListener { toggleDay(Calendar.WEDNESDAY) }
        btnDayThu.setOnClickListener { toggleDay(Calendar.THURSDAY) }
        btnDayFri.setOnClickListener { toggleDay(Calendar.FRIDAY) }
        btnDaySat.setOnClickListener { toggleDay(Calendar.SATURDAY) }
        btnDaySun.setOnClickListener { toggleDay(Calendar.SUNDAY) }

        // Test Notification
        btnTestNotification.setOnClickListener {
            checkAndRequestNotificationPermission()
            StudyReminderScheduler.triggerInstantTest(this)
            Toast.makeText(this, "🔔 Đã gửi thông báo thử! Bạn kéo thanh trạng thái xuống xem nhé.", Toast.LENGTH_SHORT).show()
        }

        // Save Settings
        btnSaveReminder.setOnClickListener {
            saveConfigAndSchedule()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(permission)
            }
        }
    }

    private fun updateConfigVisibility(isEnabled: Boolean) {
        layoutReminderConfig.alpha = if (isEnabled) 1.0f else 0.4f
        btnOpenTimePicker.isClickable = isEnabled
        btnPresetMorning.isEnabled = isEnabled
        btnPresetNoon.isEnabled = isEnabled
        btnPresetEvening.isEnabled = isEnabled
        btnPresetLate.isEnabled = isEnabled
        btnDayMon.isClickable = isEnabled
        btnDayTue.isClickable = isEnabled
        btnDayWed.isClickable = isEnabled
        btnDayThu.isClickable = isEnabled
        btnDayFri.isClickable = isEnabled
        btnDaySat.isClickable = isEnabled
        btnDaySun.isClickable = isEnabled
    }

    private fun setTimePreset(hour: Int, minute: Int) {
        currentHour = hour
        currentMinute = minute
        updateTimeDisplay()
        updatePresetSelection()
    }

    private fun updateTimeDisplay() {
        tvDisplayTime.text = String.format("%02d:%02d", currentHour, currentMinute)
        tvDisplayPeriod.text = when {
            currentHour in 5..10 -> "Buổi sáng ☀️"
            currentHour in 11..13 -> "Buổi trưa 🌤️"
            currentHour in 14..17 -> "Buổi chiều ⛅"
            currentHour in 18..21 -> "Buổi tối 🌙"
            else -> "Đêm muộn 🪐"
        }
    }

    private fun updatePresetSelection() {
        // Reset all presets
        resetPresetStyle(btnPresetMorning, "Sáng 08:00")
        resetPresetStyle(btnPresetNoon, "Trưa 12:30")
        resetPresetStyle(btnPresetEvening, "Tối 20:00")
        resetPresetStyle(btnPresetLate, "Đêm 21:30")

        if (currentHour == 8 && currentMinute == 0) {
            setSelectedPresetStyle(btnPresetMorning, "✓ Sáng 08:00")
        } else if (currentHour == 12 && currentMinute == 30) {
            setSelectedPresetStyle(btnPresetNoon, "✓ Trưa 12:30")
        } else if (currentHour == 20 && currentMinute == 0) {
            setSelectedPresetStyle(btnPresetEvening, "✓ Tối 20:00")
        } else if (currentHour == 21 && currentMinute == 30) {
            setSelectedPresetStyle(btnPresetLate, "✓ Đêm 21:30")
        }
    }

    private fun resetPresetStyle(button: AppCompatButton, text: String) {
        button.text = text
        button.setBackgroundResource(R.drawable.bg_pill_preset_unselected)
        button.setTextColor(Color.parseColor("#475569"))
    }

    private fun setSelectedPresetStyle(button: AppCompatButton, text: String) {
        button.text = text
        button.setBackgroundResource(R.drawable.bg_pill_preset_selected)
        button.setTextColor(Color.WHITE)
    }

    private fun toggleDay(dayOfWeek: Int) {
        if (selectedDays.contains(dayOfWeek)) {
            if (selectedDays.size > 1) {
                selectedDays.remove(dayOfWeek)
            } else {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 ngày nhắc nhở!", Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            selectedDays.add(dayOfWeek)
        }
        updateDayTogglesUI()
    }

    private fun updateDayTogglesUI() {
        tvRepeatDaysCount.text = "${selectedDays.size}/7 ngày"

        updateSingleDay(btnDayMon, tvDayMon, Calendar.MONDAY)
        updateSingleDay(btnDayTue, tvDayTue, Calendar.TUESDAY)
        updateSingleDay(btnDayWed, tvDayWed, Calendar.WEDNESDAY)
        updateSingleDay(btnDayThu, tvDayThu, Calendar.THURSDAY)
        updateSingleDay(btnDayFri, tvDayFri, Calendar.FRIDAY)
        updateSingleDay(btnDaySat, tvDaySat, Calendar.SATURDAY)
        updateSingleDay(btnDaySun, tvDaySun, Calendar.SUNDAY)
    }

    private fun updateSingleDay(frame: FrameLayout, textView: TextView, day: Int) {
        val isSelected = selectedDays.contains(day)
        if (isSelected) {
            frame.setBackgroundResource(R.drawable.bg_day_circle_selected)
            textView.setTextColor(Color.WHITE)
        } else {
            frame.setBackgroundResource(R.drawable.bg_day_circle_unselected)
            textView.setTextColor(Color.parseColor("#64748B"))
        }
    }

    private fun saveConfigAndSchedule() {
        reminderManager.isReminderEnabled = switchReminder.isChecked
        reminderManager.reminderHour = currentHour
        reminderManager.reminderMinute = currentMinute
        reminderManager.selectedDays = selectedDays

        if (switchReminder.isChecked) {
            StudyReminderScheduler.scheduleDailyReminder(this)
            Toast.makeText(
                this,
                "✓ Đã lưu! Nhắc nhở sẽ phát vào ${reminderManager.getFormattedTime()} hàng ngày ⏰",
                Toast.LENGTH_LONG
            ).show()
        } else {
            StudyReminderScheduler.cancelReminder(this)
            Toast.makeText(this, "Đã tắt nhắc nhở học tập.", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    override fun onBackPressed() {
        saveConfigAndSchedule()
    }
}

