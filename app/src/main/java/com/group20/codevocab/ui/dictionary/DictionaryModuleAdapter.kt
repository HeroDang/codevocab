package com.group20.codevocab.ui.dictionary

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.ItemModuleDetailDicBinding
import com.group20.codevocab.ui.module.WordListActivity

class DictionaryModuleAdapter(private val isSharedTab: Boolean)
    : ListAdapter<String, DictionaryModuleAdapter.ModuleViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemModuleDetailDicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ModuleViewHolder(private val binding: ItemModuleDetailDicBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            // Set click listener to navigate to WordListActivity
            itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, WordListActivity::class.java)
                // TODO: Pass actual module ID
                intent.putExtra("module_id", -1) 
                context.startActivity(intent)
            }
        }

        fun bind(moduleName: String) {
            binding.tvModuleName.text = moduleName

            // Handle UI variations based on the tab
            if (isSharedTab) {
                binding.tvSharedBy.visibility = View.VISIBLE
                binding.llSharedActions.visibility = View.VISIBLE
            } else {
                binding.tvSharedBy.visibility = View.GONE
                binding.llSharedActions.visibility = View.GONE
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }
}