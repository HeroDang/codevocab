package com.group20.codevocab.ui.module

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.R
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.databinding.ActivityModuleDetailBinding
import com.group20.codevocab.viewmodel.ModuleDetailState
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory
import kotlinx.coroutines.launch

class ModuleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModuleDetailBinding
    private lateinit var viewModel: ModuleViewModel
    private lateinit var adapter: ModuleDetailAdapter
    private var currentModuleId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModuleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ModuleViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        currentModuleId = intent.getStringExtra("module_id")
        if (currentModuleId == null) {
            finish()
            return
        }

        setupUI()
        setupRefreshLayout()
        observeData()

        viewModel.loadModuleDetailFromServer(currentModuleId!!)
    }

    private fun setupUI() {
        adapter = ModuleDetailAdapter(emptyList()) { subModule ->
            val intent = Intent(this, WordListActivity::class.java)
            intent.putExtra("module_id", subModule.id)
            intent.putExtra("module_name", subModule.name)
            startActivity(intent)
        }
        binding.rvSubModules.layoutManager = LinearLayoutManager(this)
        binding.rvSubModules.adapter = adapter

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRefreshLayout() {
        // ✅ Cấu hình tính năng kéo để làm mới trạng thái (Swipe to Refresh)
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefreshLayout.setOnRefreshListener {
            currentModuleId?.let {
                viewModel.loadModuleDetailFromServer(it)
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.moduleDetailState.collect { state ->
                when (state) {
                    is ModuleDetailState.Loading -> {
                        if (!binding.swipeRefreshLayout.isRefreshing) {
                            // Hiển thị loading nếU không phải đang swipe refresh
                        }
                    }

                    is ModuleDetailState.Success -> {
                        val data = state.data
                        binding.tvModuleName.text = data.title
                        binding.tvModuleDescription.text = data.description
                        adapter.updateData(data.children)
                        
                        // ✅ Tắt vòng xoay làm mới khi có dữ liệu
                        binding.swipeRefreshLayout.isRefreshing = false
                    }

                    is ModuleDetailState.Error -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(
                            this@ModuleDetailActivity,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // ✅ Tự động cập nhật lại trạng thái mỗi khi người dùng quay lại màn hình này (ví dụ: sau khi học xong WordList)
        currentModuleId?.let {
            viewModel.loadModuleDetailFromServer(it)
        }
    }
}
