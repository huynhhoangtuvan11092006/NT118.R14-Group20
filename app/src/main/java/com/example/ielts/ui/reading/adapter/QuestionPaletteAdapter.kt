package com.example.ielts.ui.reading.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R

class QuestionPaletteAdapter(
    private val totalQuestions: Int = 40,
    private val getAnswerState: (position: Int) -> Boolean,
    private val getFlagState: (position: Int) -> Boolean,
    private val getActivePosition: () -> Int,
    private val onQuestionSelected: (position: Int) -> Unit
) : RecyclerView.Adapter<QuestionPaletteAdapter.PaletteViewHolder>() {

    inner class PaletteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.tvPaletteNumber)
        val dotFlagged: View = itemView.findViewById(R.id.dotFlagged)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_palette_number, parent, false)
        return PaletteViewHolder(view)
    }

    override fun getItemCount(): Int = totalQuestions

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        val questionNum = position + 1
        val isAnswered = getAnswerState(questionNum)
        val isFlagged = getFlagState(questionNum)
        val isActive = getActivePosition() == questionNum
        val context = holder.itemView.context

        holder.tvNumber.text = questionNum.toString()

        // 1. Flag indicator
        holder.dotFlagged.visibility = if (isFlagged) View.VISIBLE else View.GONE

        // 2. Background and text color based on states
        when {
            isActive -> {
                holder.tvNumber.setBackgroundResource(R.drawable.bg_palette_circle_active)
                holder.tvNumber.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            }
            isAnswered -> {
                holder.tvNumber.setBackgroundResource(R.drawable.bg_palette_circle_done)
                holder.tvNumber.setTextColor(Color.WHITE)
            }
            else -> {
                holder.tvNumber.setBackgroundResource(R.drawable.bg_palette_circle_empty)
                holder.tvNumber.setTextColor(Color.parseColor("#64748B"))
            }
        }

        holder.itemView.setOnClickListener {
            onQuestionSelected(questionNum)
        }
    }
}
