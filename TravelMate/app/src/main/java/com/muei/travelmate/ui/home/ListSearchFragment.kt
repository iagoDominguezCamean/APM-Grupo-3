package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.Feature
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
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

        // Llamar a la función para obtener los resultados de la API
        searchTerm?.let { searchMapTilerAPI(it) }

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

    private fun fetchPlaceNamesFromJson(jsonString: String): List<String> {
        val placeNames = mutableListOf<String>()
        try {
            val jsonObject = JSONObject(jsonString)
            val featuresJsonArray = jsonObject.getJSONArray("features")

            for (i in 0 until featuresJsonArray.length()) {
                val featureObject = featuresJsonArray.getJSONObject(i)
                val placeName = featureObject.getString("place_name")
                placeNames.add(placeName)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return placeNames
    }

    private fun updateTextViewsWithPlaceNames(placeNames: List<String>) {
        val textViewsToUpdate = arrayOf(binding.title1, binding.title2, binding.title3 /* agregar todos los TextViews que necesites */)

        placeNames.forEachIndexed { index, placeName ->
            if (index < textViewsToUpdate.size) {
                activity?.runOnUiThread {
                    textViewsToUpdate[index].text = placeName
                }
            }
        }
    }

    private fun searchMapTilerAPI(searchTerm: String) {
        val apiUrl = "https://api.maptiler.com/geocoding/$searchTerm.json?key=gR5hsD5wp8w5wAwyFMPK"
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

                val placeNames = fetchPlaceNamesFromJson(response.toString())
                updateTextViewsWithPlaceNames(placeNames)

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    binding.title1.text = "Error de conexión"
                }
            }
        }.start()
    }

}
