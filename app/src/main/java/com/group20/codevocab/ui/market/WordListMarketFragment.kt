package com.group20.codevocab.ui.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.databinding.FragmentWordListMarketBinding
import com.group20.codevocab.model.ModuleItem
import com.group20.codevocab.ui.common.speaker.Speaker
import com.group20.codevocab.ui.common.speaker.SpeakerFactory
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory
import com.group20.codevocab.viewmodel.WordViewModel
import com.group20.codevocab.viewmodel.WordViewModelFactory
import kotlinx.coroutines.launch
import java.util.UUID

class WordListMarketFragment : Fragment() {

    private var _binding: FragmentWordListMarketBinding? = null
    private val binding get() = _binding!!

    private val marketViewModel: WordListMarketViewModel by viewModels()
    private lateinit var moduleViewModel: ModuleViewModel
    private lateinit var wordViewModel: WordViewModel

    private lateinit var adapter: MarketWordListAdapter
    private lateinit var speaker: Speaker
    private var moduleItem: ModuleItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            moduleItem = it.getParcelable("module")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordListMarketBinding.inflate(inflater, container, false)
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (moduleItem == null) {
            Toast.makeText(context, "Module data not found", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        speaker = SpeakerFactory.create(requireContext())

        val moduleFactory = ModuleViewModelFactory(requireContext())
        moduleViewModel = ViewModelProvider(this, moduleFactory)[ModuleViewModel::class.java]

        val wordFactory = WordViewModelFactory(requireContext())
        wordViewModel = ViewModelProvider(this, wordFactory)[WordViewModel::class.java]

        setupUI()
        observeData()

        marketViewModel.loadWords(moduleItem!!.id)
    }

    override fun onDestroyView() {
        speaker.shutdown()
        super.onDestroyView()
        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.visibility = View.VISIBLE
        _binding = null
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.toolbar.title = moduleItem?.name ?: "Module Details"
        binding.tvModuleName.text = moduleItem?.name ?: "Loading..."
        binding.tvAuthor.text = "by ${moduleItem?.ownerName ?: "Unknown"}"

        adapter = MarketWordListAdapter { word ->
            speaker.speak(word.textEn)
        }

        binding.recyclerViewWords.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewWords.adapter = adapter
        binding.recyclerViewWords.isNestedScrollingEnabled = false

        binding.btnCopy.setOnClickListener { copyModuleToLocal() }
    }

    private fun copyModuleToLocal() {
        moduleItem?.let { originalModule ->
            lifecycleScope.launch {
                val newLocalId = moduleViewModel.copyModuleToLocal(originalModule)
                
                val wordsToSave = adapter.currentList.map { wordItem ->
                    WordEntity(
                        id = UUID.randomUUID().toString(),
                        moduleId = newLocalId,
                        textEn = wordItem.textEn,
                        meaningVi = wordItem.meaningVi,
                        ipa = wordItem.ipa,
                        partOfSpeech = wordItem.partOfSpeech,
                        exampleSentence = wordItem.exampleSentence,
                        audioUrl = wordItem.audioUrl,
                        createdAt = System.currentTimeMillis().toString()
                    )
                }

                wordViewModel.saveWords(wordsToSave)
                Toast.makeText(context, "'${originalModule.name}' has been copied to your library!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                marketViewModel.state.collect { state ->
                    when (state) {
                        is WordListMarketState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.contentScrollView.visibility = View.GONE
                        }
                        is WordListMarketState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentScrollView.visibility = View.VISIBLE
                            adapter.submitList(state.items)
                            binding.tvWordCount.text = "${state.items.size} words"
                            if (moduleItem?.name == null) {
                                binding.toolbar.title = state.title
                                binding.tvModuleName.text = state.title
                            }
                        }
                        is WordListMarketState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}