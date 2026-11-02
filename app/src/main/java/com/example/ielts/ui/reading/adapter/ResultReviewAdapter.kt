package com.example.ielts.ui.reading.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.remote.dto.QuestionResultReviewDto

class ResultReviewAdapter(
    private val reviews: List<QuestionResultReviewDto>,
    private val onViewInPassageClicked: (item: QuestionResultReviewDto) -> Unit
) : RecyclerView.Adapter<ResultReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestionNum: TextView = itemView.findViewById(R.id.tvReviewQuestionNum)
        val tvStatusBadge: TextView = itemView.findViewById(R.id.tvReviewStatusBadge)
        val tvPrompt: TextView = itemView.findViewById(R.id.tvReviewPrompt)
        val tvUserAnswer: TextView = itemView.findViewById(R.id.tvReviewUserAnswer)
        val tvCorrectAnswer: TextView = itemView.findViewById(R.id.tvReviewCorrectAnswer)
        val tvExplanation: TextView = itemView.findViewById(R.id.tvReviewExplanation)
        val btnViewInPassage: View = itemView.findViewById(R.id.btnViewInPassage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun getItemCount(): Int = reviews.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = reviews[position]

        holder.tvQuestionNum.text = "Câu ${item.position} • Passage ${item.passageNumber}"
        holder.tvPrompt.text = item.prompt ?: "Câu hỏi ${item.position}"

        val isUnanswered = item.userAnswer.isNullOrBlank()
        val userAns = if (isUnanswered) "Chưa điền" else item.userAnswer
        holder.tvUserAnswer.text = userAns
        holder.tvCorrectAnswer.text = item.correctAnswer

        if (item.isCorrect) {
            holder.tvStatusBadge.text = "✓ Chính xác (+1)"
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_green_bg)
            holder.tvStatusBadge.setTextColor(Color.parseColor("#16A34A"))
            holder.tvUserAnswer.setTextColor(Color.parseColor("#16A34A"))
        } else if (isUnanswered) {
            holder.tvStatusBadge.text = "! Chưa làm (0)"
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_purple_bg)
            holder.tvStatusBadge.setTextColor(Color.parseColor("#E11D48"))
            holder.tvUserAnswer.setTextColor(Color.parseColor("#94A3B8"))
        } else {
            holder.tvStatusBadge.text = "✗ Chưa đúng (0)"
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_purple_bg)
            holder.tvStatusBadge.setTextColor(Color.parseColor("#DC2626"))
            holder.tvUserAnswer.setTextColor(Color.parseColor("#DC2626"))
        }

        holder.tvExplanation.text = item.explanation ?: "Dẫn chứng đối chiếu trực tiếp từ bài đọc Passage ${item.passageNumber}."

        holder.btnViewInPassage.setOnClickListener {
            onViewInPassageClicked(item)
        }
    }
}
