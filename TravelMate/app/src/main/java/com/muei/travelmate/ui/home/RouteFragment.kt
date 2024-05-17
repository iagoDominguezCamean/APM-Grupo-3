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
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentRouteBinding
import com.muei.travelmate.ui.route.Location
import com.muei.travelmate.ui.route.LocationProvider
import com.muei.travelmate.ui.route.LocationType
import com.muei.travelmate.ui.route.PlaceLatLngAsyncTask
import com.muei.travelmate.ui.route.RouteAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RouteFragment : Fragment() {

    private var _binding: FragmentRouteBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val routeTotalStops: Int = 5
    private var latlngArray: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val homeViewModel = ViewModelProvider(this).get(RouteViewModel::class.java)

        _binding = FragmentRouteBinding.inflate(inflater, container, false)
        binding.arrowIcon.setOnClickListener {
            Log.d("RouteFragment", "Flecha para volver pulsada")
            val bundle = Bundle().apply {
                putString("placeName", "Map")
                putString("placeId", " ")
                putDouble("lat", 0.00000000000)
                putDouble("lng", 0.00000000000)
                putString("placeType","default")
            }
            Log.d("ShowBundleRoute", bundle.toString())
            findNavController().navigate(R.id.nav_map, bundle)
        }

        // Populo el Recyclerview de la busqueda de ruta
        initRecyclerRouteSearch()

        // asigno accion al boton de añadir parada
        val buttonAdd = binding.root.findViewById<Button>(R.id.buttonAddStop)
        buttonAdd.setOnClickListener {
            LocationProvider.routeList.add(Location("", "", LocationType.CITY, "", ""))
            println("%%%%%%%%%% Añade localidad a la lista:"+LocationProvider.routeList)
            //recyclerItemAdded(LocationProvider.routeList.size-1)
            recyclerItemChanged()

        }

        // asigno accion al boton de buscar ruta
        val buttonSearch = binding.root.findViewById<Button>(R.id.buttonSearch)
        buttonSearch.setOnClickListener {
            recyclerItemChanged()
            val result = LocationProvider.routeList.joinToString(",") { it.toString() }
            println("%%%%%%%%%% Busqueda con la lista:"+LocationProvider.routeList)
            showToast("Buscar ruta -> "+result)

            val bundle = Bundle()
            bundle.putInt("numItems", LocationProvider.routeList.size)
            bundle.putString("bundleType", "customRoute")

            CoroutineScope(Dispatchers.Main).launch {
                val deferred = async {
                    LocationProvider.routeList.map { location ->
                        async {
                            PlaceLatLngAsyncTask(location.toString(), getString(R.string.API_KEY)) { latLng ->
                                this@RouteFragment.latlngArray.add(latLng.toString())
                                Log.d("CustomRoute","location added: $location $latLng")
                            }.execute().get()
                        }
                    }
                }

                deferred.await()

                Log.d("CustomRouteMap",latlngArray.toString())
                bundle.putStringArrayList("location_array", latlngArray)
                findNavController().navigate(R.id.nav_map, bundle)
            }
        }

        updateAddButton()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun initRecyclerRouteSearch(){
        val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerRouteSearch)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter =
            RouteAdapter(LocationProvider.routeList, { recyclerItemChanged() })
    }

    fun recyclerItemChanged(){
        updateAddButton()
        // Actualizar adaptador
        val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerRouteSearch)

        //recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = RouteAdapter(LocationProvider.routeList, { recyclerItemChanged() })

    }
    /*
    fun recyclerItemAdded(pos:Int) {
        updateAddButton()
        val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerRouteSearch)
        recyclerView.adapter?.notifyItemInserted(pos)
    }

    fun recyclerItemRemoved(pos:Int) {
        updateAddButton()
        val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerRouteSearch)
        recyclerView.adapter?.notifyItemRemoved(pos)
    }
    */
    fun updateAddButton(){
        // Actualizar bt añadir parada
        val buttonAdd = binding.root.findViewById<Button>(R.id.buttonAddStop)
        val routeRemainingStops = routeTotalStops-LocationProvider.routeList.size
        buttonAdd.text = "Añadir parada ("+routeRemainingStops.toString()+")"
        buttonAdd.isEnabled = routeRemainingStops>=1
    }

    fun showToast(message: String?) {
        val context = context ?: return // Check if context is null
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}