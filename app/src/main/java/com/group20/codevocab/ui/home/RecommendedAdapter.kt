package com.group20.codevocab.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.repository.ModuleProgressInfo
import com.group20.codevocab.databinding.ItemRecommendedModuleBinding

class RecommendedAdapter(
    private var items: List<Pair<ModuleEntity, ModuleProgressInfo>> = emptyList(),
    private val onItemClick: (ModuleEntity) -> Unit
) : RecyclerView.Adapter<RecommendedAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRecommendedModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(module: ModuleEntity, progress: ModuleProgressInfo) {
            binding.tvRecommendedName.text = module.name
            binding.tvRecommendedType.text = module.moduleType.replaceFirstChar { it.uppercase() }
            
            // ✅ Hiển thị giống màn Flashcard: "X of Y"
            binding.tvProgressCount.text = "${progress.processedCount} of ${progress.totalCount}"
            
            // ✅ Thiết lập ProgressBar dựa trên số lượng từ thực tế
            binding.pbRecommended.max = if (progress.totalCount > 0) progress.totalCount else 100
            binding.pbRecommended.progress = progress.processedCount

            binding.root.setOnClickListener { onItemClick(module) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecommendedModuleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (module, progress) = items[position]
        holder.bind(module, progress)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Pair<ModuleEntity, ModuleProgressInfo>>) {
        items = newItems
        notifyDataSetChanged()
    }
}
