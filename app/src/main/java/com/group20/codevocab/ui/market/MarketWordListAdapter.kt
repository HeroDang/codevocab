package com.group20.codevocab.ui.market

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.ItemWordMarketBinding
import com.group20.codevocab.model.WordItem

class MarketWordListAdapter(
    private val onSpeakClick: (WordItem) -> Unit
) : ListAdapter<WordItem, MarketWordListAdapter.WordViewHolder>(DiffCallback()) {

    inner class WordViewHolder(val binding: ItemWordMarketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = getItem(position)
        holder.binding.tvWordInEnglish.text = word.textEn
        holder.binding.tvWordInVietnamese.text = word.meaningVi
        holder.binding.tvPronunciation.text = word.ipa ?: ""

        holder.binding.ivSpeaker.setOnClickListener {
            onSpeakClick(word)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<WordItem>() {
        override fun areItemsTheSame(oldItem: WordItem, newItem: WordItem):
            Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: WordItem, newItem: WordItem):
            Boolean = oldItem == newItem
    }
}