package com.group20.codevocab.ui.market

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.R
import com.group20.codevocab.databinding.FragmentMarketBinding
import com.group20.codevocab.databinding.ItemModuleDetailMarketBinding
import com.group20.codevocab.model.ModuleItem
import kotlinx.coroutines.launch

class MarketFragment : Fragment() {

    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!

    private val marketViewModel: MarketViewModel by viewModels()
    private lateinit var marketAdapter: MarketAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                marketViewModel.state.collect { state ->
                    when (state) {
                        is MarketModulesState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            marketAdapter.submitList(state.items)
                        }
                        is MarketModulesState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is MarketModulesState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        marketViewModel.loadMarketModules()
    }

    private fun setupRecyclerView() {
        marketAdapter = MarketAdapter { moduleId ->
            val bundle = bundleOf("moduleId" to moduleId)
            findNavController().navigate(R.id.action_marketFragment_to_wordListMarketFragment, bundle)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = marketAdapter
        }
    }

    private fun setupSearch() {
        // Open search
        binding.ivSearch.setOnClickListener {
            binding.tvTitle.visibility = View.GONE
            binding.etSearch.visibility = View.VISIBLE
            binding.ivSearch.visibility = View.GONE
            binding.ivCloseSearch.visibility = View.VISIBLE
            
            binding.etSearch.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        // Close search
        binding.ivCloseSearch.setOnClickListener {
            binding.etSearch.setText("") // Clear filter
            binding.etSearch.visibility = View.GONE
            binding.ivCloseSearch.visibility = View.GONE
            binding.tvTitle.visibility = View.VISIBLE
            binding.ivSearch.visibility = View.VISIBLE
            
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                marketAdapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle keyboard search action
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MarketAdapter(private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<MarketAdapter.ViewHolder>() {

    private var originalItems: List<ModuleItem> = emptyList()
    private var items: List<ModuleItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemModuleDetailMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<ModuleItem>) {
        originalItems = newItems
        items = newItems
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        items = if (query.isEmpty()) {
            originalItems
        } else {
            originalItems.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemModuleDetailMarketBinding, private val onClick: (String) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModuleItem) {
            binding.tvModuleName.text = item.name
            binding.tvAuthor.text = item.ownerName ?: "Anonymous"
            binding.tvWordCount.text = "${item.wordCount ?: 0} words"
            binding.tvModuleDescription?.text = item.description
            binding.root.setOnClickListener { onClick(item.id) }
        }
    }
}