package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentHomeBinding
import com.muei.travelmate.ui.route.Location
import com.muei.travelmate.ui.route.LocationType

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val mock_locations = arrayOf(
        Location("París", "", LocationType.CITY, ""),
        Location("Barcelona","", LocationType.CITY, ""),
        Location("Tokio","", LocationType.CITY, ""),
        Location("Berlín", "", LocationType.CITY, ""),
        Location("Moscú","", LocationType.CITY, ""),
        Location("Marrakech","", LocationType.CITY, ""),

        Location("ResurrectionFest","", LocationType.MUSIC, ""),
        Location("Sonar","", LocationType.MUSIC, ""),
        Location("Glastonbury Festival","", LocationType.MUSIC, ""),
        Location("Tomorrowland","", LocationType.MUSIC, ""),
        Location("Montreux Jazz Festival","", LocationType.MUSIC, ""),
        Location("Sziget Festival","", LocationType.MUSIC, ""),

        Location("Torre Eiffel", "", LocationType.HISTORIC_SITE, ""),
        Location("Machu Picchu","", LocationType.HISTORIC_SITE, ""),
        Location("Gran Muralla China","", LocationType.HISTORIC_SITE, ""),
        Location("Sagrada Familia", "", LocationType.HISTORIC_SITE, ""),
        Location("Catedral de Santiago de Compostela","", LocationType.HISTORIC_SITE, ""),
        Location("Coliseo","", LocationType.HISTORIC_SITE, ""),

        Location("Museo del Louvre", "", LocationType.ART, ""),
        Location("Museo de Arte Moderno (MoMA)","", LocationType.ART, ""),
        Location("Galería Uffizi","", LocationType.ART, ""),
        Location("Centro Pompidou", "", LocationType.ART, ""),
        Location("Museo Nacional de Arte Reina Sofía","", LocationType.ART, ""),
        Location("Tate Modern","", LocationType.ART, ""),

        Location("Parque Nacional de Yellowstone", "", LocationType.NATURE, ""),
        Location("Parque Nacional Banff","", LocationType.NATURE, ""),
        Location("Parque Nacional de Jiuzhaigou","", LocationType.NATURE, ""),
        Location("Fiordos de Noruega", "", LocationType.NATURE, ""),
        Location("Parque Nacional Kruger","", LocationType.NATURE, ""),
        Location("Parque Nacional Torres del Paine","", LocationType.NATURE, ""),

        Location("Disneyland París","", LocationType.MISC, ""),
        Location("Nou Camp","", LocationType.MISC, ""),
        Location("Legoland","", LocationType.MISC, ""),
        Location("Isla de Sentosa","", LocationType.MISC, ""),
        Location("Las Vegas","", LocationType.MISC, ""),
        Location("Costa Amalfitana","", LocationType.MISC, ""),

    )

    lateinit var featured0:ImageButton
    lateinit var featured1:ImageButton
    lateinit var recommended0:ImageButton
    lateinit var recommended1:ImageButton
    lateinit var recommended2:ImageButton
    lateinit var recommended3:ImageButton

    lateinit var featured_locations:List<Location>
    lateinit var recommended_locations:List<Location>

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

       // for(i in getLocations(3,LocationType.NATURE)){
       //     println(i.searchTerm)
       // }

        // extender el imagebutton para que a partir de location se inicialice

        featured_locations = getLocations(2,LocationType.CITY)
        recommended_locations = getLocations(4,LocationType.MUSIC)

        featured0 = binding.root.findViewById(R.id.featured0)
        featured1 = binding.root.findViewById(R.id.featured0)
        recommended0 = binding.root.findViewById(R.id.recommended0)
        recommended1 = binding.root.findViewById(R.id.recommended1)
        recommended2 = binding.root.findViewById(R.id.recommended2)
        recommended3 = binding.root.findViewById(R.id.recommended3)

        featured0.setOnClickListener() {
            Toast.makeText(binding.root.context, "ImageButton clickeado", Toast.LENGTH_SHORT).show()
        }



        return binding.root
    }
    // Función para elegir n ubicaciones con el tipo coincidente
    fun getLocations(n: Int, tag: LocationType): List<Location> {
        // Filtrar ubicaciones por tipo
        val filteredLocations = mock_locations.filter { it.type == tag }

        // Si n es mas grande que la lista original devuelvo toda la lista
        if (n >= filteredLocations.size) {
            return filteredLocations.shuffled()
        }

        // Seleccionar aleatoriamente n ubicaciones de las filtradas
        return filteredLocations.shuffled().take(n)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}