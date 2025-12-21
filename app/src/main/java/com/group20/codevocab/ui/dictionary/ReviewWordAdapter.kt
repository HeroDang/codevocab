package com.group20.codevocab.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.ItemReviewWordBinding
import com.group20.codevocab.model.ReviewableWord

class ReviewWordAdapter(
    private val onEditClick: (ReviewableWord) -> Unit
) : ListAdapter<ReviewableWord, ReviewWordAdapter.ReviewWordViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewWordViewHolder {
        val binding = ItemReviewWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewWordViewHolder(binding, onEditClick)
    }

    override fun onBindViewHolder(holder: ReviewWordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewWordViewHolder(
        private val binding: ItemReviewWordBinding,
        private val onEditClick: (ReviewableWord) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        private var currentWord: ReviewableWord? = null

        init {
            binding.ivEdit.setOnClickListener {
                currentWord?.let { word ->
                    onEditClick(word)
                }
            }
        }

        fun bind(item: ReviewableWord) {
            currentWord = item
            binding.tvWord.text = item.textEn ?: "[No Word]"
            binding.tvMeaning.text = item.meaningVi ?: ""
            binding.tvIpa.text = item.ipa ?: ""
            binding.tvPartOfSpeech.text = item.partOfSpeech ?: ""
            
            // Unset listener to avoid triggering it during binding
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.isChecked = item.isChecked
            
            // Set listener to update model
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                currentWord?.isChecked = isChecked
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ReviewableWord>() {
        override fun areItemsTheSame(oldItem: ReviewableWord, newItem: ReviewableWord): Boolean {
            return oldItem.textEn == newItem.textEn && oldItem.meaningVi == newItem.meaningVi
        }

        override fun areContentsTheSame(oldItem: ReviewableWord, newItem: ReviewableWord): Boolean {
            return oldItem == newItem
        }
    }
}