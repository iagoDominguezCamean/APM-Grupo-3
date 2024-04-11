package com.muei.travelmate.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.muei.travelmate.R
import com.muei.travelmate.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigation
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Log.d("MapActivity", "Home pulsado")
                    navController.navigate(R.id.nav_home)
                    finish()

                    true
                }

                R.id.nav_fav -> {
                    Log.d("MapActivity", "Fav pulsado")
                    navController.navigate(R.id.nav_fav)
                    finish()

                    true
                }

                R.id.nav_map -> {
                    Log.d("MapActivity", "Ya estás en el mapa")
                    true
                }

                R.id.nav_user -> {
                    Log.d("MapActivity", "User pulsado")
                    navController.navigate(R.id.nav_user)
                    finish()

                    true
                }
                else -> false
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        binding.searchIcon.setOnClickListener {
            Log.d("MapActivity", "Icono de busqueda pulsado")
            navController.navigate(R.id.nav_route)
        }

        binding.arrowIcon.setOnClickListener{
            Log.d("MapActivity", "Flecha para volver pulsada")
            finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}