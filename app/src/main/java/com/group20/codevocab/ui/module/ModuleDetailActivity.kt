package com.group20.codevocab.ui.module

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.group20.codevocab.R
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.repository.ModuleRepository
import com.group20.codevocab.databinding.ActivityModuleDetailBinding
import com.group20.codevocab.ui.flashcard.FlashcardActivity
import com.group20.codevocab.viewmodel.ModuleViewModel
import com.group20.codevocab.viewmodel.ModuleViewModelFactory

class ModuleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModuleDetailBinding
    private lateinit var viewModel: ModuleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModuleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1️⃣ Khởi tạo database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "vocab.db"
        ).createFromAsset("databases/vocab.db").build()

        // 2️⃣ Repository + ViewModel
        val repository = ModuleRepository(db.moduleDao())
        val factory = ModuleViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ModuleViewModel::class.java]

        val moduleId = intent.getIntExtra("module_id", -1)
        if (moduleId == -1) {
            finish()
            return
        }

        binding.rvSubModules.layoutManager = LinearLayoutManager(this)

        // Load module chính
        viewModel.getModuleById(moduleId).observe(this, Observer { module ->
            binding.tvModuleName.text = module.name
            binding.tvModuleDescription.text = module.description
        })

        // Load danh sách module con
        viewModel.getSubModules(moduleId).observe(this, Observer { list ->
            binding.rvSubModules.adapter = ModuleDetailAdapter(
                list,
                onStartLearningClick = { subModule ->
                    Toast.makeText(applicationContext, "onStartLearningClick: ${subModule.name}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, FlashcardActivity::class.java)
                    intent.putExtra("module_id", subModule.id)
                    startActivity(intent)
                },
                onQuizClick = { subModule ->
                    Toast.makeText(
                        applicationContext,
                        "onQuizClick: ${subModule.name}",
                        Toast.LENGTH_SHORT
                    ).show()
//                    val intent = Intent(this, QuizActivity::class.java)
//                    intent.putExtra("module_id", subModule.id)
//                    startActivity(intent)
                }
            )
        })
    }
}
