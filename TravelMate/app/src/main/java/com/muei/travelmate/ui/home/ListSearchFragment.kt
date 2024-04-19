package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentListSearchBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ListSearchFragment : Fragment() {

    private var _binding: FragmentListSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSearchBinding.inflate(inflater, container, false)
        val searchTerm = arguments?.getString("searchTerm")
        Log.d("SearchFragment", "Término de búsqueda: $searchTerm")
        binding.titleText.text = "Término de búsqueda: $searchTerm"

        // Inicialización de los TextViews de item{i}
        val itemTextViews = arrayOf(binding.item1, binding.item2, binding.item3 /* agregar todos los TextViews de item{i} que necesites */)

        // Oculta todos los TextViews de item{i}
        itemTextViews.forEach { it.visibility = View.INVISIBLE }

        // Llamar a la función para obtener los resultados de la API
        searchTerm?.let { searchGooglePlacesAPI(it) }

        binding.arrowIcon.setOnClickListener {
            Log.d("SearchFragment", "Flecha para volver pulsada")
            findNavController().navigate(R.id.nav_home)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun searchGooglePlacesAPI(query: String) {
        val apiKey = "AIzaSyAG6Mpf5GzKQbeDXOQQ2NUvPlARMobE_SQ" // Insert your Google Places API key here
        val apiUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$query&type=locality&key=$apiKey"

        Thread {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val places = fetchPlacesFromJson(response.toString())
                updateTextViewsWithPlaces(places)

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    binding.title1.text = "Error de conexión"
                }
            }
        }.start()
    }

    private fun fetchPlacesFromJson(jsonString: String): List<Place> {
        val places = mutableListOf<Place>()
        try {
            val jsonObject = JSONObject(jsonString)
            if (jsonObject.getString("status") == "OK") {
                val resultsArray = jsonObject.getJSONArray("results")
                for (i in 0 until resultsArray.length()) {
                    val resultObj = resultsArray.getJSONObject(i)
                    val formattedAddress = resultObj.getString("formatted_address")
                    val placeId = resultObj.getString("place_id")
                    places.add(Place(formattedAddress, placeId))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return places
    }

    private fun updateTextViewsWithPlaces(places: List<Place>) {
        val titleTextViews = arrayOf(binding.title1, binding.title2, binding.title3 /* agregar todos los TextViews de título que necesites */)
        val itemTextViews = arrayOf(binding.item1, binding.item2, binding.item3 /* agregar todos los TextViews de item{i} que necesites */)

        places.take(titleTextViews.size).forEachIndexed { index, place ->
            activity?.runOnUiThread {
                titleTextViews[index].text = place.formattedAddress
                itemTextViews[index].apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        val bundle = Bundle().apply {
                            putString("place_id", place.placeId)
                        }
                        findNavController().navigate(R.id.nav_search, bundle)
                    }
                }
            }
        }
    }
    data class Place(val formattedAddress: String, val placeId: String)

}
