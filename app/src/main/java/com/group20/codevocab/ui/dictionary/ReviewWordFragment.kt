package com.group20.codevocab.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.databinding.FragmentReviewWordBinding
import com.group20.codevocab.model.ReviewableWord
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory
import com.group20.codevocab.viewmodel.WordViewModel
import com.group20.codevocab.viewmodel.WordViewModelFactory
import java.util.UUID

class ReviewWordFragment : Fragment() {

    private var _binding: FragmentReviewWordBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewWordAdapter: ReviewWordAdapter
    private lateinit var moduleViewModel: ModuleViewModel
    private lateinit var wordViewModel: WordViewModel
    private var localModules: List<ModuleEntity> = emptyList()
    private var selectedModuleId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewWordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ModuleViewModel
        val moduleFactory = ModuleViewModelFactory(requireContext())
        moduleViewModel = ViewModelProvider(this, moduleFactory)[ModuleViewModel::class.java]

        // Initialize WordViewModel
        val wordFactory = WordViewModelFactory(requireContext())
        wordViewModel = ViewModelProvider(this, wordFactory)[WordViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupModuleSpinner()
        setupButtons()
        
        // Load local modules
        moduleViewModel.loadModules()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        // Initialize adapter with click listener callback
        reviewWordAdapter = ReviewWordAdapter { word ->
            // Handle edit word navigation
            // You might want to pass data to the edit fragment here
            findNavController().navigate(R.id.action_reviewWordFragment_to_editWordFragment)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewWordAdapter
        }

        // Lấy JSON text từ arguments
        arguments?.getString("ocr_text")?.let { jsonString ->
            if (jsonString.isBlank()) {
                reviewWordAdapter.submitList(emptyList())
                return@let
            }

            try {
                val gson = Gson()
                val type = object : TypeToken<List<ReviewableWord>>() {}.type
                val words = gson.fromJson<List<ReviewableWord>>(jsonString, type)

                // Kiểm tra null safety cho list và các item bên trong
                if (words != null) {
                    // Filter out null items and duplicates to prevent crashes in DiffUtil
                    val distinctWords = words.filterNotNull().distinctBy { it.textEn to it.meaningVi }
                    reviewWordAdapter.submitList(distinctWords)
                } else {
                    reviewWordAdapter.submitList(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Sử dụng context an toàn để tránh crash nếu fragment bị detached
                context?.let { ctx ->
                    Toast.makeText(ctx, "Error parsing OCR data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                reviewWordAdapter.submitList(emptyList())
            }
        } ?: run {
            reviewWordAdapter.submitList(emptyList()) // No data passed
        }
    }

    private fun setupModuleSpinner() {
        moduleViewModel.modules.observe(viewLifecycleOwner) { modules ->
            // Check context safe to avoid crash if fragment detached
            val context = context ?: return@observe
            
            localModules = modules
            val moduleNames = modules.map { it.name }
            val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, moduleNames)
            binding.autoCompleteModule.setAdapter(adapter)

            if (moduleNames.isNotEmpty()) {
                binding.autoCompleteModule.setOnItemClickListener { _, _, position, _ ->
                    // Capture selected module ID for future save usage
                    if (position < localModules.size) {
                        selectedModuleId = localModules[position].id
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnSaveWord.setOnClickListener {
            if (selectedModuleId == null) {
                Toast.makeText(context, "Please select a module", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val wordsToSave = reviewWordAdapter.currentList.filter { it.isChecked }.map { item ->
                WordEntity(
                    id = UUID.randomUUID().toString(),
                    moduleId = selectedModuleId!!,
                    textEn = item.textEn ?: "",
                    meaningVi = item.meaningVi,
                    partOfSpeech = item.partOfSpeech,
                    ipa = item.ipa,
                    exampleSentence = item.exampleSentence,
                    audioUrl = null,
                    createdAt = System.currentTimeMillis().toString()
                )
            }

            if (wordsToSave.isNotEmpty()) {
                wordViewModel.saveWords(wordsToSave)
                Toast.makeText(context, "Saved ${wordsToSave.size} words", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, "No words selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}