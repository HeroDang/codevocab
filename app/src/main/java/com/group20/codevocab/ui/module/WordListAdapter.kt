package com.group20.codevocab.ui.module

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.VocabularyEntity
import com.group20.codevocab.model.WordItem

class WordListAdapter(
    private var words: List<WordItem>,
    private val onSpeakClick: (WordItem) -> Unit
) :
    RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val tvMeaning: TextView = itemView.findViewById(R.id.tvMeaning)
        val tvPhonetic: TextView = itemView.findViewById(R.id.tvPhonetic)
        val btnSpeak: ImageView = itemView.findViewById(R.id.btnSpeak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.tvWord.text = word.textEn
        holder.tvMeaning.text = word.meaningVi
        holder.tvPhonetic.text = word.ipa ?: ""

        holder.btnSpeak.setOnClickListener {
            onSpeakClick(word)
        }
    }

    override fun getItemCount(): Int = words.size

    fun updateData(newWords: List<WordItem>) {
        words = newWords
        notifyDataSetChanged()
    }
}