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
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.provider.Settings
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Properties


class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placeType: String
    private lateinit var placeName: String
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.searchIcon.setOnClickListener {
            Log.d("MapFragment", "Pulsado boton buscar en mapa")
            findNavController().navigate(R.id.nav_route)
        }
        Log.d("MapCreated","Obteniendo valores")
        placeName = requireArguments().getString("placeName") ?: throw IllegalArgumentException("Argument 'placeName' not found in bundle")
        placeType = requireArguments().getString("placeType") ?: throw IllegalArgumentException("Argument 'placeType' not found in bundle")
        lat = requireArguments().getDouble("lat")
        lng = requireArguments().getDouble("lng")

        binding.titleText.text = "$placeName: $placeType"

        val mapView = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapView.getMapAsync(this)

        if(lat != 0.0 && lng != 0.0)
            FetchPlacesTask().execute()

        if(!isGPSenabled())
            showGPSdisabledAlert()

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
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))
            }else{
                Log.d("MapReady", "Unable to get current location")
                Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isGPSenabled(): Boolean{
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGPSdisabledAlert(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
    companion object {
        private const val DEFAULT_ZOOM_LEVEL = 12f
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private inner class FetchPlacesTask : AsyncTask<Void, Void, JSONArray>() {

        override fun doInBackground(vararg params: Void?): JSONArray? {
            val urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=$lat,$lng" +
                    "&radius=5000" +
                    "&type=$placeType" +
                    "&key=AIzaSyAG6Mpf5GzKQbeDXOQQ2NUvPlARMobE_SQ"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            val inputStream = connection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()
            connection.disconnect()
            return JSONObject(stringBuilder.toString()).getJSONArray("results")
        }

        override fun onPostExecute(results: JSONArray?) {
            super.onPostExecute(results)
            if (results != null) {
                addMarkersFromAPIResults(results)
                // Centrar el mapa en las coordenadas lat y lng
                val location = LatLng(lat, lng)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
            }
        }
    }

    private fun addMarkersFromAPIResults(results: JSONArray) {
        for (i in 0 until results.length()) {
            val result = results.getJSONObject(i)
            val location = result.getJSONObject("geometry").getJSONObject("location")
            val lat = location.getDouble("lat")
            val lng = location.getDouble("lng")
            val placeName = result.getString("name")
            val placeLatLng = LatLng(lat, lng)
            Log.d("LatLngMarker", placeLatLng.toString())
            googleMap.addMarker(MarkerOptions().position(placeLatLng).title(placeName))
        }
    }
}