package com.group20.codevocab.ui.module

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.model.SubModuleItem

class ModuleDetailAdapter(
    private var modules: List<SubModuleItem>,
    private val onItemClick: (SubModuleItem) -> Unit
) : RecyclerView.Adapter<ModuleDetailAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tvModuleName)
        val tvDesc = view.findViewById<TextView>(R.id.tvModuleDescription)
        val tvWordCount = view.findViewById<TextView>(R.id.tvWordCount)
        
        fun bind(module: SubModuleItem) {
            tvName.text = module.name
            tvDesc.text = module.description
            tvWordCount.text = "${module.wordCount ?: 0} words"
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
