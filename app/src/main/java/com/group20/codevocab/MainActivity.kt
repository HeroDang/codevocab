package com.group20.codevocab

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.group20.codevocab.databinding.ActivityMainBinding
import com.group20.codevocab.utils.LocaleHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dùng ViewBinding để inflate layout XML
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy NavController từ NavHostFragment
//        val navController = findNavController(R.id.nav_host_fragment)

        // Lấy NavHostFragment từ FragmentManager
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Kết nối BottomNavigationView với NavController
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun attachBaseContext(newBase: Context?) {
        val lang = LocaleHelper.getLanguage(newBase!!)
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }
}
