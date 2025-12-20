package com.group20.codevocab.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.FragmentGroupBinding
import com.group20.codevocab.databinding.ItemGroupBinding

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()

        binding.fab.setOnClickListener {
            showCreateGroupDialog()
        }
    }

    private fun setupRecyclerView() {
        val groupAdapter = GroupAdapter(createDummyData())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }
    }

    private fun showCreateGroupDialog() {
        val dialogFragment = CreateGroupDialogFragment()
        dialogFragment.show(childFragmentManager, "CreateGroupDialog")
    }

    private fun createDummyData(): List<Pair<String, Int>> {
        return listOf(
            "Design Enthusiasts" to 24,
            "Tech Innovators" to 18,
            "Fitness Buddies" to 32,
            "Book Club" to 15,
            "Photography Masters" to 41,
            "Startup Founders" to 9
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Simple adapter for the Group screen
class GroupAdapter(private val items: List<Pair<String, Int>>) : 
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(private val binding: ItemGroupBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<String, Int>) {
            binding.tvGroupName.text = item.first
            binding.tvMemberCount.text = "${item.second} members"
        }
    }
}