package com.group20.codevocab.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.group20.codevocab.R
import com.group20.codevocab.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private val tabTitles = arrayOf("My Modules", "Shared to Me")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupTabs()
        setupFab()
    }

    private fun setupViewPager() {
        val pagerAdapter = DictionaryPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
    }

    private fun setupTabs() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupFab() {
        binding.fab.setOnClickListener { anchorView ->
            showPopupMenu(anchorView)
        }
    }

    private fun showPopupMenu(anchorView: View) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(R.menu.dictionary_add_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_create_module -> {
                    Toast.makeText(context, "Create module clicked", Toast.LENGTH_SHORT).show()
                    // TODO: Handle Create module action
                    true
                }
                R.id.action_add_word -> {
                    Toast.makeText(context, "Add Word clicked", Toast.LENGTH_SHORT).show()
                    // TODO: Handle Add Word action
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class DictionaryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = tabTitles.size

        override fun createFragment(position: Int): Fragment {
            return DictionaryModuleListFragment.newInstance(position == 1)
        }
    }
}