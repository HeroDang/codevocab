package com.group20.codevocab.ui.dictionary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ItemModuleDetailDicBinding
import com.group20.codevocab.ui.module.WordListActivity

class DictionaryModuleAdapter(private val isSharedTab: Boolean)
    : ListAdapter<String, DictionaryModuleAdapter.ModuleViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding = ItemModuleDetailDicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding, isSharedTab)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ModuleViewHolder(private val binding: ItemModuleDetailDicBinding, private val isSharedTab: Boolean)
        : RecyclerView.ViewHolder(binding.root) {

        private var currentModuleName: String? = null

        init {
            // Set click listener for the whole item
            itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, WordListActivity::class.java)
                intent.putExtra("module_id", -1) // TODO: Pass actual module ID
                context.startActivity(intent)
            }

            // Set click listener for the menu icon
            binding.ivShare.setOnClickListener { view ->
                currentModuleName?.let { showPopupMenu(view, it) }
            }

            // Set click listener for the Accept button
            binding.btnAccept.setOnClickListener { view ->
                currentModuleName?.let { moduleName ->
                    val context = view.context
                    if (context is AppCompatActivity) {
                        AcceptModuleDialogFragment.newInstance(moduleName)
                            .show(context.supportFragmentManager, AcceptModuleDialogFragment.TAG)
                    }
                }
            }
        }

        fun bind(moduleName: String) {
            currentModuleName = moduleName
            binding.tvModuleName.text = moduleName

            // Handle UI variations based on the tab
            if (isSharedTab) {
                binding.tvSharedBy.visibility = View.VISIBLE
                binding.llSharedActions.visibility = View.VISIBLE
                binding.ivShare.visibility = View.GONE // Hide menu in Shared Tab
            } else {
                binding.tvSharedBy.visibility = View.GONE
                binding.llSharedActions.visibility = View.GONE
                binding.ivShare.visibility = View.VISIBLE // Show menu in My Modules Tab
            }
        }

        private fun showPopupMenu(anchor: View, moduleName: String) {
            val context = anchor.context
            val popup = PopupMenu(context, anchor)
            popup.menuInflater.inflate(R.menu.dictionary_module_menu, popup.menu)

            // Make the "Delete" menu item red
            val deleteItem = popup.menu.findItem(R.id.action_delete)
            val spannableString = SpannableString(deleteItem.title)
            spannableString.setSpan(ForegroundColorSpan(Color.RED), 0, spannableString.length, 0)
            deleteItem.title = spannableString

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_rename -> {
                        if (context is AppCompatActivity) {
                            val dialog = RenameModuleDialogFragment.newInstance(moduleName)
                            dialog.show(context.supportFragmentManager, "RenameModuleDialog")
                        }
                        true
                    }
                    R.id.action_share -> {
                        if (context is AppCompatActivity) {
                            val dialog = ShareModuleDialogFragment.newInstance(moduleName)
                            dialog.show(context.supportFragmentManager, "ShareModuleDialog")
                        }
                        true
                    }
                    R.id.action_delete -> {
                        // TODO: Handle Delete
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }
}