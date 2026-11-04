package com.example.ielts.ui.listening.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R

class ListeningPaletteAdapter(
    private val totalQuestions: Int = 40,
    private val userAnswers: Map<Int, String>,
    private val flaggedQuestions: Set<Int>,
    private val onQuestionClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ListeningPaletteAdapter.PaletteViewHolder>() {

    class PaletteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNum: TextView = itemView.findViewById(R.id.tvPaletteQuestionNum)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question_palette_cell, parent, false)
        return PaletteViewHolder(view)
    }

    override fun getItemCount(): Int = totalQuestions

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        val qNum = position + 1
        val isAnswered = !userAnswers[qNum].isNullOrBlank()
        val isFlagged = flaggedQuestions.contains(qNum)

        when {
            isFlagged -> {
                holder.tvNum.text = "$qNum ★"
                holder.tvNum.setBackgroundResource(R.drawable.bg_question_palette_flagged)
                holder.tvNum.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.tertiary_orange_container))
            }
            isAnswered -> {
                holder.tvNum.text = "$qNum ✓"
                holder.tvNum.setBackgroundResource(R.drawable.bg_question_palette_answered)
                holder.tvNum.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            }
            else -> {
                holder.tvNum.text = "$qNum"
                holder.tvNum.setBackgroundResource(R.drawable.bg_question_palette_unanswered)
                holder.tvNum.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_primary))
            }
        }

        holder.itemView.setOnClickListener {
            onQuestionClick(qNum)
        }
    }
}
