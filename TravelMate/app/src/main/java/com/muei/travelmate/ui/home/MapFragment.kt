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
import com.google.maps.android.PolyUtil
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placeType: String
    private lateinit var placeName: String
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private lateinit var bundleType: String
    private var latlngArrayString: String = ""
    private var placesResult: JSONArray? = null
    private var points: String = ""
    private var routePoints: JSONArray = JSONArray()
    private var showingPoints = true
    private var routeInfo = true

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

        placeName = requireArguments().getString("placeName") ?: ""
        placeType = requireArguments().getString("placeType") ?: ""
        lat = requireArguments().getDouble("lat")
        lng = requireArguments().getDouble("lng")
        bundleType = requireArguments().getString("bundleType") ?: ""
        latlngArrayString = requireArguments().getString("latlngArrayString") ?: ""
        routeInfo = requireArguments().getBoolean("routeInfo")?: true
        Log.d("CustomRouteMap", "Route: $latlngArrayString")

        binding.titleText.text = "$placeName: $placeType"

        val mapView = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapView.getMapAsync(this)

        if (lat != 0.0 && lng != 0.0 && bundleType != "customRoute") {
            if (placesResult == null) {
                FetchPlacesTask().execute()
            }
        }

        if (!routeInfo) {
            binding.infoLayout.visibility = View.INVISIBLE
        } else
            binding.infoLayout.visibility = View.VISIBLE

        // toggle between points and route
        binding.startButton.setOnClickListener {
            // use toggle boolean
            if ((placesResult != null || latlngArrayString.isNotEmpty()) && googleMap != null){
                if (showingPoints) {
                    googleMap?.clear()
                    addMarkersFromAPIResults(routePoints)
                    drawRoute(points)
                    binding.startButton.text = "Ruta"
                    showingPoints = false

                } else {
                    googleMap?.clear()
                    addMarkersFromAPIResults(placesResult!!)
                    binding.startButton.text = "Puntos"
                    showingPoints = true
                }
            }
        }

        if (!isGPSenabled()) {
            showGPSdisabledAlert()
        }

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

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.isMyLocationEnabled = true
        Log.d("MapReady", "ZoomControl true, myLocation true")

        if (bundleType == "customRoute") {
            placesResult = JSONArray(latlngArrayString)
            Log.d("CustomRouteHandle", "CustomRoute detected")
            if (placesResult != null) {
                addMarkersFromAPIResults(placesResult!!)
                handleRoutePoints(placesResult!!)

                // Obtener la ubicación del primer lugar
                val firstPlaceLocation = placesResult!!.getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                val location = LatLng(
                    firstPlaceLocation.getDouble("lat"),
                    firstPlaceLocation.getDouble("lng")
                )

                // Mover la cámara a la ubicación del primer lugar
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))

                Log.d("CustomRouteHandle", "CustomRoute retrieved")
            }

        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if (location != null){
                Log.d("MapReady", "Location retrieved successfully")
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))
            }else{
                Log.d("MapReady", "Unable to get current location")
                Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isGPSenabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showGPSdisabledAlert() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    companion object {
        private const val DEFAULT_ZOOM_LEVEL = 12f
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private inner class FetchPlacesTask : AsyncTask<Void, Void, JSONArray>() {

        override fun doInBackground(vararg params: Void?): JSONArray? {
            val urlString =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location=$lat,$lng" +
                        "&radius=5000" +
                        "&type=$placeType" +
                        "&key=${getString(R.string.API_KEY)}"
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
                placesResult = results
                addMarkersFromAPIResults(results)
                // Centrar el mapa en las coordenadas lat y lng
                val location = LatLng(lat, lng)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
                // Generar ruta entre los puntos importantes
                val numWaypoints = minOf(results.length(), 5)
                if (numWaypoints >= 2) {
                    handleRoutePoints(results)
                } else {
                    Log.e("MapFragment", "No hay suficientes puntos intermedios disponibles para calcular la ruta.")
                }
            }
        }
    }

    private fun addMarkersFromAPIResults(results: JSONArray) {
        if(googleMap != null){
            for (i in 0 until results.length()) {
                val result = results.getJSONObject(i)
                val location = result.getJSONObject("geometry").getJSONObject("location")
                val lat = location.getDouble("lat")
                val lng = location.getDouble("lng")
                val placeName = result.getString("name")
                val placeLatLng = LatLng(lat, lng)
                Log.d("LatLngMarker", placeLatLng.toString())
                googleMap?.addMarker(MarkerOptions().position(placeLatLng).title(placeName))
            }
        }else{
            Log.d("MapFragment", "GoogleMap Is not Initialized")
        }
    }

    private inner class FetchRouteTask(
        private val origin: LatLng,
        private val destination: LatLng,
        private val waypoints: List<LatLng>
    ) : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String? {
            val waypointsStr = waypoints.joinToString("|") { "via:${it.latitude},${it.longitude}" }
            val urlString = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&waypoints=$waypointsStr" +
                    "&key=${getString(R.string.API_KEY)}"
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
            return stringBuilder.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                val jsonObject = JSONObject(result)
                val routes = jsonObject.getJSONArray("routes")
                if (routes.length() > 0) {
                    points = routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")
                    val duration = routes.getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0)
                        .getJSONObject("duration")
                        .getString("text")
                    binding.timeText.text = duration
                    val distance = routes.getJSONObject(0)
                        .getJSONArray("legs")
                        .getJSONObject(0)
                        .getJSONObject("distance")
                        .getString("text")
                    binding.distanceText.text = distance
                }
            }
        }
    }

    private fun drawRoute(encodedPoints: String) {
        if(googleMap != null){
            val path = PolyUtil.decode(encodedPoints)
            googleMap?.addPolyline(PolylineOptions().addAll(path).color(R.color.purple_700).width(40f))
        }else{
            Log.d("MapFragment", "GoogleMap Is not Initialized")
        }
    }

    private fun handleRoutePoints(results: JSONArray) {
        val numWaypoints = minOf(results.length(), 5)
        for (i in 0 until numWaypoints) {
            routePoints.put(results.getJSONObject(i))
        }

        // Obtener el origen
        val origin = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
        val originLatLng = LatLng(origin.getDouble("lat"), origin.getDouble("lng"))

        // Obtener los waypoints
        val waypoints = mutableListOf<LatLng>()
        for (i in 1 until numWaypoints - 1) {
            val waypoint = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location")
            val waypointLatLng = LatLng(waypoint.getDouble("lat"), waypoint.getDouble("lng"))
            waypoints.add(waypointLatLng)
        }

        // Obtener el destino
        val destination = results.getJSONObject(numWaypoints - 1).getJSONObject("geometry").getJSONObject("location")
        val destinationLatLng = LatLng(destination.getDouble("lat"), destination.getDouble("lng"))

        // Ejecutar la tarea de búsqueda de ruta
        FetchRouteTask(originLatLng, destinationLatLng, waypoints).execute()
    }
}

