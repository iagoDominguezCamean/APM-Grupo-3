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
import com.muei.travelmate.ui.route.RouteAdapter

class RouteFragment : Fragment() {

    private var _binding: FragmentRouteBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
            LocationProvider.routeList.add(Location(""))
            println("---------------%%%%%%%%%% "+LocationProvider.routeList)
            //recyclerItemAdded(LocationProvider.routeList.size-1)
            recyclerItemChanged()

        }

        // asigno accion al boton de buscar ruta
        val buttonSearch = binding.root.findViewById<Button>(R.id.buttonSearch)
        buttonSearch.setOnClickListener {
            recyclerItemChanged()
            val result = LocationProvider.routeList.joinToString(",") { it.toString() }
            showToast("Buscar ruta -> "+result)
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

        recyclerView.adapter?.notifyDataSetChanged()
    }

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

    fun updateAddButton(){
        // Actualizar bt añadir parada
        val buttonAdd = binding.root.findViewById<Button>(R.id.buttonAddStop)
        val remainingStops = 5-LocationProvider.routeList.size
        buttonAdd.text = "Añadir parada ("+remainingStops.toString()+")"
        buttonAdd.isEnabled = remainingStops>=1
    }

    fun showToast(message: String?) {
        val context = context ?: return // Check if context is null
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}