package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.SupportMapFragment
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentMapBinding
import com.muei.travelmate.databinding.FragmentUserBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.searchIcon.setOnClickListener {
            Log.d("MapFragment", "Pulsado boton buscar en mapa")
            findNavController().navigate(R.id.nav_route)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapView = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapView.getMapAsync {map ->
            googleMap = map

            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.setOnMapClickListener { latLng ->
                // Handle map click events
                // Example: Add a marker at the clicked position
                googleMap.addMarker(MarkerOptions().position(latLng).title("Clicked Position"))
            }

            // Move camera to a default location (for example, city center)
            val defaultLocation = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM_LEVEL))
        }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEFAULT_LATITUDE = -33.8688
        private const val DEFAULT_LONGITUDE = 151.2093
        private const val DEFAULT_ZOOM_LEVEL = 12f
    }
}