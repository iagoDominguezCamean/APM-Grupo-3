package com.muei.travelmate.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.muei.travelmate.R
import com.muei.travelmate.databinding.ActivityMainBinding
import com.muei.travelmate.ui.home.MapActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNav
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Log.d("MainActivity", "Home pulsado")
                    navController.navigate(R.id.nav_home)
                    true
                }

                R.id.nav_fav -> {
                    Log.d("MainActivity", "Fav pulsado")
                    navController.navigate(R.id.nav_fav)
                    true
                }

                R.id.nav_map -> {
                    Log.d("MainActivity", "Map pulsado")
                    val intent = Intent(this@MainActivity, MapActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_user -> {
                    Log.d("MainActivity", "User pulsado")
                    navController.navigate(R.id.nav_user)
                    true
                }

                else -> false
            }
        }
    }
}
