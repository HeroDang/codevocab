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
    // Thêm callback onRenameClick để xử lý sự kiện đổi tên
    private val onRenameClick: (ModuleItem) -> Unit
) : ListAdapter<ModuleItem, DictionaryModuleAdapter.ModuleViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val binding =
            ItemModuleDetailDicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Truyền callback vào ViewHolder
        return ModuleViewHolder(binding, isSharedTab, onRenameClick)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ModuleViewHolder(
        private val binding: ItemModuleDetailDicBinding,
        private val isSharedTab: Boolean,
        private val onRenameClick: (ModuleItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentModule: ModuleItem? = null

        init {
            // Sự kiện click vào item để mở danh sách từ
            itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, WordListActivity::class.java)
                currentModule?.let { module ->
                    intent.putExtra("module_id", module.id)
                    intent.putExtra("module_name", module.name)
                    intent.putExtra("is_local", module.isLocal)
                }
                context.startActivity(intent)
            }

            // Sự kiện mở menu (3 chấm)
            binding.ivShareContainer.setOnClickListener { view ->
                currentModule?.let { showPopupMenu(view, it) }
            }

            // Sự kiện nút Accept (cho Shared Tab)
            binding.btnAccept.setOnClickListener { view ->
                currentModule?.let { module ->
                    val context = view.context
                    if (context is AppCompatActivity) {
                        AcceptModuleDialogFragment.newInstance(module.name)
                            .show(context.supportFragmentManager, AcceptModuleDialogFragment.TAG)
                    }
                }
            }
        }

        fun bind(module: ModuleItem) {
            currentModule = module
            binding.tvModuleName.text = module.name

            if (isSharedTab) {
                binding.tvSharedBy.visibility = View.VISIBLE
                binding.llSharedActions.visibility = View.VISIBLE
                binding.ivShareContainer.visibility = View.GONE
            } else {
                binding.tvSharedBy.visibility = View.GONE
                binding.llSharedActions.visibility = View.GONE
                binding.ivShareContainer.visibility = View.VISIBLE
            }
        }

        private fun showPopupMenu(anchor: View, module: ModuleItem) {
            val context = anchor.context
            val popup = PopupMenu(context, anchor)
            popup.menuInflater.inflate(R.menu.dictionary_module_menu, popup.menu)

            // Tô đỏ nút Delete
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
                        // Gọi callback để Fragment xử lý (hiển thị Dialog)
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
                        // TODO: Handle Delete here or via callback
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
