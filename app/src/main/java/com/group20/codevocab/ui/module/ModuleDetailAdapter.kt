package com.group20.codevocab.ui.module

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.model.SubModuleItem

class ModuleDetailAdapter(
    private var modules: List<SubModuleItem>,
    private val onItemClick: (SubModuleItem) -> Unit
) : RecyclerView.Adapter<ModuleDetailAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tvModuleName)
        val tvDesc = view.findViewById<TextView>(R.id.tvModuleDescription)
        val tvWordCount = view.findViewById<TextView>(R.id.tvWordCount)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatusText)
        val ivStatus = view.findViewById<ImageView>(R.id.ivStatusIcon)
        
        fun bind(module: SubModuleItem) {
            tvName.text = module.name
            tvDesc.text = module.description
            tvWordCount.text = "${module.wordCount ?: 0} words"

            val total = module.wordCount ?: 0
            val learned = module.learnedCount

            when {
                learned == 0 -> {
                    // Not started
                    tvStatus.text = "Not started"
                    tvStatus.setTextColor(Color.parseColor("#777777"))
                    ivStatus.setImageResource(R.drawable.ic_eye) // Hoặc ic_circle nếu có
                    ivStatus.setColorFilter(Color.parseColor("#777777"))
                }
                learned >= total && total > 0 -> {
                    // Completed
                    tvStatus.text = "Completed"
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                    ivStatus.setImageResource(R.drawable.ic_check)
                    ivStatus.setColorFilter(Color.parseColor("#4CAF50"))
                }
                else -> {
                    // In progress
                    tvStatus.text = "In progress"
                    tvStatus.setTextColor(Color.parseColor("#FF9800"))
                    ivStatus.setImageResource(R.drawable.ic_time)
                    ivStatus.setColorFilter(Color.parseColor("#FF9800"))
                }
            }

            itemView.setOnClickListener { onItemClick(module) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount() = modules.size

    fun updateData(newItems: List<SubModuleItem>) {
        modules = newItems
        notifyDataSetChanged()
    }
}
