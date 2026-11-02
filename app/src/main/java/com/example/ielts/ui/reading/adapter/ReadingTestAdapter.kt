package com.example.ielts.ui.reading.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.remote.dto.ReadingTestListItemDto

class ReadingTestAdapter(
    private var testList: List<ReadingTestListItemDto>,
    private val onStartTest: (ReadingTestListItemDto) -> Unit,
    private val onViewAnalysis: (ReadingTestListItemDto) -> Unit
) : RecyclerView.Adapter<ReadingTestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSeriesBadge: TextView = itemView.findViewById(R.id.tvSeriesBadge)
        val tvNewBadge: TextView = itemView.findViewById(R.id.tvNewBadge)
        val tvStatusPill: TextView = itemView.findViewById(R.id.tvStatusPill)
        val tvTestTitle: TextView = itemView.findViewById(R.id.tvTestTitle)
        val tvTestSubtitle: TextView = itemView.findViewById(R.id.tvTestSubtitle)
        val tvPassage1: TextView = itemView.findViewById(R.id.tvPassage1)
        val tvPassage2: TextView = itemView.findViewById(R.id.tvPassage2)
        val tvPassage3: TextView = itemView.findViewById(R.id.tvPassage3)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvTotalQuestions: TextView = itemView.findViewById(R.id.tvTotalQuestions)
        val tvDifficulty: TextView = itemView.findViewById(R.id.tvDifficulty)
        val layoutStartAction: View = itemView.findViewById(R.id.layoutStartAction)
        val btnStartTest: TextView = itemView.findViewById(R.id.btnStartTest)
        val layoutCompletedActions: View = itemView.findViewById(R.id.layoutCompletedActions)
        val btnRetake: TextView = itemView.findViewById(R.id.btnRetake)
        val btnViewAnalysis: TextView = itemView.findViewById(R.id.btnViewAnalysis)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reading_test_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = testList[position]

        // 1. Series badge
        val titleUpper = item.title.uppercase()
        when {
            titleUpper.contains("19") -> {
                holder.tvSeriesBadge.text = "Cambridge 19"
                holder.tvNewBadge.visibility = View.VISIBLE
            }
            titleUpper.contains("18") -> {
                holder.tvSeriesBadge.text = "Cambridge 18"
                holder.tvNewBadge.visibility = View.GONE
            }
            titleUpper.contains("17") -> {
                holder.tvSeriesBadge.text = "Cambridge 17"
                holder.tvNewBadge.visibility = View.GONE
            }
            titleUpper.contains("16") -> {
                holder.tvSeriesBadge.text = "Cambridge 16"
                holder.tvNewBadge.visibility = View.GONE
            }
            titleUpper.contains("15") -> {
                holder.tvSeriesBadge.text = "Cambridge 15"
                holder.tvNewBadge.visibility = View.GONE
            }
            titleUpper.contains("14") -> {
                holder.tvSeriesBadge.text = "Cambridge 14"
                holder.tvNewBadge.visibility = View.GONE
            }
            titleUpper.contains("13") -> {
                holder.tvSeriesBadge.text = "Cambridge 13"
                holder.tvNewBadge.visibility = View.GONE
            }
            titleUpper.contains("12") -> {
                holder.tvSeriesBadge.text = "Cambridge 12"
                holder.tvNewBadge.visibility = View.GONE
            }
            titleUpper.contains("11") -> {
                holder.tvSeriesBadge.text = "Cambridge 11"
                holder.tvNewBadge.visibility = View.GONE
            }
            else -> {
                holder.tvSeriesBadge.text = "Cambridge IELTS"
                holder.tvNewBadge.visibility = View.GONE
            }
        }

        // 2. Title & Subtitle
        holder.tvTestTitle.text = item.title
        holder.tvTestSubtitle.text = item.summary ?: "Bộ đề chuẩn khảo thí British Council & IDP"

        // 3. Passages list
        val passages = item.passageTitles
        if (passages.isNotEmpty()) {
            holder.tvPassage1.text = "P1  " + passages.getOrElse(0) { "Passage 1" }
            holder.tvPassage2.text = "P2  " + passages.getOrElse(1) { "Passage 2" }
            holder.tvPassage3.text = "P3  " + passages.getOrElse(2) { "Passage 3" }
        } else {
            holder.tvPassage1.text = "P1  Urban Farming & Green Cities"
            holder.tvPassage2.text = "P2  The Forest Canopy Exploration"
            holder.tvPassage3.text = "P3  Artificial Intelligence in Modern Healthcare"
        }

        // 4. Metadata badges
        holder.tvDuration.text = "${item.durationMinutes} phút"
        holder.tvTotalQuestions.text = "${item.totalQuestions} câu"
        holder.tvDifficulty.text = "Độ khó: ${item.difficulty}"

        // 5. Completion Status
        val progress = item.userProgress
        if (progress != null && progress.bandEstimate != null) {
            val scoreText = if (progress.highestScore != null) {
                "${progress.highestScore.toInt()}/${item.totalQuestions} đúng"
            } else {
                "Hoàn thành"
            }
            holder.tvStatusPill.text = "Band ${progress.bandEstimate} • $scoreText"
            holder.tvStatusPill.setBackgroundResource(R.drawable.bg_pill_status_purple)
            holder.tvStatusPill.setTextColor(Color.parseColor("#7C3AED"))

            holder.layoutCompletedActions.visibility = View.VISIBLE
            holder.layoutStartAction.visibility = View.GONE
        } else {
            holder.tvStatusPill.text = "Chưa làm"
            holder.tvStatusPill.setBackgroundResource(R.drawable.bg_pill_soft_gray)
            holder.tvStatusPill.setTextColor(Color.parseColor("#64748B"))

            holder.layoutCompletedActions.visibility = View.GONE
            holder.layoutStartAction.visibility = View.VISIBLE
        }

        // 6. Action Clicks
        holder.btnStartTest.setOnClickListener { onStartTest(item) }
        holder.btnRetake.setOnClickListener { onStartTest(item) }
        holder.btnViewAnalysis.setOnClickListener { onViewAnalysis(item) }
        holder.itemView.setOnClickListener {
            if (item.userProgress != null) {
                onViewAnalysis(item)
            } else {
                onStartTest(item)
            }
        }
    }

    override fun getItemCount(): Int = testList.size

    fun updateData(newList: List<ReadingTestListItemDto>) {
        this.testList = newList
        notifyDataSetChanged()
    }
}
