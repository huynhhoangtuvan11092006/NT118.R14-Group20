package com.example.ielts.ui.writing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.datasource.WritingTestItem

class WritingTestAdapter(
    private var testList: List<WritingTestItem>,
    private val onStartWriting: (WritingTestItem) -> Unit
) : RecyclerView.Adapter<WritingTestAdapter.WritingViewHolder>() {

    fun updateData(newList: List<WritingTestItem>) {
        this.testList = newList
        notifyDataSetChanged()
    }

    inner class WritingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSeriesBadge: TextView = itemView.findViewById(R.id.tvSeriesBadge)
        val tvTaskTypeBadge: TextView = itemView.findViewById(R.id.tvTaskTypeBadge)
        val tvTopicBadge: TextView = itemView.findViewById(R.id.tvTopicBadge)
        val tvTestTitle: TextView = itemView.findViewById(R.id.tvTestTitle)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvMinWords: TextView = itemView.findViewById(R.id.tvMinWords)
        val tvPromptPreview: TextView = itemView.findViewById(R.id.tvPromptPreview)
        val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        val btnStartWriting: Button = itemView.findViewById(R.id.btnStartWriting)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WritingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_writing_test_card, parent, false)
        return WritingViewHolder(view)
    }

    override fun getItemCount(): Int = testList.size

    override fun onBindViewHolder(holder: WritingViewHolder, position: Int) {
        val item = testList[position]
        val context = holder.itemView.context

        holder.tvSeriesBadge.text = item.series
        holder.tvTestTitle.text = item.title
        holder.tvDuration.text = "⏱ ${item.durationMinutes} phút"
        holder.tvMinWords.text = "📝 Tối thiểu ${item.minWords} từ"
        holder.tvTopicBadge.text = item.topic
        holder.tvPromptPreview.text = item.prompt.replace("\n\n", " ")

        if (item.taskType == "ACADEMIC_TASK_1") {
            holder.tvTaskTypeBadge.text = "Task 1"
            holder.tvTaskTypeBadge.setBackgroundResource(R.drawable.bg_pill_soft_indigo)
            holder.tvTaskTypeBadge.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
        } else {
            holder.tvTaskTypeBadge.text = "Task 2"
            holder.tvTaskTypeBadge.setBackgroundResource(R.drawable.bg_pill_soft_orange)
            holder.tvTaskTypeBadge.setTextColor(ContextCompat.getColor(context, R.color.badge_orange_text))
        }

        // Check if user has already written this essay (from local prefs)
        val prefs = context.getSharedPreferences("writing_practice_prefs", android.content.Context.MODE_PRIVATE)
        val savedBand = prefs.getFloat("saved_band_${item.id}", -1f)
        val savedWords = prefs.getInt("saved_words_${item.id}", 0)

        if (savedBand > 0f) {
            holder.tvStatusBadge.text = "✓ Đã chấm: Band $savedBand • $savedWords từ"
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.accent_green))
            holder.btnStartWriting.text = "Viết lại  ›"
        } else {
            holder.tvStatusBadge.text = "Chưa làm"
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            holder.btnStartWriting.text = "Bắt đầu viết  ›"
        }

        holder.btnStartWriting.setOnClickListener {
            onStartWriting(item)
        }
        holder.itemView.setOnClickListener {
            onStartWriting(item)
        }
    }
}

