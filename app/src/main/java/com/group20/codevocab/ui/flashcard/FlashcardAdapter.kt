package com.group20.codevocab.ui.flashcard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity
import com.group20.codevocab.data.local.entity.VocabularyEntity

class FlashcardAdapter(
    private var vocabList: List<Pair<VocabularyEntity, FlashcardProgressEntity?>> = emptyList(),
    val onKnownClicked: (FlashcardProgressEntity) -> Unit
) : RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {

    fun submitList(newList: List<Pair<VocabularyEntity, FlashcardProgressEntity?>>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = vocabList.size
            override fun getNewListSize() = newList.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                vocabList[oldItemPosition].first.id == newList[newItemPosition].first.id
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                vocabList[oldItemPosition].second?.isKnown == newList[newItemPosition].second?.isKnown
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        vocabList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class FlashcardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val tvMeaning: TextView = itemView.findViewById(R.id.tvMeaning)
        val tvPhonetic: TextView = itemView.findViewById(R.id.tvPhonetic)
        val btnKnown: Button = itemView.findViewById(R.id.btnKnown)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flashcard, parent, false)
        return FlashcardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        val (vocab, flash) = vocabList[position]
        holder.tvWord.text = vocab.word
        holder.tvMeaning.text = vocab.meaningVi
        holder.tvPhonetic.text = vocab.phonetic ?: ""

        // Hiển thị trạng thái học
        val known = flash?.isKnown == true
        holder.btnKnown.text = if (known) "✅ Đã thuộc" else "📖 Học lại"
        holder.btnKnown.alpha = if (known) 0.6f else 1.0f

        // Khi click -> gọi callback
        holder.btnKnown.setOnClickListener {
            flash?.let { onKnownClicked(it) }
        }
    }

    override fun getItemCount() = vocabList.size
}
