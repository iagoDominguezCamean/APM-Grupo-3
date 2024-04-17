package com.muei.travelmate.ui.home

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentMapBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MapSearchFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private lateinit var mMap: GoogleMap
    private lateinit var placeType: String
    private lateinit var placeName: String
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.searchIcon.setOnClickListener {
            Log.d("com.muei.travelmate.ui.home.MapSearchFragment", "Pulsado boton buscar en mapa")
            findNavController().navigate(R.id.nav_route)
        }

        placeName = requireArguments().getString("placeName") ?: throw IllegalArgumentException("Argument 'placeName' not found in bundle")
        placeType = requireArguments().getString("placeType") ?: throw IllegalArgumentException("Argument 'placeType' not found in bundle")
        lat = requireArguments().getDouble("lat")
        lng = requireArguments().getDouble("lng")

        binding.titleText.text = "$placeName: $placeType"
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
            FetchPlacesTask().execute()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
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
            mMap.addMarker(MarkerOptions().position(placeLatLng).title(placeName))
        }
    }
}
