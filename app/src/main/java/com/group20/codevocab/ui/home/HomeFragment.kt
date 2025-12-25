package com.group20.codevocab.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.group20.codevocab.MainActivity
import com.group20.codevocab.R
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.data.repository.ModuleRepository
import com.group20.codevocab.databinding.FragmentHomeBinding
import com.group20.codevocab.ui.profile.EditProfileActivity
import com.group20.codevocab.ui.theme.SettingsActivity
import com.group20.codevocab.viewmodel.BaseViewModelFactory
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.UserViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var moduleViewModel: ModuleViewModel
    private lateinit var recommendedAdapter: RecommendedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModels()
        setupRecyclerView()
        observeData()
        setupQuickAccess()

        userViewModel.fetchCurrentUser()
    }

    override fun onResume() {
        super.onResume()
        moduleViewModel.loadInProgressModules()
    }

    private fun setupViewModels() {
        val apiService = ApiClient.api
        val authRepository = AuthRepository(apiService)
        val db = AppDatabase.getDatabase(requireContext())
        val moduleRepository = ModuleRepository(apiService, db.moduleDao(), db.flashcardDao(), db.wordDao())

        userViewModel = ViewModelProvider(this, object : BaseViewModelFactory<UserViewModel>() {
            override fun createViewModel() = UserViewModel(authRepository)
        })[UserViewModel::class.java]

        moduleViewModel = ViewModelProvider(this, object : BaseViewModelFactory<ModuleViewModel>() {
            override fun createViewModel() = ModuleViewModel(moduleRepository)
        })[ModuleViewModel::class.java]
    }

    private fun setupRecyclerView() {
        recommendedAdapter = RecommendedAdapter { module ->
            // FIX: Navigate using a Bundle to avoid Safe Args build issues
            val bundle = Bundle().apply {
                putString("moduleId", module.id)
            }
            findNavController().navigate(R.id.action_homeFragment_to_wordListMarketFragment, bundle)
        }
        binding.rvRecommended.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recommendedAdapter
        }
    }

    private fun observeData() {
        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.name
                if (!it.avatarUrl.isNullOrEmpty()) {
                    Glide.with(this).load(it.avatarUrl).circleCrop().into(binding.ivAvatar)
                }
            }
        }

        moduleViewModel.inProgressModules.observe(viewLifecycleOwner) { inProgress ->
            if (inProgress.isNullOrEmpty()) {
                binding.layoutRecommendedHeader.visibility = View.GONE
                binding.rvRecommended.visibility = View.GONE
            } else {
                binding.layoutRecommendedHeader.visibility = View.VISIBLE
                binding.rvRecommended.visibility = View.VISIBLE
                recommendedAdapter.updateData(inProgress)
            }
        }
    }

    private fun setupQuickAccess() {
        val bottomNav = (requireActivity() as? MainActivity)?.findViewById<BottomNavigationView>(R.id.nav_view)

        binding.btnLearning.setOnClickListener { bottomNav?.selectedItemId = R.id.learningFragment }
        binding.btnDictionary.setOnClickListener { bottomNav?.selectedItemId = R.id.dictionaryFragment }
        binding.btnMarket.setOnClickListener { bottomNav?.selectedItemId = R.id.marketFragment }
        binding.btnGroup.setOnClickListener { bottomNav?.selectedItemId = R.id.groupFragment }

        binding.btnStats.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_statsFragment) }

        binding.btnSettings.setOnClickListener { startActivity(Intent(requireContext(), SettingsActivity::class.java)) }
        binding.btnSettingsTop.setOnClickListener { startActivity(Intent(requireContext(), SettingsActivity::class.java)) }
        binding.ivAvatar.setOnClickListener { startActivity(Intent(requireContext(), EditProfileActivity::class.java)) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}