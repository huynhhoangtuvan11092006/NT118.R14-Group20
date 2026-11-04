package com.example.ielts.ui.vocabulary.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ielts.R
import com.example.ielts.data.datasource.VocabularyDataSource
import com.example.ielts.data.datasource.VocabularyWord

enum class WordStatus(val label: String, val bgRes: Int, val textColor: String) {
    MASTERED("Đã thuộc", R.drawable.bg_tag_mastered, "#15803D"),
    REVIEW("Cần ôn lại", R.drawable.bg_tag_review, "#B45309"),
    LEARNING("Đang học", R.drawable.bg_tag_learning, "#003EA8"),
    NEW("Chưa học", R.drawable.bg_tag_new, "#64748B")
}

data class WordRowItem(val word: VocabularyWord, val status: WordStatus)

class VocabularyWordRowAdapter(
    private var items: List<WordRowItem>,
    private val onClick: (VocabularyWord) -> Unit
) : RecyclerView.Adapter<VocabularyWordRowAdapter.ViewHolder>() {

    fun update(newItems: List<WordRowItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_vocab_word_row, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvWord: TextView = view.findViewById(R.id.tvRowWord)
        private val tvPos: TextView = view.findViewById(R.id.tvRowPos)
        private val tvMeaning: TextView = view.findViewById(R.id.tvRowMeaning)
        private val tvStatus: TextView = view.findViewById(R.id.tvRowStatus)

        fun bind(item: WordRowItem) {
            tvWord.text = item.word.word.replaceFirstChar { it.uppercase() }
            tvPos.text = "• ${VocabularyDataSource.formatPartOfSpeech(item.word.pos)}"
            tvMeaning.text = item.word.meaningVi
            tvStatus.text = item.status.label
            tvStatus.setBackgroundResource(item.status.bgRes)
            tvStatus.setTextColor(Color.parseColor(item.status.textColor))
            itemView.setOnClickListener { onClick(item.word) }
        }
    }
}

