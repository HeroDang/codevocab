package com.group20.codevocab.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.databinding.FragmentDictionaryModuleListBinding
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

        // 2. Setup RecyclerView
        setupRecyclerView()

        // 3. Observe StateFlow from ViewModel
        observeViewModel()

        // 4. Load Data
        viewModel.loadUserModulesFromServer("9150dfe1-0758-4716-9d0e-99fc0fbe3a63")
    }

    private fun setupRecyclerView() {
        adapter = DictionaryModuleAdapter(isSharedTab)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@DictionaryModuleListFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ModulesState.Loading -> {
                        // binding.progressBar.visibility = View.VISIBLE 
                        // Assuming you added a ProgressBar to the layout, otherwise comment out
                    }
                    is ModulesState.Success -> {
                        // binding.progressBar.visibility = View.GONE
                        // Filter or use data based on tab if needed, 
                        // for now we just show all modules or implement filtering logic here
                        // In a real app, you might have separate API calls or filter the list
                        if (isSharedTab) {
                            // Demo filtering: In reality, backend handles this or we filter by property
                             adapter.submitList(emptyList()) // Placeholder for Shared Modules
                        } else {
                            adapter.submitList(state.items)
                        }
                    }
                    is ModulesState.Error -> {
                        // binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
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