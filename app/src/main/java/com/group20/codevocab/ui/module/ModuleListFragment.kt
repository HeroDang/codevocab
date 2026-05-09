package com.group20.codevocab.ui.module

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.R
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.remote.dto.ModuleWithParentIdDto
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.data.repository.SpeakingPracticeRepository
import com.group20.codevocab.databinding.FragmentModuleListBinding
import com.group20.codevocab.model.ModuleItem
import com.group20.codevocab.ui.pronunciation.PronunciationActivity
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory
import com.group20.codevocab.viewmodel.ModuleWithParentIdState
import com.group20.codevocab.viewmodel.ModulesState
import com.group20.codevocab.viewmodel.SpeakingPracticeViewModel
import com.group20.codevocab.viewmodel.SpeakingPracticeViewModelFactory
import kotlinx.coroutines.launch

class ModuleListFragment : Fragment() {
    private var _binding: FragmentModuleListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ModuleViewModel

    private lateinit var speakingViewModel: SpeakingPracticeViewModel
    private lateinit var adapter: ModuleListAdapter
    private var currentModules: List<ModuleItem> = emptyList()

    // Thêm biến member để quản lý dialog
    private var selectionDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ModuleViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        val authRepository = AuthRepository(ApiClient.api)
        val speakingRepository = SpeakingPracticeRepository()
        val speakingFactory =
            SpeakingPracticeViewModelFactory(speakingRepository, authRepository)
        speakingViewModel = ViewModelProvider(this, speakingFactory)[SpeakingPracticeViewModel::class.java]

        // Setup Menu
        binding.btnMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // Setup RecyclerView
        adapter = ModuleListAdapter(emptyList(), R.layout.item_module_list) { module ->
            val intent = Intent(requireContext(), ModuleDetailActivity::class.java)
            intent.putExtra("module_id", module.id)
            startActivity(intent)
        }
        binding.rvModules.layoutManager = LinearLayoutManager(requireContext())
        binding.rvModules.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ModulesState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ModulesState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        currentModules = state.items
                        adapter.updateData(state.items)
                    }

                    is ModulesState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        viewModel.loadModulesFromServer()
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.module_list_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_speaking_practice -> {
                    showModuleSelectionDialog()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showModuleSelectionDialog1() {
        if (currentModules.isEmpty()) {
            Toast.makeText(requireContext(), "No modules available", Toast.LENGTH_SHORT).show()
            return
        }

        val moduleNames = currentModules.map { it.name }.toTypedArray()
        var selectedModuleIndex = 0

        AlertDialog.Builder(requireContext())
            .setTitle("Pick a module for speaking practice")
            .setSingleChoiceItems(moduleNames, 0) { _, which ->
                selectedModuleIndex = which
            }
            .setPositiveButton("Practice") { _, _ ->
                val selectedModule = currentModules[selectedModuleIndex]
                val intent = Intent(requireContext(), SpeakingActivity::class.java).apply {
                    putExtra("module_id", selectedModule.id)
                    putExtra("module_name", selectedModule.name)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showModuleSelectionDialog() {
        // 1. Gọi API lấy danh sách module với parent id
        speakingViewModel.getModulesWithParentId()

        // 2. Quan sát trạng thái moduleState
        viewLifecycleOwner.lifecycleScope.launch {
            speakingViewModel.moduleState.collect { state ->
                when (state) {
                    is ModuleWithParentIdState.Loading -> {
                        // Có thể hiện một Loading Dialog nhỏ ở đây
                    }
                    is ModuleWithParentIdState.Success -> {
                        val modules = state.modules
                        if (modules.isNotEmpty()) {
                            renderSelectionDialog(modules)
                        } else {
                            Toast.makeText(requireContext(), "No modules available", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is ModuleWithParentIdState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun renderSelectionDialog(modules: List<ModuleWithParentIdDto>) {
        // Đóng dialog cũ nếu đang hiển thị để tránh chồng lấp (gây lag)
        selectionDialog?.dismiss()

        val moduleNames = modules.map { it.name }.toTypedArray()
        var selectedModuleIndex = 0

        selectionDialog = AlertDialog.Builder(requireContext())
            .setTitle("Pick a module for speaking practice")
            .setSingleChoiceItems(moduleNames, 0) { _, which ->
                selectedModuleIndex = which
            }
            .setPositiveButton("Practice") { dialog, _ ->
                val selectedModule = modules[selectedModuleIndex]
                val intent = Intent(requireContext(), SpeakingActivity::class.java).apply {
                    putExtra("module_id", selectedModule.id)
                    putExtra("module_name", selectedModule.name)
                }
                startActivity(intent)
                dialog.dismiss() // Đóng ngay sau khi start activity
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Thay vì null, hãy gọi dismiss() tường minh
            }
            .create()

        selectionDialog?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
