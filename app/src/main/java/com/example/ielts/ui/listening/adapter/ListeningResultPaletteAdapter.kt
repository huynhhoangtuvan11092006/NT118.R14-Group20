package com.example.ielts.ui.listening.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.remote.dto.ListeningResultReviewDto

class ListeningResultPaletteAdapter(
    private val items: List<ListeningResultReviewDto>,
    private val onQuestionClicked: (Int) -> Unit
) : RecyclerView.Adapter<ListeningResultPaletteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listening_result_palette_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPaletteCellNumber: TextView = itemView.findViewById(R.id.tvPaletteCellNumber)

        fun bind(item: ListeningResultReviewDto) {
            tvPaletteCellNumber.text = item.position.toString()

            when {
                item.isCorrect -> {
                    tvPaletteCellNumber.setBackgroundResource(R.drawable.bg_result_palette_correct)
                    tvPaletteCellNumber.setTextColor(Color.WHITE)
                }
                item.userAnswer.isNullOrBlank() -> {
                    tvPaletteCellNumber.setBackgroundResource(R.drawable.bg_result_palette_unanswered)
                    tvPaletteCellNumber.setTextColor(Color.parseColor("#64748B"))
                }
                else -> {
                    tvPaletteCellNumber.setBackgroundResource(R.drawable.bg_result_palette_incorrect)
                    tvPaletteCellNumber.setTextColor(Color.WHITE)
                }
            }

            itemView.setOnClickListener {
                onQuestionClicked(item.position)
            }
        }
    }
}

