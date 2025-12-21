package com.group20.codevocab.ui.market

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.ItemWordMarketBinding
import com.group20.codevocab.model.WordItem

class WordMarketAdapter(
    private var words: List<WordItem>,
    private val onSpeakClick: (WordItem) -> Unit
) : RecyclerView.Adapter<WordMarketAdapter.WordViewHolder>() {

    inner class WordViewHolder(val binding: ItemWordMarketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.binding.tvWordInEnglish.text = word.textEn
        holder.binding.tvWordInVietnamese.text = word.meaningVi
        holder.binding.tvPronunciation.text = word.ipa ?: ""

        holder.binding.ivSpeaker.setOnClickListener {
            onSpeakClick(word)
        }
    }

    override fun getItemCount(): Int = words.size

    fun updateData(newWords: List<WordItem>) {
        words = newWords
        notifyDataSetChanged()
    }
}