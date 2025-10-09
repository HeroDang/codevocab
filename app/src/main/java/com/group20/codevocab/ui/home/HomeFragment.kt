package com.group20.codevocab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.repository.ModuleRepository
import com.group20.codevocab.databinding.FragmentHomeBinding
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ModuleViewModel
    private lateinit var adapter: ModuleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1️⃣ Khởi tạo database
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "vocab.db"
        ).createFromAsset("databases/vocab.db").build()

        // 2️⃣ Repository + ViewModel
        val repository = ModuleRepository(db.moduleDao())
        val factory = ModuleViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        // 3️⃣ Setup RecyclerView
        adapter = ModuleAdapter(emptyList()) { module ->
            Toast.makeText(requireContext(), "Clicked: ${module.name}", Toast.LENGTH_SHORT).show()
        }
        binding.rvModules.layoutManager = LinearLayoutManager(requireContext())
        binding.rvModules.adapter = adapter

        // 4️⃣ Quan sát dữ liệu
        viewModel.modules.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }

        // 5️⃣ Gọi load data
        viewModel.loadModules()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
