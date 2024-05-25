package com.muei.travelmate.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.muei.travelmate.R
import com.muei.travelmate.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var placeName: String? = null
    private var placeId: String? = ""
    private var latitude: Double? = null
    private var longitude: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar los permisos al iniciar la aplicación
        if (!checkPermission()) {
            requestLocationPermissions()
        }

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
                    val bundle = Bundle().apply {
                        putString("placeName", placeName ?: "Map")
                        putString("placeId", placeId ?: " ")
                        putDouble("lat", latitude ?: 0.0)
                        putDouble("lng", longitude ?: 0.0)
                        putString("placeType", "default")
                    }
                    Log.d("ShowBundleMain", bundle.toString())
                    navController.navigate(R.id.nav_map, bundle)
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

    private fun checkPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
