package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.searchBar.setOnClickListener {
            val searchTerm = binding.searchBar.text.toString().trim()
            if (searchTerm.isNotBlank()) {
                val bundle = Bundle().apply {
                    putString("searchTerm", searchTerm)
                }
                Log.d("HomeFragment", "Busqueda iniciada")
                findNavController().navigate(R.id.nav_list_search, bundle)
            } else {
                Log.d("HomeFragment", "No se permitió la búsqueda porque el término de búsqueda está vacío.")
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}