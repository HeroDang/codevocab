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
import com.group20.codevocab.viewmodel.SortType
import kotlinx.coroutines.launch

class DictionaryModuleListFragment : Fragment() {

    private var _binding: FragmentDictionaryModuleListBinding? = null
    private val binding get() = _binding!!
    private var isSharedTab: Boolean = false

    private lateinit var viewModel: ModuleViewModel
    private lateinit var sharedViewModel: ModuleViewModel // Shared UI state VM
    private lateinit var adapter: DictionaryModuleAdapter

    private var localModules: List<ModuleItem> = emptyList()
    private var serverModules: List<ModuleItem> = emptyList()
    private var currentSortType: SortType = SortType.NAME

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

        // 1. Initialize ViewModels
        val factory = ModuleViewModelFactory(requireContext())
        // Fragment-scoped VM for data to avoid conflict between tabs
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]
        // ParentFragment-scoped VM for shared Filter/Sort state (DictionaryFragment)
        try {
            sharedViewModel = ViewModelProvider(requireParentFragment(), factory)[ModuleViewModel::class.java]
        } catch (e: Exception) {
            // Fallback if not attached to parent fragment properly (though it should be via ViewPager2)
             sharedViewModel = ViewModelProvider(requireActivity(), factory)[ModuleViewModel::class.java]
        }

        // 2. Setup RecyclerView
        setupRecyclerView()

        // 3. Observe Data (Local & Remote) & Sort Type
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
        adapter = DictionaryModuleAdapter(
            isSharedTab = isSharedTab,
            onRenameClick = { moduleItem ->
                showRenameDialog(moduleItem)
            },
            onAcceptClick = { moduleItem ->
                viewModel.acceptShareModule(
                    module = moduleItem,
                    onSuccess = {
                        val dialog = AcceptModuleDialogFragment.newInstance(moduleItem.name, moduleItem.id)
                        dialog.show(parentFragmentManager, AcceptModuleDialogFragment.TAG)
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
        currentEditingModuleId = moduleItem.id
        dialog.show(parentFragmentManager, "RenameModuleDialog")
    }

    private var currentEditingModuleId: String? = null

    private fun setupFragmentResultListeners() {
        setFragmentResultListener(RenameModuleDialogFragment.REQUEST_KEY) { _, bundle ->
            val newName = bundle.getString(RenameModuleDialogFragment.BUNDLE_KEY_NEW_NAME)
            val moduleId = currentEditingModuleId

            if (!newName.isNullOrEmpty() && moduleId != null) {
                val currentItem = (localModules + serverModules).find { it.id == moduleId }
                if (currentItem != null) {
                     val updatedItem = currentItem.copy(name = newName)
                     viewModel.updateModule(updatedItem)
                }
                currentEditingModuleId = null
            }
        }

        setFragmentResultListener(CreateModuleDialogFragment.REQUEST_KEY) { _, bundle ->
            val moduleName = bundle.getString(CreateModuleDialogFragment.BUNDLE_KEY_NAME)
            if (!moduleName.isNullOrEmpty()) {
                viewModel.createModuleLocal(moduleName)
            }
        }
    }

    private fun observeViewModel() {
        // Observe Sort Type from Shared ViewModel
        if (isSharedTab) {
            sharedViewModel.sharedModulesSortType.observe(viewLifecycleOwner) { sortType ->
                currentSortType = sortType
                mergeAndSubmitList()
            }
        } else {
            sharedViewModel.myModulesSortType.observe(viewLifecycleOwner) { sortType ->
                currentSortType = sortType
                mergeAndSubmitList()
            }
        }

        // 1. Quan sát Local Database (My Modules tab only)
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
                            wordCount = wordCount,
                            createdAt = entity.createdAt
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
                    is ModulesState.Loading -> {
//                        if (adapter.currentList.isEmpty()) {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
//                        }
                    }
                    is ModulesState.Success -> {
                        serverModules = state.items 
                        mergeAndSubmitList()
                    }
                    is ModulesState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun mergeAndSubmitList() {
        var list: List<ModuleItem>
        if (isSharedTab) {
            list = serverModules
        } else {
            // Gộp 2 danh sách lại, ưu tiên server nếu trùng ID (hoặc ngược lại tùy logic, ở đây distinctBy giữ phần tử đầu tiên)
            // localModules + serverModules -> Local ưu tiên
            list = (localModules + serverModules).distinctBy { it.id }
        }
        
        // Apply Sorting
        list = when (currentSortType) {
            SortType.NAME -> list.sortedBy { it.name.lowercase() }
            SortType.DATE -> list.sortedByDescending { it.createdAt ?: "" }
            SortType.WORD_COUNT -> list.sortedByDescending { it.wordCount ?: 0 }
        }

        adapter.submitList(list)

        // Data processed and sorted, show UI
        // Hide loading only if we have data OR if loading is finished (or failed)
        // Fixed: removed check for list.isNotEmpty() to ensure loading indicator persists until loading is done
        if (viewModel.state.value !is ModulesState.Loading) {
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
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