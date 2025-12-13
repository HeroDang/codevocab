package com.group20.codevocab.ui.module

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.ModuleEntity

class ModuleListAdapter(
    private var modules: List<ModuleEntity>,
    private val layoutId: Int,
    private val onItemClick: (ModuleEntity) -> Unit
) : RecyclerView.Adapter<ModuleListAdapter.ModuleViewHolder>() {

    inner class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvModuleName)
        val tvDesc: TextView = itemView.findViewById(R.id.tvModuleDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return ModuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val module = modules[position]
        holder.tvName.text = module.name
        holder.tvDesc.text = module.description
        holder.itemView.setOnClickListener { onItemClick(module) }
    }

    override fun getItemCount() = modules.size

    fun updateData(newList: List<ModuleEntity>) {
        modules = newList
        notifyDataSetChanged()
    }
}
