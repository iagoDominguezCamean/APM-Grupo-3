package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentSearchBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private var placeName: String? = null
    private var placeId: String? = ""
    private var latitude: Double? = null
    private var longitude: Double? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.arrowIcon.setOnClickListener {
            Log.d("SearchFragment", "Flecha para volver pulsada")
            findNavController().navigateUp() }

        binding.item1.setOnClickListener {
            Log.d("SearchFragment", "Ruta 1 pulsada")
            val bundle = Bundle().apply {
                putString("placeName", placeName)
                putString("placeId", placeId)
                latitude?.let { it1 -> putDouble("lat", it1) }
                longitude?.let { it1 -> putDouble("lng", it1) }
                putString("placeType", "museum")
            }
            Log.d("ShowBundleSearch", bundle.toString())
            findNavController().navigate(R.id.nav_map, bundle)
        }

        binding.item2.setOnClickListener {
            Log.d("SearchFragment", "Ruta 2 pulsada")
            val bundle = Bundle().apply {
                putString("placeName", placeName)
                putString("placeId", placeId)
                latitude?.let { it1 -> putDouble("lat", it1) }
                longitude?.let { it1 -> putDouble("lng", it1) }
                putString("placeType", "natural_feature")
            }
            Log.d("ShowBundleSearch", bundle.toString())
            findNavController().navigate(R.id.nav_map, bundle)
        }

        binding.item3.setOnClickListener {
            Log.d("SearchFragment", "Ruta 3 pulsada")
            val bundle = Bundle().apply {
                putString("placeName", placeName)
                putString("placeId", placeId)
                latitude?.let { it1 -> putDouble("lat", it1) }
                longitude?.let { it1 -> putDouble("lng", it1) }
                putString("placeType", "church")
            }
            Log.d("ShowBundleSearch", bundle.toString())
            findNavController().navigate(R.id.nav_map, bundle)
        }

        binding.item4.setOnClickListener {
            Log.d("SearchFragment", "Ruta 4 pulsada")
            val bundle = Bundle().apply {
                putString("placeName", placeName)
                putString("placeId", placeId)
                latitude?.let { it1 -> putDouble("lat", it1) }
                longitude?.let { it1 -> putDouble("lng", it1) }
                putString("placeType", "restaurant")
            }
            Log.d("ShowBundleSearch", bundle.toString())
            findNavController().navigate(R.id.nav_map, bundle)
        }


        // Obtener el place_id del Bundle de la navegación
        this.placeId = arguments?.getString("place_id") ?: return binding.root // Devuelve la vista si place_id es nulo

        // Llama a la función para buscar detalles del lugar
        fetchPlaceDetails(this.placeId!!)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun fetchPlaceDetails(placeId: String) {
        val apiKey = getString(R.string.API_KEY) // Inserta tu clave de API de Google Places aquí
        val apiUrl = "https://maps.googleapis.com/maps/api/place/details/json?fields=name,photo,geometry&place_id=$placeId&key=$apiKey"

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

                val jsonObject = JSONObject(response.toString())
                if (jsonObject.getString("status") == "OK") {
                    val result = jsonObject.getJSONObject("result")
                    this.placeName = result.getString("name")
                    val locationObj = result.getJSONObject("geometry").getJSONObject("location")
                    this.latitude = locationObj.getDouble("lat")
                    this.longitude = locationObj.getDouble("lng")
                    val photos = if (result.has("photos")) result.getJSONArray("photos") else null
                    val photoReference = if (photos != null && photos.length() > 0) {
                        val photoObj = photos.getJSONObject(0)
                        photoObj.getString("photo_reference")
                    } else {
                        null
                    }

                    // Cambiar el texto de destination1 con el nombre del lugar
                    activity?.runOnUiThread {
                        binding.destination1.text = this.placeName
                        binding.title2.text = "Love $placeName"
                    }

                    // Cargar la foto si hay una referencia de foto
                    if (photoReference != null) {
                        activity?.runOnUiThread {
                            loadPlacePhoto(photoReference)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun loadPlacePhoto(photoReference: String) {
        val maxwidth = 300 // Tamaño máximo de la imagen
        val apiKey = "AIzaSyAG6Mpf5GzKQbeDXOQQ2NUvPlARMobE_SQ" // Insert your Google Places API key here
        val apiUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxwidth&photo_reference=$photoReference&key=$apiKey"

        Picasso.get()
            .load(apiUrl)
            .into(binding.destinationImage) // Cambia 'destinationImage' al ID de tu ImageView
    }
}