package com.group20.codevocab.ui.module

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.databinding.ActivityModuleDetailBinding
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory

class ModuleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModuleDetailBinding
    private lateinit var viewModel: ModuleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModuleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ModuleViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        val moduleId = intent.getIntExtra("module_id", -1)
        if (moduleId == -1) {
            finish()
            return
        }

        binding.rvSubModules.layoutManager = LinearLayoutManager(this)

        // back button -> close this activity and return to MainActivity (previous screen)
        binding.btnBack.setOnClickListener {
            finish()
        }
        // Load module chính
        viewModel.getModuleById(moduleId).observe(this, Observer { module ->
            binding.tvModuleName.text = module.name
            binding.tvModuleDescription.text = module.description
        })

        // Load danh sách module con
        viewModel.getSubModules(moduleId).observe(this, Observer { list ->
            binding.rvSubModules.adapter = ModuleDetailAdapter(
                list
            ) { subModule ->
                // Khi click vào item module con -> Chuyển sang màn hình WordList
                val intent = Intent(this, WordListActivity::class.java)
                intent.putExtra("module_id", subModule.id)
                startActivity(intent)
            }
        })
    }
}
