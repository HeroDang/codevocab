package com.group20.codevocab.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group20.codevocab.databinding.FragmentGroupTabBinding
import com.group20.codevocab.databinding.ItemGroupMemberBinding
import com.group20.codevocab.databinding.ItemGroupPostBinding

class GroupTabFragment : Fragment() {

    private var _binding: FragmentGroupTabBinding? = null
    private val binding get() = _binding!!

    private var isMembersTab: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isMembersTab = it.getBoolean(ARG_IS_MEMBERS_TAB)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        if (isMembersTab) {
            binding.recyclerView.adapter = MembersAdapter(createDummyMembers())
        } else {
            binding.recyclerView.adapter = PostsAdapter(createDummyPosts())
        }
    }

    // Dummy data generation - replace with your actual data
    private fun createDummyMembers(): List<String> {
        return listOf("Sarah Johnson", "Michael Chen", "Emma Rodriguez", "David Kim", "Lisa Anderson", "James Wilson")
    }

    private fun createDummyPosts(): List<String> {
        return listOf("Advanced JavaScript Concepts", "Medical Terminology Basics", "Spanish Verbs - Present Tense", "World History Timeline")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IS_MEMBERS_TAB = "is_members_tab"

        fun newInstance(isMembersTab: Boolean) = GroupTabFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_IS_MEMBERS_TAB, isMembersTab)
            }
        }
    }
}

// Adapters
class MembersAdapter(private val members: List<String>) : RecyclerView.Adapter<MembersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(ItemGroupMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(members[position], position == 0)
    override fun getItemCount() = members.size

    class ViewHolder(private val binding: ItemGroupMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String, isOwner: Boolean) {
            binding.tvMemberName.text = name
            binding.tvMemberRole.text = if (isOwner) "Owner" else "Member"
        }
    }
}

class PostsAdapter(private val posts: List<String>) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(ItemGroupPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(posts[position])
    override fun getItemCount() = posts.size

    class ViewHolder(private val binding: ItemGroupPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.tvPostModuleName.text = title
        }
    }
}