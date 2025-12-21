package com.group20.codevocab.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.databinding.FragmentReviewWordBinding

class ReviewWordFragment : Fragment() {

    private var _binding: FragmentReviewWordBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewWordAdapter: ReviewWordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupModuleSpinner()
        setupButtons()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        // TODO: Handle menu item clicks
    }

    private fun setupRecyclerView() {
        reviewWordAdapter = ReviewWordAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewWordAdapter
        }
        reviewWordAdapter.submitList(getDummyWords())
    }

    private fun setupModuleSpinner() {
        val modules = arrayOf("User Authentication", "API Handling", "Database Management", "UI Components")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, modules)
        binding.autoCompleteModule.setAdapter(adapter)
    }

    private fun setupButtons() {
        binding.btnSaveWord.setOnClickListener {
            // TODO: Handle saving selected words
            findNavController().navigateUp() // Go back for now
        }
    }

    private fun getDummyWords(): List<ReviewableWord> {
        return listOf(
            ReviewableWord("Ephemeral", "Lasting for a very short time"),
            ReviewableWord("Serendipity", "The occurrence of events by chance in a happy or beneficial way"),
            ReviewableWord("Petrichor", "A pleasant smell that frequently accompanies the first rain after a long..."),
            ReviewableWord("Limerence", "The state of being infatuated with another person")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}