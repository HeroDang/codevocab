package com.group20.codevocab.ui.module

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModuleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ModuleViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        adapter = ModuleDetailAdapter(emptyList()) { subModule ->
            val intent = Intent(this, WordListActivity::class.java)
            intent.putExtra("module_id", subModule.id)
            startActivity(intent)
        }
        binding.rvSubModules.adapter = adapter

        val moduleId = intent.getStringExtra("module_id")
        if (moduleId == null) {
            finish()
            return
        }

        binding.rvSubModules.layoutManager = LinearLayoutManager(this)

        // back button -> close this activity and return to MainActivity (previous screen)
        binding.btnBack.setOnClickListener {
            finish()
        }
//         Load module chính
//        viewModel.getModuleById(moduleId).observe(this, Observer { module ->
//            binding.tvModuleName.text = module.name
//            binding.tvModuleDescription.text = module.description
//        })

//         Load danh sách module con
//        viewModel.getSubModules(moduleId).observe(this, Observer { list ->
//            binding.rvSubModules.adapter = ModuleDetailAdapter(
//                list
//            ) { subModule ->
//                // Khi click vào item module con -> Chuyển sang màn hình WordList
//                val intent = Intent(this, WordListActivity::class.java)
//                intent.putExtra("module_id", subModule.id)
//                startActivity(intent)
//            }
//        })

        lifecycleScope.launch {
            viewModel.moduleDetailState.collect { state ->
                when (state) {
                    is ModuleDetailState.Loading -> {
                        // optional: show loading
                    }

                    is ModuleDetailState.Success -> {
                        val data = state.data
                        binding.tvModuleName.text = data.title
                        binding.tvModuleDescription.text = data.description

                        adapter.updateData(data.children)
                    }

                    is ModuleDetailState.Error -> {
                        Toast.makeText(
                            this@ModuleDetailActivity,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        viewModel.loadModuleDetailFromServer(moduleId)
    }
}
