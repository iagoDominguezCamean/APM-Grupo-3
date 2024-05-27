package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.net.PlacesClient
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentRouteBinding
import com.muei.travelmate.ui.route.Location
import com.muei.travelmate.ui.route.LocationProvider
import com.muei.travelmate.ui.route.LocationType
import com.muei.travelmate.ui.route.PlaceLatLngAsyncTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.google.android.libraries.places.api.Places
import com.muei.travelmate.ui.route.RouteAdapter

class RouteFragment : Fragment() {

    private var _binding: FragmentRouteBinding? = null
    private val binding get() = _binding!!
    private val routeTotalStops: Int = 5
    private var latlngArray: ArrayList<String> = ArrayList()
    private lateinit var placesClient: PlacesClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.arrowIcon.setOnClickListener {
            findNavController().navigate(R.id.nav_map, Bundle().apply {
                putString("placeName", "Map")
                putString("placeId", " ")
                putDouble("lat", 0.0)
                putDouble("lng", 0.0)
                putString("placeType", "default")
            })
        }

        Places.initialize(requireContext(), getString(R.string.API_KEY))
        placesClient = Places.createClient(requireContext())
        initRecyclerRouteSearch()

        val buttonAdd = binding.root.findViewById<Button>(R.id.buttonAddStop)
        buttonAdd.setOnClickListener {
            LocationProvider.routeList.add(Location("", "", LocationType.CITY, "", ""))
            recyclerItemChanged()
        }

        val buttonSearch = binding.root.findViewById<Button>(R.id.buttonSearch)
        buttonSearch.setOnClickListener {
            recyclerItemChanged()
            val result = LocationProvider.routeList.joinToString(",") { it.toString() }
            showToast("Buscar ruta -> $result")

            val bundle = Bundle()
            bundle.putInt("numItems", LocationProvider.routeList.size)
            bundle.putString("bundleType", "customRoute")
            bundle.putBoolean("routeInfo", true)

            CoroutineScope(Dispatchers.Main).launch {
                val deferred = async {
                    LocationProvider.routeList.map { location ->
                        async {
                            PlaceLatLngAsyncTask(location.toString(), getString(R.string.API_KEY)) { latLng ->
                                latLng?.put("name", location.name)
                                this@RouteFragment.latlngArray.add(latLng.toString())
                                Log.d("CustomRoute","location added: $location $latLng")
                            }.execute().get()
                        }
                    }
                }

                deferred.await()

                bundle.putString("latlngArrayString", latlngArray.toString())
                findNavController().navigate(R.id.nav_map, bundle)
            }
        }

        updateAddButton()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerRouteSearch() {
        val recyclerView: RecyclerView = binding.recyclerRouteSearch
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RouteAdapter(LocationProvider.routeList, { recyclerItemChanged() }, placesClient)
    }

    private fun recyclerItemChanged() {
        updateAddButton()
        binding.recyclerRouteSearch.adapter?.notifyDataSetChanged()
    }

    private fun updateAddButton() {
        val buttonAdd = binding.root.findViewById<Button>(R.id.buttonAddStop)
        val routeRemainingStops = routeTotalStops - LocationProvider.routeList.size
        buttonAdd.text = "Añadir parada ($routeRemainingStops)"
        buttonAdd.isEnabled = routeRemainingStops >= 1
    }

    private fun showToast(message: String?) {
        val context = context ?: return
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
