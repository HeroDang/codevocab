package com.group20.codevocab.ui.module

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.ModuleEntity

class ModuleDetailAdapter(
    private val modules: List<ModuleEntity>,
    private val onStartLearningClick: (ModuleEntity) -> Unit,
    private val onQuizClick: (ModuleEntity) -> Unit
) : RecyclerView.Adapter<ModuleDetailAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tvModuleName)
        val tvDesc = view.findViewById<TextView>(R.id.tvModuleDescription)
        val btnStart = view.findViewById<Button>(R.id.btnStartLearning)
        val btnQuiz = view.findViewById<Button>(R.id.btnQuiz)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val module = modules[position]
        holder.tvName.text = module.name
        holder.tvDesc.text = module.description

        holder.btnStart.setOnClickListener { onStartLearningClick(module) }
        holder.btnQuiz.setOnClickListener { onQuizClick(module) }
    }

    override fun getItemCount() = modules.size
}
