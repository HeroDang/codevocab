package com.group20.codevocab.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ItemReviewWordBinding

data class ReviewableWord(val word: String, val meaning: String, var isChecked: Boolean = true)

class ReviewWordAdapter : ListAdapter<ReviewableWord, ReviewWordAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReviewWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemReviewWordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReviewableWord) {
            binding.tvWord.text = item.word
            binding.tvMeaning.text = item.meaning
            binding.checkbox.isChecked = item.isChecked

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
            }

            binding.ivEdit.setOnClickListener {
                it.findNavController().navigate(R.id.action_reviewWordFragment_to_editWordFragment)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ReviewableWord>() {
        override fun areItemsTheSame(oldItem: ReviewableWord, newItem: ReviewableWord): Boolean {
            return oldItem.word == newItem.word
        }

        override fun areContentsTheSame(oldItem: ReviewableWord, newItem: ReviewableWord): Boolean {
            return oldItem == newItem
        }
    }
}