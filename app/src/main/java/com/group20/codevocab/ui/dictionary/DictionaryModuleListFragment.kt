package com.group20.codevocab.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.databinding.FragmentDictionaryModuleListBinding

class DictionaryModuleListFragment : Fragment() {

    private var _binding: FragmentDictionaryModuleListBinding? = null
    private val binding get() = _binding!!
    private var isSharedTab: Boolean = false

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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val dictionaryAdapter = DictionaryModuleAdapter(isSharedTab)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dictionaryAdapter
        }
        // Submit a dummy list for simulation
        dictionaryAdapter.submitList(createDummyData())
    }
    
    // Create dummy data based on the tab
    private fun createDummyData(): List<String> {
        return if(isSharedTab){
             listOf("IELTS Vocabulary", "Travel Phrases")
        }else{
            listOf("Business English", "IELTS Vocabulary", "Travel Phrases", "Food & Cooking", "Tech Terms")
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