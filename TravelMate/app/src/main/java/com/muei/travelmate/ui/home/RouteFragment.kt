package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentRouteBinding
import com.muei.travelmate.databinding.FragmentSearchBinding

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
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}