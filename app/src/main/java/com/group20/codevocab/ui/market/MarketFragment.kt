package com.group20.codevocab.ui.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.FragmentMarketBinding
import com.group20.codevocab.databinding.ItemModuleDetailMarketBinding

class MarketFragment : Fragment() {

    private var _binding: FragmentMarketBinding? = null
    private val binding get() = _binding!!

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
        
        val marketAdapter = MarketAdapter(createDummyData())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = marketAdapter
        }
    }

    private fun createDummyData(): List<Pair<String, String>> {
        return listOf(
            "Essential Business Vocabulary" to "Sarah Johnson",
            "Spanish Travel Phrases" to "Carlos Martinez",
            "Medical Terminology Basics" to "Dr. Emily Chen",
            "Programming Fundamentals" to "Alex Thompson",
            "French Cuisine Terms" to "Marie Dubois",
            "Japanese Hiragana Guide" to "Yuki Tanaka",
            "SAT Vocabulary Mastery" to "Michael Brown",
            "German Grammar Essentials" to "Hans Mueller"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// A simple adapter for the market screen
class MarketAdapter(private val items: List<Pair<String, String>>) : 
    RecyclerView.Adapter<MarketAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemModuleDetailMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(private val binding: ItemModuleDetailMarketBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<String, String>) {
            binding.tvModuleName.text = item.first
            binding.tvAuthor.text = item.second
        }
    }
}