package com.group20.codevocab.ui.dictionary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.databinding.FragmentDictionaryModuleListBinding
import com.group20.codevocab.model.ModuleItem
import com.group20.codevocab.ui.module.WordListActivity
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory
import com.group20.codevocab.viewmodel.ModulesState
import kotlinx.coroutines.launch

class DictionaryModuleListFragment : Fragment() {

    private var _binding: FragmentDictionaryModuleListBinding? = null
    private val binding get() = _binding!!
    private var isSharedTab: Boolean = false

    private lateinit var viewModel: ModuleViewModel
    private lateinit var adapter: DictionaryModuleAdapter

    private var localModules: List<ModuleItem> = emptyList()
    private var serverModules: List<ModuleItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isSharedTab = it.getBoolean(ARG_IS_SHARED_TAB, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryModuleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize ViewModel
        val factory = ModuleViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        // 2. Setup RecyclerView (đã cập nhật callback)
        setupRecyclerView()

        // 3. Observe Data (Local & Remote)
        observeViewModel()

        // 4. Lắng nghe kết quả từ Rename & Create Dialog
        setupFragmentResultListeners()

        // 5. Trigger Loads
        if (isSharedTab) {
            viewModel.loadSharedWithMeModules()
        } else {
            // Load Local first
            viewModel.loadModules()
            // Load Server
            viewModel.loadMyModulesFromServer()
        }

        // 6. Lắng nghe kết quả từ Accept Dialog
        setFragmentResultListener(AcceptModuleDialogFragment.REQUEST_KEY) { _, bundle ->
            val shouldViewModule = bundle.getBoolean(AcceptModuleDialogFragment.BUNDLE_KEY_VIEW_MODULE, false)
            val moduleId = bundle.getString(AcceptModuleDialogFragment.BUNDLE_KEY_MODULE_ID)
            val moduleName = bundle.getString(AcceptModuleDialogFragment.BUNDLE_KEY_MODULE_NAME)

            if (shouldViewModule && !moduleId.isNullOrEmpty()) {
                val intent = Intent(context, WordListActivity::class.java)
                intent.putExtra("module_id", moduleId)
                intent.putExtra("module_name", moduleName)
                intent.putExtra("is_local", false) // Shared module is remote
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        // Cập nhật Adapter với callback xử lý Rename và Accept
        adapter = DictionaryModuleAdapter(
            isSharedTab = isSharedTab,
            onRenameClick = { moduleItem ->
                showRenameDialog(moduleItem)
            },
            onAcceptClick = { moduleItem ->
                // Gọi API để accept module
                viewModel.acceptShareModule(
                    module = moduleItem,
                    onSuccess = {
                        // Hiển thị dialog thông báo thành công
                        // Truyền thêm moduleId để dialog có thể trả về khi người dùng nhấn "View Module"
                        val dialog = AcceptModuleDialogFragment.newInstance(moduleItem.name, moduleItem.id)
                        dialog.show(parentFragmentManager, AcceptModuleDialogFragment.TAG)
                        
                        // Thông báo toast
                        Toast.makeText(context, "Accepted share module successfully", Toast.LENGTH_SHORT).show()
                    },
                    onError = { message ->
                        Toast.makeText(context, "Error accepting: $message", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@DictionaryModuleListFragment.adapter
        }
    }

    private fun showRenameDialog(moduleItem: ModuleItem) {
        val dialog = RenameModuleDialogFragment.newInstance(moduleItem.name)

        // Lưu ID module đang sửa vào arguments của Fragment hiện tại tạm thời 
        // hoặc lưu vào một biến class level để dùng khi nhận kết quả
        currentEditingModuleId = moduleItem.id
        
        dialog.show(parentFragmentManager, "RenameModuleDialog")
    }

    // Biến tạm để lưu ID của module đang được sửa
    private var currentEditingModuleId: String? = null

    private fun setupFragmentResultListeners() {
        // Rename Listener
        setFragmentResultListener(RenameModuleDialogFragment.REQUEST_KEY) { _, bundle ->
            val newName = bundle.getString(RenameModuleDialogFragment.BUNDLE_KEY_NEW_NAME)
            val moduleId = currentEditingModuleId

            if (!newName.isNullOrEmpty() && moduleId != null) {
                // Find current item
                val currentItem = (localModules + serverModules).find { it.id == moduleId }
                
                if (currentItem != null) {
                     val updatedItem = currentItem.copy(name = newName)
                     viewModel.updateModule(updatedItem)
                }

                // Reset ID tạm
                currentEditingModuleId = null
            }
        }

        // Create Module Listener
        setFragmentResultListener(CreateModuleDialogFragment.REQUEST_KEY) { _, bundle ->
            val moduleName = bundle.getString(CreateModuleDialogFragment.BUNDLE_KEY_NAME)
            if (!moduleName.isNullOrEmpty()) {
                viewModel.createModuleLocal(moduleName)
            }
        }
    }

    private fun observeViewModel() {
        // 1. Quan sát Local Database
        viewModel.modules.observe(viewLifecycleOwner) { entities ->
            if (!isSharedTab) {
                lifecycleScope.launch {
                    val safeEntities = entities ?: emptyList()
                    localModules = safeEntities.map { entity ->
                        val wordCount = viewModel.getWordCount(entity.id)
                        ModuleItem(
                            id = entity.id,
                            name = entity.name,
                            description = entity.description,
                            isPublic = entity.isPublic,
                            isLocal = true,
                            wordCount = wordCount
                        )
                    }
                    mergeAndSubmitList()
                }
            }
        }

        // 2. Quan sát Server State
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ModulesState.Loading -> { }
                    is ModulesState.Success -> {
                        if (isSharedTab) {
                            adapter.submitList(state.items)
                        } else {
                            serverModules = state.items
                            mergeAndSubmitList()
                        }
                    }
                    is ModulesState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun mergeAndSubmitList() {
        // Gộp 2 danh sách lại, ưu tiên loại bỏ trùng lặp nếu cần (ví dụ theo ID)
        val combinedList = (localModules + serverModules).distinctBy { it.id }
        adapter.submitList(combinedList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IS_SHARED_TAB = "is_shared_tab"

        fun newInstance(isSharedTab: Boolean) = DictionaryModuleListFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_IS_SHARED_TAB, isSharedTab)
            }
        }
    }
}