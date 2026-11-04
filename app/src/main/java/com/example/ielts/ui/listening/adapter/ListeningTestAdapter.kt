package com.example.ielts.ui.listening.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.remote.dto.ListeningTestListItemDto

class ListeningTestAdapter(
    private var testList: List<ListeningTestListItemDto>,
    private val onStartTest: (ListeningTestListItemDto) -> Unit
) : RecyclerView.Adapter<ListeningTestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSeriesBadge: TextView = itemView.findViewById(R.id.tvSeriesBadge)
        val tvNewBadge: TextView = itemView.findViewById(R.id.tvNewBadge)
        val tvStatusPill: TextView = itemView.findViewById(R.id.tvStatusPill)
        val tvTestTitle: TextView = itemView.findViewById(R.id.tvTestTitle)
        val tvTestSubtitle: TextView = itemView.findViewById(R.id.tvTestSubtitle)
        val tvSection1: TextView = itemView.findViewById(R.id.tvSection1)
        val tvSection2: TextView = itemView.findViewById(R.id.tvSection2)
        val tvSection3: TextView = itemView.findViewById(R.id.tvSection3)
        val tvSection4: TextView = itemView.findViewById(R.id.tvSection4)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvTotalQuestions: TextView = itemView.findViewById(R.id.tvTotalQuestions)
        val btnStartTest: View = itemView.findViewById(R.id.btnStartTest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_listening_test_card, parent, false)
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
            else -> {
                holder.tvSeriesBadge.text = "Cambridge IELTS"
                holder.tvNewBadge.visibility = View.GONE
            }
        }

        // 2. Title & Subtitle
        holder.tvTestTitle.text = item.title
        holder.tvTestSubtitle.text = item.summary ?: "Bộ đề chuẩn khảo thí British Council & IDP"

        // 3. Sections list
        val sections = item.sectionTitles
        if (sections.isNotEmpty()) {
            holder.tvSection1.text = "S1  " + sections.getOrElse(0) { "Section 1" }
            holder.tvSection2.text = "S2  " + sections.getOrElse(1) { "Section 2" }
            holder.tvSection3.text = "S3  " + sections.getOrElse(2) { "Section 3" }
            holder.tvSection4.text = "S4  " + sections.getOrElse(3) { "Section 4" }
        } else {
            holder.tvSection1.text = "S1  Section 1: Conversation & Inquiry"
            holder.tvSection2.text = "S2  Section 2: Local Community Monologue"
            holder.tvSection3.text = "S3  Section 3: Academic Students Discussion"
            holder.tvSection4.text = "S4  Section 4: University Lecture"
        }

        // 4. Meta info
        holder.tvDuration.text = "${item.durationMinutes} phút"
        holder.tvTotalQuestions.text = "${item.totalQuestions} câu"

        // 5. User progress status pill
        val progress = item.userProgress
        if (progress != null && progress.bandEstimate != null) {
            holder.tvStatusPill.text = "Đã làm • Band ${progress.bandEstimate}"
            holder.tvStatusPill.setBackgroundResource(R.drawable.bg_badge_pill)
            holder.tvStatusPill.setTextColor(Color.parseColor("#15803D"))
        } else {
            holder.tvStatusPill.text = "Chưa làm"
            holder.tvStatusPill.setBackgroundResource(R.drawable.bg_pill_soft_gray)
            holder.tvStatusPill.setTextColor(Color.parseColor("#64748B"))
        }

        // 6. Start button click
        holder.btnStartTest.setOnClickListener {
            onStartTest(item)
        }
        holder.itemView.setOnClickListener {
            onStartTest(item)
        }
    }

    override fun getItemCount(): Int = testList.size

    fun updateData(newList: List<ListeningTestListItemDto>) {
        testList = newList
        notifyDataSetChanged()
    }
}

