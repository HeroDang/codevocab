package com.group20.codevocab

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.group20.codevocab.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // --- MANUAL SETUP START ---

        // 1. Manually handle tab selection
        navView.setOnItemSelectedListener { item ->
            // This helper function correctly handles singleTop and popUpTo behavior
            // based on your bottom_nav_menu.xml attributes.
            NavigationUI.onNavDestinationSelected(item, navController)
            true
        }

        // 2. Manually handle UI updates (hiding nav bar and highlighting the correct tab)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val topLevelDestinations = setOf(
                R.id.homeFragment, R.id.learningFragment, R.id.dictionaryFragment, R.id.marketFragment, R.id.groupFragment
            )

            // Show or hide the bottom nav
            if (topLevelDestinations.contains(destination.id)) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }

            // Ensure the correct tab is highlighted in the bottom nav
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
}