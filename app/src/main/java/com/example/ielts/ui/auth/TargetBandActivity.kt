package com.example.ielts.ui.auth

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.ielts.MainActivity
import com.example.ielts.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class TargetBandActivity : AppCompatActivity() {

    private var selectedBand = 7.0
    private var selectedStudyTime = "30 phút/ngày"
    private var selectedDate: Calendar = Calendar.getInstance().apply {
        // Mặc định ngày thi dự kiến cách hiện tại 146 ngày
        add(Calendar.DAY_OF_YEAR, 146)
    }

    private lateinit var tvExamDateValue: TextView
    private lateinit var tvCountdownBadge: TextView
    private lateinit var tvForecastDescription: TextView

    // Horizontal ScrollView cho dải Band
    private lateinit var hsvBands: HorizontalScrollView
    private lateinit var llBandContainer: LinearLayout

    // 3 Cards thời gian học
    private lateinit var cardStudy15: LinearLayout
    private lateinit var cardStudy30: LinearLayout
    private lateinit var cardStudy60: LinearLayout

    private lateinit var ivRadio15: ImageView
    private lateinit var ivRadio30: ImageView
    private lateinit var ivRadio60: ImageView

    private lateinit var tvTitle15: TextView
    private lateinit var tvTitle30: TextView
    private lateinit var tvTitle60: TextView

    private val bandButtons by lazy {
        listOf(
            R.id.btnBand50 to 5.0,
            R.id.btnBand55 to 5.5,
            R.id.btnBand60 to 6.0,
            R.id.btnBand65 to 6.5,
            R.id.btnBand70 to 7.0,
            R.id.btnBand75 to 7.5,
            R.id.btnBand80 to 8.0,
            R.id.btnBand85 to 8.5
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target_band)

        tvExamDateValue = findViewById(R.id.tvExamDateValue)
        tvCountdownBadge = findViewById(R.id.tvCountdownBadge)
        tvForecastDescription = findViewById(R.id.tvForecastDescription)

        hsvBands = findViewById(R.id.hsvBands)
        llBandContainer = findViewById(R.id.llBandContainer)

        cardStudy15 = findViewById(R.id.cardStudy15)
        cardStudy30 = findViewById(R.id.cardStudy30)
        cardStudy60 = findViewById(R.id.cardStudy60)

        ivRadio15 = findViewById(R.id.ivRadio15)
        ivRadio30 = findViewById(R.id.ivRadio30)
        ivRadio60 = findViewById(R.id.ivRadio60)

        tvTitle15 = findViewById(R.id.tvTitle15)
        tvTitle30 = findViewById(R.id.tvTitle30)
        tvTitle60 = findViewById(R.id.tvTitle60)

        val btnBack = findViewById<View>(R.id.btnBack)
        val btnChangeExamDate = findViewById<View>(R.id.btnChangeExamDate)
        val btnContinue = findViewById<View>(R.id.btnContinue)

        btnBack.setOnClickListener {
            finish()
        }

        // Đăng ký sự kiện click cho các nút chọn Band
        bandButtons.forEach { (buttonId, bandValue) ->
            findViewById<AppCompatButton>(buttonId)?.setOnClickListener {
                selectBand(bandValue, smooth = true)
            }
        }

        // Tự động căn lề padding cho container dải band để nút đầu & cuối đều cuộn được vào chính giữa
        hsvBands.post {
            val halfScrollWidth = hsvBands.width / 2
            val unselectedHalf = dpToPx(56) / 2
            val sidePadding = (halfScrollWidth - unselectedHalf).coerceAtLeast(0)
            llBandContainer.setPadding(sidePadding, 0, sidePadding, 0)

            // Mặc định chọn và focus Band 7.0 vào chính giữa
            selectBand(7.0, smooth = false)
        }

        // Chọn ngày thi dự kiến
        btnChangeExamDate.setOnClickListener {
            showDatePicker()
        }
        updateExamDateUI()

        // Xử lý chọn 3 mức thời gian học
        cardStudy15.setOnClickListener {
            selectStudyTime("15 phút/ngày")
        }
        cardStudy30.setOnClickListener {
            selectStudyTime("30 phút/ngày")
        }
        cardStudy60.setOnClickListener {
            selectStudyTime("1 giờ/ngày")
        }

        // Mặc định chọn mức 30 phút/ngày
        selectStudyTime("30 phút/ngày")

        // Nút Tiếp tục vào Trang chủ
        btnContinue.setOnClickListener {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateText = formatter.format(selectedDate.time)

            // Lưu cài đặt vào SharedPreferences
            getSharedPreferences("ielts_user_prefs", MODE_PRIVATE).edit().apply {
                putFloat("target_band", selectedBand.toFloat())
                putString("exam_date", dateText)
                putString("study_time", selectedStudyTime)
                apply()
            }

            Toast.makeText(
                this,
                "Đã lưu mục tiêu Band %.1f!".format(selectedBand),
                Toast.LENGTH_SHORT
            ).show()

            // Chuyển sang ProfileActivity (Thiết lập hồ sơ cá nhân hóa)
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("TARGET_BAND", selectedBand)
                putExtra("EXAM_DATE", dateText)
                putExtra("STUDY_TIME", selectedStudyTime)
            }
            startActivity(intent)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun selectBand(band: Double, smooth: Boolean = true) {
        selectedBand = band

        val selectedWidth = dpToPx(78)
        val selectedHeight = dpToPx(52)
        val selectedMargin = dpToPx(5)

        val unselectedWidth = dpToPx(56)
        val unselectedHeight = dpToPx(44)
        val unselectedMargin = dpToPx(4)

        var selectedButtonView: AppCompatButton? = null

        bandButtons.forEach { (buttonId, value) ->
            findViewById<AppCompatButton>(buttonId)?.let { btn ->
                val isSelected = (value == band)
                val params = btn.layoutParams as LinearLayout.LayoutParams

                if (isSelected) {
                    selectedButtonView = btn
                    params.width = selectedWidth
                    params.height = selectedHeight
                    params.setMargins(selectedMargin, 0, selectedMargin, 0)
                    btn.layoutParams = params

                    btn.setBackgroundResource(R.drawable.bg_band_selected)
                    btn.setTextColor(ContextCompat.getColor(this, R.color.white))
                    btn.textSize = 15.5f
                    btn.elevation = dpToPx(4).toFloat()
                    btn.text = "✓ ${if (value == 8.5) "8.5+" else "%.1f".format(value)}"
                } else {
                    params.width = if (value == 8.5) dpToPx(62) else unselectedWidth
                    params.height = unselectedHeight
                    params.setMargins(unselectedMargin, 0, unselectedMargin, 0)
                    btn.layoutParams = params

                    btn.setBackgroundResource(R.drawable.bg_band_unselected)
                    btn.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                    btn.textSize = 14f
                    btn.elevation = 0f
                    btn.text = if (value == 8.5) "8.5+" else "%.1f".format(value)
                }
            }
        }

        // Cập nhật câu dự phóng lộ trình tương ứng
        tvForecastDescription.text = when (band) {
            5.0 -> "Band 5.0 củng cố ngữ pháp nền tảng & từ vựng thông dụng - Lộ trình tối ưu khoảng 2 tháng"
            5.5 -> "Band 5.5 xây dựng nền tảng từ vựng & phản xạ cơ bản - Lộ trình tối ưu khoảng 2.5 tháng"
            6.0 -> "Band 6.0 đáp ứng yêu cầu xét tuyển đại học & chuẩn đầu ra - Lộ trình tối ưu khoảng 3.5 tháng"
            6.5 -> "Band 6.5 mức điểm vàng cho đại đa số ngành học quốc tế - Lộ trình tối ưu khoảng 4 tháng"
            7.0 -> "Band 7.0 mở ra cơ hội du học & làm việc toàn cầu - Lộ trình tối ưu khoảng 4.5 tháng"
            7.5 -> "Band 7.5 thành thạo ngôn ngữ học thuật chuyên sâu - Lộ trình tối ưu khoảng 6 tháng"
            8.0 -> "Band 8.0 đẳng cấp chuyên gia và học bổng toàn phần - Lộ trình tối ưu khoảng 7.5 tháng"
            else -> "Band 8.5+ năng lực ngôn ngữ tương đương người bản xứ học thuật - Lộ trình chuyên sâu 8 tháng"
        }

        // TỰ ĐỘNG CUỘN NÚT ĐANG CHỌN VÀO CHÍNH GIỮA MÀN HÌNH
        selectedButtonView?.let { btn ->
            hsvBands.post {
                val scrollTargetX = (btn.left + btn.right) / 2 - (hsvBands.width / 2)
                if (smooth) {
                    hsvBands.smoothScrollTo(scrollTargetX, 0)
                } else {
                    hsvBands.scrollTo(scrollTargetX, 0)
                }
            }
        }
    }

    private fun selectStudyTime(time: String) {
        selectedStudyTime = time

        val primaryColor = ContextCompat.getColor(this, R.color.primary_blue)
        val textPrimaryColor = ContextCompat.getColor(this, R.color.text_primary)

        // Reset cả 3 cards
        cardStudy15.setBackgroundResource(R.drawable.bg_card_study_unselected)
        cardStudy30.setBackgroundResource(R.drawable.bg_card_study_unselected)
        cardStudy60.setBackgroundResource(R.drawable.bg_card_study_unselected)

        ivRadio15.setImageResource(R.drawable.ic_radio_custom_unchecked)
        ivRadio30.setImageResource(R.drawable.ic_radio_custom_unchecked)
        ivRadio60.setImageResource(R.drawable.ic_radio_custom_unchecked)

        tvTitle15.setTextColor(textPrimaryColor)
        tvTitle30.setTextColor(textPrimaryColor)
        tvTitle60.setTextColor(textPrimaryColor)

        when (time) {
            "15 phút/ngày" -> {
                cardStudy15.setBackgroundResource(R.drawable.bg_card_study_selected)
                ivRadio15.setImageResource(R.drawable.ic_radio_custom_checked)
                tvTitle15.setTextColor(primaryColor)
            }
            "30 phút/ngày" -> {
                cardStudy30.setBackgroundResource(R.drawable.bg_card_study_selected)
                ivRadio30.setImageResource(R.drawable.ic_radio_custom_checked)
                tvTitle30.setTextColor(primaryColor)
            }
            "1 giờ/ngày" -> {
                cardStudy60.setBackgroundResource(R.drawable.bg_card_study_selected)
                ivRadio60.setImageResource(R.drawable.ic_radio_custom_checked)
                tvTitle60.setTextColor(primaryColor)
            }
        }
    }

    private fun showDatePicker() {
        val today = Calendar.getInstance()

        val picker = DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate.set(year, month, day, 0, 0, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)
                updateExamDateUI()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        picker.datePicker.minDate = today.timeInMillis
        picker.show()
    }

    private fun updateExamDateUI() {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        tvExamDateValue.text = formatter.format(selectedDate.time)

        // Tính số ngày còn lại
        val today = Calendar.getInstance()
        val diffMs = selectedDate.timeInMillis - today.timeInMillis
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMs).coerceAtLeast(0)

        tvCountdownBadge.text = "🔥 Còn $diffDays ngày nữa"
    }
}
