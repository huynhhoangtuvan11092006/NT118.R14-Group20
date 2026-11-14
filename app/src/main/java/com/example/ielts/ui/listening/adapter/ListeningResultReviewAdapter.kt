package com.example.ielts.ui.listening.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.remote.dto.ListeningResultReviewDto

class ListeningResultReviewAdapter(
    private var items: List<ListeningResultReviewDto>,
    private val onListenAtEvidence: (ListeningResultReviewDto) -> Unit
) : RecyclerView.Adapter<ListeningResultReviewAdapter.ViewHolder>() {

    fun updateList(newItems: List<ListeningResultReviewDto>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listening_result_review_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvReviewQuestionNum: TextView = itemView.findViewById(R.id.tvReviewQuestionNum)
        private val tvReviewStatusBadge: TextView = itemView.findViewById(R.id.tvReviewStatusBadge)
        private val tvReviewPrompt: TextView = itemView.findViewById(R.id.tvReviewPrompt)
        private val tvReviewUserAnswer: TextView = itemView.findViewById(R.id.tvReviewUserAnswer)
        private val tvReviewCorrectAnswer: TextView = itemView.findViewById(R.id.tvReviewCorrectAnswer)
        private val tvReviewEvidenceQuote: TextView = itemView.findViewById(R.id.tvReviewEvidenceQuote)
        private val tvReviewExplanation: TextView = itemView.findViewById(R.id.tvReviewExplanation)
        private val layoutEvidenceCard: View = itemView.findViewById(R.id.layoutEvidenceCard)
        private val btnListenAtEvidence: View = itemView.findViewById(R.id.btnListenAtEvidence)
        private val tvListenTimestampText: TextView = itemView.findViewById(R.id.tvListenTimestampText)
        private val btnReviewViewAttachment: TextView? = itemView.findViewById(R.id.btnReviewViewAttachment)

        fun bind(item: ListeningResultReviewDto) {
            tvReviewQuestionNum.text = "Câu ${item.position} • Sec ${item.sectionNumber}"
            tvReviewPrompt.text = item.prompt ?: "Câu hỏi số ${item.position}"

            when {
                item.isCorrect -> {
                    tvReviewStatusBadge.text = "✓ Đúng (+1)"
                    tvReviewStatusBadge.setBackgroundResource(R.drawable.bg_badge_green_bg)
                    tvReviewStatusBadge.setTextColor(Color.parseColor("#16A34A"))
                }
                item.userAnswer.isNullOrBlank() -> {
                    tvReviewStatusBadge.text = "Chưa làm"
                    tvReviewStatusBadge.setBackgroundResource(R.drawable.bg_badge_unanswered)
                    tvReviewStatusBadge.setTextColor(Color.parseColor("#64748B"))
                }
                else -> {
                    tvReviewStatusBadge.text = "✗ Chưa đúng"
                    tvReviewStatusBadge.setBackgroundResource(R.drawable.bg_badge_incorrect)
                    tvReviewStatusBadge.setTextColor(Color.parseColor("#EF4444"))
                }
            }

            if (item.userAnswer.isNullOrBlank()) {
                tvReviewUserAnswer.text = "(Chưa trả lời)"
                tvReviewUserAnswer.setTextColor(Color.parseColor("#94A3B8"))
            } else {
                tvReviewUserAnswer.text = item.userAnswer
                if (item.isCorrect) {
                    tvReviewUserAnswer.setTextColor(Color.parseColor("#16A34A"))
                } else {
                    tvReviewUserAnswer.setTextColor(Color.parseColor("#EF4444"))
                }
            }

            tvReviewCorrectAnswer.text = item.correctAnswer

            if (item.imageResId != null && item.imageResId != 0) {
                btnReviewViewAttachment?.visibility = View.VISIBLE
                btnReviewViewAttachment?.text = item.imageTitle ?: "Xem tài liệu / bản đồ đính kèm"
                btnReviewViewAttachment?.setOnClickListener {
                    com.example.ielts.ui.listening.ListeningImageDialogHelper.show(
                        itemView.context,
                        item.imageResId,
                        item.imageTitle
                    )
                }
            } else {
                btnReviewViewAttachment?.visibility = View.GONE
            }

            if (!item.evidenceQuote.isNullOrBlank()) {
                layoutEvidenceCard.visibility = View.VISIBLE
                tvReviewEvidenceQuote.text = "\"${item.evidenceQuote}\""
                tvReviewExplanation.text = item.explanation ?: "Dẫn chứng đối chiếu trực tiếp từ Section ${item.sectionNumber}."
            } else {
                layoutEvidenceCard.visibility = View.GONE
            }

            if (item.timestampSeconds > 0 || item.timestampStr != "00:00") {
                btnListenAtEvidence.visibility = View.VISIBLE
                tvListenTimestampText.text = "Nghe lại câu này (${item.timestampStr})"
                btnListenAtEvidence.setOnClickListener {
                    onListenAtEvidence(item)
                }
            } else {
                btnListenAtEvidence.visibility = View.GONE
            }
        }
    }
}

