package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        val homeViewModel =
            ViewModelProvider(this).get(RouteViewModel::class.java)

        _binding = FragmentRouteBinding.inflate(inflater, container, false)
        binding.arrowIcon.setOnClickListener {
            Log.d("RouteFragment", "Flecha para volver pulsada")
            findNavController().navigate(R.id.nav_map) }

        // Populo el Recyclerview de la busqueda de ruta
        init_recycler_route_search()
        // asigno accion al boton de añadir destino
        val buttonAdd = view?.findViewById<Button>(R.id.buttonAddStop)

        buttonAdd?.setOnClickListener {
            LocationProvider.routeList.add(Location(""))
            init_recycler_route_search()
            println("----"+LocationProvider.routeList+"  "+LocationProvider.routeList.size)
            //Toast.makeText(requireContext(), "Button Clicked", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun init_recycler_route_search(){
        val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerRouteSearch)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = RouteAdapter(LocationProvider.routeList)
    }
}