package com.muei.travelmate.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.muei.travelmate.R
import com.muei.travelmate.databinding.ActivityMainBinding

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
                    if(!checkPermission()){
                        Log.d("Permisos de geolocalización", "Solicitando")
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                    }else{
                        Log.d("Permisos de geolocalización", "Concedidos")
                        navController.navigate(R.id.nav_map)
                    }

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
    private fun checkPermission(): Boolean{
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
