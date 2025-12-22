package com.group20.codevocab.ui.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MarketAdapter(private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<MarketAdapter.ViewHolder>() {

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
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemModuleDetailMarketBinding, private val onClick: (String) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModuleItem) {
            binding.tvModuleName.text = item.name
            binding.tvAuthor.text = "Sarah Johnson"
            binding.tvWordCount.text = "150 words"
            binding.tvModuleDescription?.text = item.description
            binding.root.setOnClickListener { onClick(item.id) }
        }
    }
}