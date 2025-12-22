package com.group20.codevocab.ui.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.group20.codevocab.R
import com.group20.codevocab.databinding.FragmentWordListMarketBinding
import com.group20.codevocab.ui.common.speaker.Speaker
import com.group20.codevocab.ui.common.speaker.SpeakerFactory
import kotlinx.coroutines.launch

class WordListMarketFragment : Fragment() {

    private var _binding: FragmentWordListMarketBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WordListMarketViewModel by viewModels()
    private lateinit var adapter: WordMarketAdapter
    private lateinit var speaker: Speaker
    private var moduleId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moduleId = arguments?.getString("moduleId")
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

        if (moduleId == null) {
            Toast.makeText(context, "Module ID not found", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        speaker = SpeakerFactory.create(requireContext())

        setupUI()
        observeData()

        viewModel.loadWords(moduleId!!)
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

        adapter = WordMarketAdapter(emptyList()) { word ->
            speaker.speak(word.textEn)
        }

        binding.recyclerViewWords.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewWords.adapter = adapter
        binding.recyclerViewWords.isNestedScrollingEnabled = false
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is WordListMarketState.Loading -> {
                            // You can show a progress bar here if you want
                        }
                        is WordListMarketState.Success -> {
                            adapter.updateData(state.items)
                            binding.toolbar.title = state.title
                            binding.tvModuleName.text = state.title
                            binding.tvWordCount.text = "${state.items.size} words"
                        }
                        is WordListMarketState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}