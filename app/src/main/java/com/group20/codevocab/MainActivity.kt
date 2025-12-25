package com.group20.codevocab

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.group20.codevocab.databinding.ActivityMainBinding
import com.group20.codevocab.utils.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var prefManager: PreferenceManager
    private var sessionStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferenceManager(this)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // --- MANUAL SETUP START ---

        // 1. Manually handle tab selection
        navView.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)
            true
        }

        // 2. Manually handle UI updates
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val topLevelDestinations = setOf(
                R.id.homeFragment, R.id.learningFragment, R.id.dictionaryFragment, R.id.marketFragment, R.id.groupFragment
            )

            if (topLevelDestinations.contains(destination.id)) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }

            val menu = navView.menu
            for (i in 0 until menu.size()) {
                val item = menu.getItem(i)
                if (item.itemId == destination.id) {
                    item.isChecked = true
                    break
                }
            }
        }
        // --- MANUAL SETUP END ---
    }

    override fun onResume() {
        super.onResume()
        // ✅ Ghi lại thời điểm người dùng bắt đầu phiên làm việc
        sessionStartTime = System.currentTimeMillis()
    }

    override fun onPause() {
        super.onPause()
        // ✅ Tính toán thời gian đã sử dụng và lưu vào PreferenceManager
        if (sessionStartTime > 0) {
            val sessionDurationMillis = System.currentTimeMillis() - sessionStartTime
            val sessionDurationMinutes = sessionDurationMillis / (1000 * 60)
            
            // Chỉ lưu nếu thời gian sử dụng ít nhất là 1 phút để tránh nhiễu dữ liệu
            // Hoặc bạn có thể lưu chính xác mili giây nếu muốn độ chính xác cao hơn
            if (sessionDurationMinutes >= 0) {
                prefManager.addStudyTime(sessionDurationMinutes)
            }
            sessionStartTime = 0
        }
    }
}