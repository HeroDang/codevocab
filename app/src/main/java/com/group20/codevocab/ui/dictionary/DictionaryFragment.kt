package com.group20.codevocab.ui.dictionary

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayoutMediator
import com.group20.codevocab.R
import com.group20.codevocab.databinding.FragmentDictionaryBinding
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory
import com.group20.codevocab.viewmodel.SortType

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private val tabTitles = arrayOf("My Modules", "Shared to Me")
    private lateinit var viewModel: ModuleViewModel

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

        // Initialize Shared ViewModel scoped to this Fragment's lifecycle
        val factory = ModuleViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        setupViewPager()
        setupTabs()
        setupFab()
        setupFilterButtons()
        observeSortType()
    }

    private fun setupViewPager() {
        val pagerAdapter = DictionaryPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
    }

    private fun setupTabs() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Lấy sort type hiện tại của tab được chọn và cập nhật UI
                val isSharedTab = position == 1
                val currentSortType = viewModel.getSortType(isSharedTab)
                updateFilterUI(currentSortType)
            }
        })
    }

    private fun setupFilterButtons() {
        // Initial setup for default tab (index 0)
        val initialIsShared = binding.viewPager.currentItem == 1
        updateFilterUI(viewModel.getSortType(initialIsShared))

        binding.btnSortName.setOnClickListener {
            val isShared = binding.viewPager.currentItem == 1
            viewModel.setSortType(isShared, SortType.NAME)
        }

        binding.btnSortDate.setOnClickListener {
            val isShared = binding.viewPager.currentItem == 1
            viewModel.setSortType(isShared, SortType.DATE)
        }

        binding.btnSortWordCount.setOnClickListener {
            val isShared = binding.viewPager.currentItem == 1
            viewModel.setSortType(isShared, SortType.WORD_COUNT)
        }
    }

    private fun observeSortType() {
        viewModel.myModulesSortType.observe(viewLifecycleOwner) {
            if (binding.viewPager.currentItem == 0) {
                updateFilterUI(it)
            }
        }
        viewModel.sharedModulesSortType.observe(viewLifecycleOwner) {
            if (binding.viewPager.currentItem == 1) {
                updateFilterUI(it)
            }
        }
    }

    private fun updateFilterUI(selectedSort: SortType) {
        // Helper to update button style
        fun updateButton(button: MaterialButton, type: SortType) {
            val isSelected = selectedSort == type
            if (isSelected) {
                button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EDE7F6")) // Light purple bg
                button.setTextColor(Color.parseColor("#5E35B1")) // Deep purple text
                button.iconTint = ColorStateList.valueOf(Color.parseColor("#5E35B1"))
                button.strokeWidth = 0
            } else {
                button.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_700))
                button.iconTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_700))
                button.strokeWidth = 0
            }
        }

        updateButton(binding.btnSortName, SortType.NAME)
        updateButton(binding.btnSortDate, SortType.DATE)
        updateButton(binding.btnSortWordCount, SortType.WORD_COUNT)
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
                    showCreateModuleDialog()
                    true
                }
                R.id.action_add_word -> {
                    findNavController().navigate(R.id.action_dictionaryFragment_to_addWordFragment)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showCreateModuleDialog() {
        val dialogFragment = CreateModuleDialogFragment()
        dialogFragment.show(childFragmentManager, "CreateModuleDialog")
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