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
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                notifyItemChanged(pos)
            }
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
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos)
                }
                holder.btnFalse.setOnClickListener {
                    onAnswerSelected(questionNum, "FALSE")
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos)
                }
                holder.btnNotGiven.setOnClickListener {
                    onAnswerSelected(questionNum, "NOT GIVEN")
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos)
                }
            }
            "MULTIPLE_CHOICE" -> {
                holder.layoutTfng.visibility = View.GONE
                holder.layoutMultipleChoice.visibility = View.VISIBLE
                holder.layoutFillBlank.visibility = View.GONE

                // Parse options if provided
                val parsedOpts = parseOptions(item.options)
                val optA = parsedOpts.getOrNull(0) ?: "A. Lựa chọn A"
                val optB = parsedOpts.getOrNull(1) ?: "B. Lựa chọn B"
                val optC = parsedOpts.getOrNull(2) ?: "C. Lựa chọn C"
                val optD = parsedOpts.getOrNull(3) ?: "D. Lựa chọn D"

                holder.btnOptA.text = optA
                holder.btnOptB.text = optB
                holder.btnOptC.text = optC
                holder.btnOptD.text = optD

                applyChoiceStyle(holder.btnOptA, currentAnswer == "A", optA)
                applyChoiceStyle(holder.btnOptB, currentAnswer == "B", optB)
                applyChoiceStyle(holder.btnOptC, currentAnswer == "C", optC)
                applyChoiceStyle(holder.btnOptD, currentAnswer == "D", optD)

                holder.btnOptA.setOnClickListener {
                    onAnswerSelected(questionNum, "A")
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos)
                }
                holder.btnOptB.setOnClickListener {
                    onAnswerSelected(questionNum, "B")
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos)
                }
                holder.btnOptC.setOnClickListener {
                    onAnswerSelected(questionNum, "C")
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos)
                }
                holder.btnOptD.setOnClickListener {
                    onAnswerSelected(questionNum, "D")
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos)
                }
            }
            else -> {
                // FILL_IN_BLANK or any text input question
                holder.layoutTfng.visibility = View.GONE
                holder.layoutMultipleChoice.visibility = View.GONE
                holder.layoutFillBlank.visibility = View.VISIBLE

                (holder.edtFillAnswer.tag as? TextWatcher)?.let { oldWatcher ->
                    holder.edtFillAnswer.removeTextChangedListener(oldWatcher)
                }

                if (holder.edtFillAnswer.text.toString() != (currentAnswer ?: "")) {
                    holder.edtFillAnswer.setText(currentAnswer ?: "")
                }

                val watcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        val text = s?.toString()?.trim() ?: ""
                        onAnswerSelected(questionNum, text)
                    }
                }
                holder.edtFillAnswer.tag = watcher
                holder.edtFillAnswer.addTextChangedListener(watcher)
            }
        }
    }

    private fun applyChoiceStyle(button: TextView, isSelected: Boolean, baseText: String) {
        val cleanText = if (baseText.startsWith("✓ ")) baseText.removePrefix("✓ ") else baseText
        val context = button.context
        if (isSelected) {
            button.setBackgroundResource(R.drawable.bg_choice_selected)
            button.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            button.text = "✓ $cleanText"
        } else {
            button.setBackgroundResource(R.drawable.bg_choice_unselected)
            button.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            button.text = cleanText
        }
    }

    private fun parseOptions(rawOptions: Any?): List<String> {
        if (rawOptions == null) return emptyList()
        if (rawOptions is List<*>) {
            return rawOptions.mapNotNull { it?.toString() }
        }
        val str = rawOptions.toString().trim()
        if (str.isEmpty()) return emptyList()
        val regex = Regex("([A-D]\\.\\s+[^A-D]+)")
        val matches = regex.findAll(str).map { it.value.trim() }.toList()
        return if (matches.isNotEmpty()) matches else str.split("\n")
    }
}
