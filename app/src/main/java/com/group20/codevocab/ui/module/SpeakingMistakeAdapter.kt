package com.group20.codevocab.ui.module

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.ItemSpeakingMistakeBinding

data class SpeakingMistake(
    val index: Int,
    val original: String,
    val phonetics: String,
    val recognized: String,
    val mistakes: String
)

class SpeakingMistakeAdapter(private val items: List<SpeakingMistake>) :
    RecyclerView.Adapter<SpeakingMistakeAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSpeakingMistakeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSpeakingMistakeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            chipIndex.text = "Sentence ${item.index}"
            tvOriginal.text = item.original
            tvPhonetics.text = item.phonetics
            tvRecognized.text = "Your pronunciation: ${item.recognized}"
            tvMistakesList.text = "Mistakes: ${item.mistakes}"
        }
    }

    override fun getItemCount() = items.size
}
