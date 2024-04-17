package com.muei.travelmate.ui.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import android.location.Location

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

        val mapView = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapView.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap){
        Log.d("MapReady", "Entrando en map ready")
        googleMap = map

        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        Log.d("MapReady", "Tenemos permisos")
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.isMyLocationEnabled = true
        Log.d("MapReady", "ZoomControl true, myLocation true")
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if (location != null){
                Log.d("MapReady", "Location retrieved successfully")
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Position"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))
            }else{
                Log.d("MapReady", "Unable to get current location")
                Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        private const val DEFAULT_LATITUDE = -33.8688
        private const val DEFAULT_LONGITUDE = 151.2093
        private const val DEFAULT_ZOOM_LEVEL = 12f
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}