package com.group20.codevocab.ui.dictionary

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
import com.group20.codevocab.model.ModuleItem
import com.group20.codevocab.ui.module.WordListActivity

class DictionaryModuleAdapter(
    private val isSharedTab: Boolean,
    // Callback cho sự kiện rename
    private val onRenameClick: (ModuleItem) -> Unit,
    // Callback cho sự kiện accept
    private val onAcceptClick: (ModuleItem) -> Unit = {}
) : ListAdapter<ModuleItem, DictionaryModuleAdapter.ModuleViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding =
            ItemModuleDetailDicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModuleViewHolder(binding, isSharedTab, onRenameClick, onAcceptClick)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ModuleViewHolder(
        private val binding: ItemModuleDetailDicBinding,
        private val isSharedTab: Boolean,
        private val onRenameClick: (ModuleItem) -> Unit,
        private val onAcceptClick: (ModuleItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentModule: ModuleItem? = null

        init {
            // Sự kiện click vào item
            itemView.setOnClickListener {
                val context = it.context
                currentModule?.let { module ->
                    // Nếu pending thì chặn action
                    if (isSharedTab && module.status == "pending") {
                        return@setOnClickListener
                    }
                    
                    val intent = Intent(context, WordListActivity::class.java)
                    intent.putExtra("module_id", module.id)
                    intent.putExtra("module_name", module.name)
                    intent.putExtra("is_local", module.isLocal)
                    // Thêm flag show_menu = true khi đi từ Dictionary
                    intent.putExtra("show_menu", true)
                    context.startActivity(intent)
                }
            }

            // Menu 3 chấm
            binding.ivShareContainer.setOnClickListener { view ->
                currentModule?.let { showPopupMenu(view, it) }
            }

            // Nút Accept (dành cho trạng thái đã accept - hiện tại chưa làm gì)
            binding.btnAccept.setOnClickListener { 
                // Placeholder
            }

            // Nút Pending Accept (dành cho trạng thái pending)
            binding.btnPendingAccept.setOnClickListener {
                 currentModule?.let { module ->
                    // Gọi callback thay vì tự xử lý context ép kiểu
                    onAcceptClick(module)
                }
            }
        }

        fun bind(module: ModuleItem) {
            currentModule = module
            binding.tvModuleName.text = module.name
            binding.tvWordCount.text = "${module.wordCount ?: 0} words"

            if (isSharedTab) {
                binding.tvSharedBy.visibility = View.VISIBLE
                binding.tvSharedBy.text = "By: ${module.ownerName ?: "Unknown"}"
                binding.llSharedActions.visibility = View.VISIBLE
                binding.ivShareContainer.visibility = View.GONE
                binding.tvPrivacyStatus.visibility = View.GONE

                when (module.status) {
                    "pending" -> {
                        binding.btnPending.visibility = View.VISIBLE
                        binding.btnPendingAccept.visibility = View.VISIBLE
                        binding.btnAccept.visibility = View.GONE
                    }
                    else -> { 
                        binding.btnPending.visibility = View.GONE
                        binding.btnPendingAccept.visibility = View.GONE
                        binding.btnAccept.visibility = View.VISIBLE
                    }
                }

            } else {
                binding.tvSharedBy.visibility = View.GONE
                binding.llSharedActions.visibility = View.GONE
                binding.ivShareContainer.visibility = View.VISIBLE
                
                binding.tvPrivacyStatus.visibility = View.VISIBLE
                if (module.isLocal) {
                    binding.tvPrivacyStatus.text = "Offline"
                    binding.tvPrivacyStatus.setBackgroundResource(R.drawable.bg_tag_private)
                    binding.tvPrivacyStatus.setTextColor(Color.parseColor("#616161")) // gray_700
                } else {
                    binding.tvPrivacyStatus.text = "Online"
                    binding.tvPrivacyStatus.setBackgroundResource(R.drawable.bg_tag_public)
                    binding.tvPrivacyStatus.setTextColor(Color.parseColor("#1565C0")) // blue_800
                }
            }
        }

        private fun showPopupMenu(anchor: View, module: ModuleItem) {
            val context = anchor.context
            val popup = PopupMenu(context, anchor)
            popup.menuInflater.inflate(R.menu.dictionary_module_menu, popup.menu)

            val deleteItem = popup.menu.findItem(R.id.action_delete)
            if (deleteItem != null) {
                val spannableString = SpannableString(deleteItem.title)
                spannableString.setSpan(
                    ForegroundColorSpan(Color.RED),
                    0,
                    spannableString.length,
                    0
                )
                deleteItem.title = spannableString
            }

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_rename -> {
                        onRenameClick(module)
                        true
                    }
                    R.id.action_share -> {
                        if (context is AppCompatActivity) {
                            val dialog = ShareModuleDialogFragment.newInstance(module.name)
                            dialog.show(context.supportFragmentManager, "ShareModuleDialog")
                        }
                        true
                    }
                    R.id.action_delete -> {
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ModuleItem>() {
        override fun areItemsTheSame(oldItem: ModuleItem, newItem: ModuleItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ModuleItem, newItem: ModuleItem) =
            oldItem == newItem
    }
}