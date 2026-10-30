package com.example.ielts.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ielts.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TargetBandActivity : AppCompatActivity() {

    private var selectedBand = 7.0
    private var selectedDate: Calendar? = null

    private lateinit var tvSelectedBand: TextView
    private lateinit var tvExamDate: TextView
    private lateinit var rgStudyTime: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target_band)

        tvSelectedBand = findViewById(R.id.tvSelectedBand)
        tvExamDate = findViewById(R.id.tvExamDate)
        rgStudyTime = findViewById(R.id.rgStudyTime)

        val btnBack = findViewById<TextView>(R.id.btnBack)
        val btnPrevious = findViewById<TextView>(R.id.btnPrevious)
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val btnExamDate = findViewById<Button>(R.id.btnExamDate)

        btnBack.setOnClickListener {
            finish()
        }

        btnPrevious.setOnClickListener {
            finish()
        }

        // Chọn band mục tiêu
        findViewById<Button>(R.id.btnBand60).setOnClickListener {
            selectBand(6.0)
        }
        findViewById<Button>(R.id.btnBand65).setOnClickListener {
            selectBand(6.5)
        }
        findViewById<Button>(R.id.btnBand70).setOnClickListener {
            selectBand(7.0)
        }
        findViewById<Button>(R.id.btnBand75).setOnClickListener {
            selectBand(7.5)
        }
        findViewById<Button>(R.id.btnBand80).setOnClickListener {
            selectBand(8.0)
        }

        // Chọn ngày thi dự kiến
        btnExamDate.setOnClickListener {
            showDatePicker()
        }

        // Hoàn tất lựa chọn mục tiêu
        btnContinue.setOnClickListener {
            val studyTime = when (rgStudyTime.checkedRadioButtonId) {
                R.id.rb15Minutes -> "15 phút/ngày"
                R.id.rb30Minutes -> "30 phút/ngày"
                R.id.rb1Hour -> "1 giờ/ngày"
                R.id.rb2Hours -> "2 giờ/ngày"
                else -> "30 phút/ngày"
            }

            val dateText = selectedDate?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(it.time)
            } ?: "Chưa chọn ngày thi"

            // Tạm thời truyền dữ liệu sang Hồ sơ để kiểm tra luồng.
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("TARGET_BAND", selectedBand)
            intent.putExtra("EXAM_DATE", dateText)
            intent.putExtra("STUDY_TIME", studyTime)
            startActivity(intent)
        }
    }

    private fun selectBand(band: Double) {
        selectedBand = band

        tvSelectedBand.text = "✦  Band mục tiêu: %.1f".format(
            Locale.US, selectedBand
        )

        val bandButtons = listOf(
            R.id.btnBand60 to 6.0,
            R.id.btnBand65 to 6.5,
            R.id.btnBand70 to 7.0,
            R.id.btnBand75 to 7.5,
            R.id.btnBand80 to 8.0
        )

        bandButtons.forEach { (id, value) ->
            val button = findViewById<Button>(id)
            val isSelected = value == band

            button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor(
                    if (isSelected) "#154378" else "#E4F2FA"
                )
            )
            button.setTextColor(
                android.graphics.Color.parseColor(
                    if (isSelected) "#FFFFFF" else "#154378"
                )
            )
        }
    }

    private fun showDatePicker() {
        val today = Calendar.getInstance()

        val picker = DatePickerDialog(
            this,
            { _, year, month, day ->
                val chosenDate = Calendar.getInstance()
                chosenDate.set(year, month, day, 0, 0, 0)
                chosenDate.set(Calendar.MILLISECOND, 0)

                selectedDate = chosenDate

                val formatter = SimpleDateFormat(
                    "dd/MM/yyyy", Locale.getDefault()
                )
                tvExamDate.text = "Ngày thi dự kiến: ${
                    formatter.format(chosenDate.time)
                }"
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )

        picker.datePicker.minDate = today.timeInMillis
        picker.show()
    }
}
