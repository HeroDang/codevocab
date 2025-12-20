package com.group20.codevocab.ui.module

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.group20.codevocab.R
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.repository.ModuleRepository
import com.group20.codevocab.databinding.FragmentModuleListBinding
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory
import com.group20.codevocab.viewmodel.ModulesState
import kotlinx.coroutines.launch

class ModuleListFragment : Fragment() {
    private var _binding: FragmentModuleListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ModuleViewModel
    private lateinit var adapter: ModuleListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ModuleViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        // 3️⃣ Setup RecyclerView
        adapter = ModuleListAdapter(emptyList(), R.layout.item_module_list) { module ->
            Toast.makeText(requireContext(), "Clicked: ${module.name}", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), ModuleDetailActivity::class.java)
            intent.putExtra("module_id", module.id)
            startActivity(intent)
        }
        binding.rvModules.layoutManager = LinearLayoutManager(requireContext())
        binding.rvModules.adapter = adapter

        // 4️⃣ Quan sát dữ liệu
        //viewModel.modules.observe(viewLifecycleOwner) { list ->
        //    adapter.updateData(list)
        //}

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is ModulesState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ModulesState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.updateData(state.items)
                    }

                    is ModulesState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


        // 5️⃣ Gọi load data
        viewModel.loadModulesFromServer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
