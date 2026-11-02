package com.example.ielts.ui.reading.adapter

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.remote.dto.QuestionDetailDto

class ReadingQuestionAdapter(
    private var questions: List<QuestionDetailDto>,
    private val getAnswer: (position: Int) -> String?,
    private val isFlagged: (position: Int) -> Boolean,
    private val onAnswerSelected: (position: Int, answer: String) -> Unit,
    private val onToggleFlag: (position: Int) -> Unit
) : RecyclerView.Adapter<ReadingQuestionAdapter.QuestionViewHolder>() {

    fun updateData(newQuestions: List<QuestionDetailDto>) {
        this.questions = newQuestions
        notifyDataSetChanged()
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestionBadge: TextView = itemView.findViewById(R.id.tvQuestionBadge)
        val tvQuestionLineRef: TextView = itemView.findViewById(R.id.tvQuestionLineRef)
        val btnToggleFlag: View = itemView.findViewById(R.id.btnToggleFlag)
        val ivFlagIcon: ImageView = itemView.findViewById(R.id.ivFlagIcon)
        val tvFlagStatus: TextView = itemView.findViewById(R.id.tvFlagStatus)
        val tvQuestionPrompt: TextView = itemView.findViewById(R.id.tvQuestionPrompt)

        // TFNG
        val layoutTfng: LinearLayout = itemView.findViewById(R.id.layoutTfngOptions)
        val btnTrue: TextView = itemView.findViewById(R.id.btnOptTrue)
        val btnFalse: TextView = itemView.findViewById(R.id.btnOptFalse)
        val btnNotGiven: TextView = itemView.findViewById(R.id.btnOptNotGiven)

        // Multiple Choice
        val layoutMultipleChoice: LinearLayout = itemView.findViewById(R.id.layoutMultipleChoice)
        val btnOptA: TextView = itemView.findViewById(R.id.btnOptA)
        val btnOptB: TextView = itemView.findViewById(R.id.btnOptB)
        val btnOptC: TextView = itemView.findViewById(R.id.btnOptC)
        val btnOptD: TextView = itemView.findViewById(R.id.btnOptD)

        // Fill Blank
        val layoutFillBlank: LinearLayout = itemView.findViewById(R.id.layoutFillBlank)
        val edtFillAnswer: EditText = itemView.findViewById(R.id.edtFillAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reading_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val item = questions[position]
        val questionNum = item.position
        val context = holder.itemView.context

        holder.tvQuestionBadge.text = "Câu $questionNum"
        holder.tvQuestionLineRef.text = item.lineRef ?: "Passage ${item.passageNumber} • ${item.type}"
        holder.tvQuestionPrompt.text = item.prompt

        // Flag Status
        val flagged = isFlagged(questionNum)
        if (flagged) {
            holder.btnToggleFlag.setBackgroundResource(R.drawable.bg_pill_soft_orange)
            holder.ivFlagIcon.setColorFilter(Color.parseColor("#D97706"))
            holder.tvFlagStatus.text = "Đã cắm cờ"
            holder.tvFlagStatus.setTextColor(Color.parseColor("#D97706"))
        } else {
            holder.btnToggleFlag.setBackgroundResource(R.drawable.bg_choice_unselected)
            holder.ivFlagIcon.setColorFilter(ContextCompat.getColor(context, R.color.text_secondary))
            holder.tvFlagStatus.text = "Cắm cờ"
            holder.tvFlagStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
        }

        holder.btnToggleFlag.setOnClickListener {
            onToggleFlag(questionNum)
            notifyItemChanged(holder.bindingAdapterPosition)
        }

        val currentAnswer = getAnswer(questionNum)

        // Handle Types
        when (item.type) {
            "TRUE_FALSE" -> {
                holder.layoutTfng.visibility = View.VISIBLE
                holder.layoutMultipleChoice.visibility = View.GONE
                holder.layoutFillBlank.visibility = View.GONE

                applyChoiceStyle(holder.btnTrue, currentAnswer == "TRUE", "TRUE")
                applyChoiceStyle(holder.btnFalse, currentAnswer == "FALSE", "FALSE")
                applyChoiceStyle(holder.btnNotGiven, currentAnswer == "NOT GIVEN", "NOT GIVEN")

                holder.btnTrue.setOnClickListener {
                    onAnswerSelected(questionNum, "TRUE")
                    notifyItemChanged(holder.bindingAdapterPosition)
                }
                holder.btnFalse.setOnClickListener {
                    onAnswerSelected(questionNum, "FALSE")
                    notifyItemChanged(holder.bindingAdapterPosition)
                }
                holder.btnNotGiven.setOnClickListener {
                    onAnswerSelected(questionNum, "NOT GIVEN")
                    notifyItemChanged(holder.bindingAdapterPosition)
                }
            }
            "MULTIPLE_CHOICE" -> {
                holder.layoutTfng.visibility = View.GONE
                holder.layoutMultipleChoice.visibility = View.VISIBLE
                holder.layoutFillBlank.visibility = View.GONE

                // Parse options if provided
                val parsedOpts = parseOptions(item.options)
                holder.btnOptA.text = parsedOpts.getOrNull(0) ?: "A. Lựa chọn A"
                holder.btnOptB.text = parsedOpts.getOrNull(1) ?: "B. Lựa chọn B"
                holder.btnOptC.text = parsedOpts.getOrNull(2) ?: "C. Lựa chọn C"
                holder.btnOptD.text = parsedOpts.getOrNull(3) ?: "D. Lựa chọn D"

                applyChoiceStyle(holder.btnOptA, currentAnswer == "A", holder.btnOptA.text.toString())
                applyChoiceStyle(holder.btnOptB, currentAnswer == "B", holder.btnOptB.text.toString())
                applyChoiceStyle(holder.btnOptC, currentAnswer == "C", holder.btnOptC.text.toString())
                applyChoiceStyle(holder.btnOptD, currentAnswer == "D", holder.btnOptD.text.toString())

                holder.btnOptA.setOnClickListener {
                    onAnswerSelected(questionNum, "A")
                    notifyItemChanged(holder.bindingAdapterPosition)
                }
                holder.btnOptB.setOnClickListener {
                    onAnswerSelected(questionNum, "B")
                    notifyItemChanged(holder.bindingAdapterPosition)
                }
                holder.btnOptC.setOnClickListener {
                    onAnswerSelected(questionNum, "C")
                    notifyItemChanged(holder.bindingAdapterPosition)
                }
                holder.btnOptD.setOnClickListener {
                    onAnswerSelected(questionNum, "D")
                    notifyItemChanged(holder.bindingAdapterPosition)
                }
            }
            "FILL_IN_BLANK" -> {
                holder.layoutTfng.visibility = View.GONE
                holder.layoutMultipleChoice.visibility = View.GONE
                holder.layoutFillBlank.visibility = View.VISIBLE

                holder.edtFillAnswer.tag = questionNum
                if (holder.edtFillAnswer.text.toString() != (currentAnswer ?: "")) {
                    holder.edtFillAnswer.setText(currentAnswer ?: "")
                }

                holder.edtFillAnswer.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        val text = holder.edtFillAnswer.text.toString().trim()
                        if (text.isNotEmpty()) {
                            onAnswerSelected(questionNum, text)
                        }
                    }
                }
            }
        }
    }

    private fun applyChoiceStyle(button: TextView, isSelected: Boolean, text: String) {
        val context = button.context
        if (isSelected) {
            button.setBackgroundResource(R.drawable.bg_choice_selected)
            button.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            button.text = "✓ $text"
        } else {
            button.setBackgroundResource(R.drawable.bg_choice_unselected)
            button.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            button.text = text
        }
    }

    private fun parseOptions(rawOptions: String?): List<String> {
        if (rawOptions.isNullOrBlank()) return emptyList()
        // Format: "A. xxx B. yyy C. zzz D. www"
        val regex = Regex("([A-D]\\.\\s+[^A-D]+)")
        val matches = regex.findAll(rawOptions).map { it.value.trim() }.toList()
        return if (matches.isNotEmpty()) matches else rawOptions.split("\n")
    }
}
