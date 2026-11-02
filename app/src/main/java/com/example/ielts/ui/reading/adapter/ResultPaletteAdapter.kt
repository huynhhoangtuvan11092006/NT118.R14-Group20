package com.example.ielts.ui.reading.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.remote.dto.QuestionResultReviewDto

class ResultPaletteAdapter(
    private val reviews: List<QuestionResultReviewDto>,
    private val onQuestionClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<ResultPaletteAdapter.PaletteViewHolder>() {

    var selectedPosition: Int = 1

    inner class PaletteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewSelectionRing: View = itemView.findViewById(R.id.viewSelectionRing)
        val tvNumber: TextView = itemView.findViewById(R.id.tvPaletteNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result_palette, parent, false)
        return PaletteViewHolder(view)
    }

    override fun getItemCount(): Int = reviews.size

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        val item = reviews[position]
        val questionNum = item.position

        holder.tvNumber.text = questionNum.toString()

        // 1. Selection indicator ring
        val isSelected = (selectedPosition == questionNum)
        holder.viewSelectionRing.visibility = if (isSelected) View.VISIBLE else View.GONE

        // 2. Status styling
        val isUnanswered = item.userAnswer.isNullOrBlank()
        when {
            item.isCorrect -> {
                // Đúng -> Màu xanh lá
                holder.tvNumber.setBackgroundResource(R.drawable.bg_result_palette_correct)
                holder.tvNumber.setTextColor(Color.WHITE)
            }
            !isUnanswered -> {
                // Làm rồi nhưng sai -> Màu đỏ
                holder.tvNumber.setBackgroundResource(R.drawable.bg_result_palette_incorrect)
                holder.tvNumber.setTextColor(Color.WHITE)
            }
            else -> {
                // Chưa làm -> Màu xám viền slate
                holder.tvNumber.setBackgroundResource(R.drawable.bg_result_palette_unanswered)
                holder.tvNumber.setTextColor(Color.parseColor("#64748B"))
            }
        }

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = questionNum
            notifyItemChanged(oldPos - 1)
            notifyItemChanged(selectedPosition - 1)
            onQuestionClicked(questionNum)
        }
    }

    fun setSelected(position: Int) {
        if (selectedPosition != position && position in 1..reviews.size) {
            val oldPos = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPos - 1)
            notifyItemChanged(selectedPosition - 1)
        }
    }
}
