package com.group20.codevocab.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.group20.codevocab.R
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.databinding.FragmentHomeBinding
import com.group20.codevocab.ui.theme.SettingsActivity
import com.group20.codevocab.viewmodel.BaseViewModelFactory
import com.group20.codevocab.viewmodel.UserViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel

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

        setupViewModel()
        observeUserData()
        setupQuickAccess()
        
        userViewModel.fetchCurrentUser()
    }

    private fun setupViewModel() {
        val apiService = ApiClient.getApiService()
        val repository = AuthRepository(apiService)
        
        val factory = object : BaseViewModelFactory<UserViewModel>() {
            override fun createViewModel(): UserViewModel {
                return UserViewModel(repository)
            }
        }
        
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
    }

    private fun observeUserData() {
        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.name
                
                if (!it.avatarUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(it.avatarUrl)
                        .placeholder(R.drawable.ic_user)
                        .circleCrop()
                        .into(binding.ivAvatar)
                } else {
                    binding.ivAvatar.setImageResource(R.drawable.ic_user)
                }
            }
        }
    }

    private fun setupQuickAccess() {
        val navController = findNavController()
        
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .setPopUpTo(R.id.homeFragment, false, true)
            .build()

        binding.btnLearning.setOnClickListener {
            navController.navigate(R.id.learningFragment, null, navOptions)
        }

        binding.btnDictionary.setOnClickListener {
            navController.navigate(R.id.dictionaryFragment, null, navOptions)
        }

        binding.btnMarket.setOnClickListener {
            navController.navigate(R.id.marketFragment, null, navOptions)
        }

        binding.btnGroup.setOnClickListener {
            navController.navigate(R.id.groupFragment, null, navOptions)
        }

        binding.btnStats.setOnClickListener {
            navController.navigate(R.id.statsFragment, null, navOptions)
        }

        binding.btnSettings.setOnClickListener {
            // ✅ Sử dụng Intent để mở Activity thay vì NavController để tránh crash
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
