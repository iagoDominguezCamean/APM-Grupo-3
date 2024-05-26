package com.muei.travelmate.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
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
        Location("París", "@drawable/ic_home_par", LocationType.CITY, "", "Francia"),
        Location("Barcelona","@drawable/ic_home_bcn", LocationType.CITY, "", "España"),
        Location("Tokio","@drawable/ic_home_tok", LocationType.CITY, "", "Japón"),
        Location("Berlín", "@drawable/ic_home_ber", LocationType.CITY, "", "Alemania"),
        Location("Moscú","@drawable/ic_home_mos", LocationType.CITY, "", "Rusia"),
        Location("Marrakech","@drawable/ic_home_mar", LocationType.CITY, "", "Marruecos"),


        Location("Resurrection Fest","@drawable/ic_home_res", LocationType.MUSIC, "", ""),
        Location("Sonar","@drawable/ic_home_son", LocationType.MUSIC, "", ""),
        Location("Glastonbury","@drawable/ic_home_gla", LocationType.MUSIC, "Glastonbury Festival Site", ""),
        Location("Tomorrowland","@drawable/ic_home_tom", LocationType.MUSIC, "Tomorrowland Winter", ""),
        Location("Montreux Jazz Festival","@drawable/ic_home_mon", LocationType.MUSIC, "", ""),
        Location("Sziget Fest","@drawable/ic_home_szi", LocationType.MUSIC, "Sziget Cultural Management Ltd.", ""),

        Location("Torre Eiffel", "@drawable/ic_home_eif", LocationType.HISTORIC_SITE, "", ""),
        Location("Machu Picchu","@drawable/ic_home_mac", LocationType.HISTORIC_SITE, "Machu Picchu, Perú", ""),
        Location("Gran Muralla China","@drawable/ic_home_mur", LocationType.HISTORIC_SITE, "Muralla China", ""),
        Location("Sagrada Familia", "@drawable/ic_home_sag", LocationType.HISTORIC_SITE, "Basílica de la Sagrada Família", ""),
        Location("Catedral de Santiago de Compostela","@drawable/ic_home_san", LocationType.HISTORIC_SITE, "", ""),
        Location("Coliseo de Roma","@drawable/ic_home_col", LocationType.HISTORIC_SITE, "", ""),

        Location("Museo del Louvre", "@drawable/ic_home_lou", LocationType.ART, "", ""),
        Location("Museo de Arte Moderno (MoMA)","@drawable/ic_home_mom", LocationType.ART, "MoMA", ""),
        Location("Galería Uffizi","@drawable/ic_home_ufi", LocationType.ART, "Galeria degli Uffizi", ""),
        Location("Centro Pompidou", "@drawable/ic_home_pom", LocationType.ART, "Centro Nacional de Arte y Cultura Georges Pompidou", ""),
        Location("Museo Nacional de Arte Reina Sofía","@drawable/ic_home_sof", LocationType.ART, "Museo Nacional Centro de Arte Reina Sofía", ""),
        Location("Tate Modern","@drawable/ic_home_tat", LocationType.ART, "", ""),

        Location("Yellowstone", "@drawable/ic_home_yel", LocationType.NATURE, "Parque Nacional Yellowstone", ""),
        Location("Parque Banff","@drawable/ic_home_baf", LocationType.NATURE, "Parque Nacional Banff", ""),
        Location("Parque de Jiuzhaigou","@drawable/ic_home_jiu", LocationType.NATURE, "Jiuzhaigou National Geological Park", ""),
        Location("Fiordos Noruegos", "@drawable/ic_home_osl", LocationType.NATURE, "Oslofjord", ""),
        Location("Parque Kruger","@drawable/ic_home_kru", LocationType.NATURE, "Parque nacional Kruger", ""),
        Location("Torres del Paine","@drawable/ic_home_tor", LocationType.NATURE, "", ""),

        Location("Disneyland París","@drawable/ic_home_dis", LocationType.MISC, "", ""),
        Location("Nou Camp","@drawable/ic_home_nou", LocationType.MISC, "Spotify Camp Nou", ""),
        Location("Legoland","@drawable/ic_home_leg", LocationType.MISC, "LEGOLAND Malaysia", ""),
        Location("Isla de Sentosa","@drawable/ic_home_sen", LocationType.MISC, "Sentosa, Singapur", ""),
        Location("Las Vegas","@drawable/ic_home_las", LocationType.MISC, "Las Vegas, Nevada", ""),
        Location("Costa Amalfitana","@drawable/ic_home_ama", LocationType.MISC, "", ""),

        )

    lateinit var featured0:ImageButton
    lateinit var featured0City: TextView
    lateinit var featured0Country:TextView
    lateinit var featured1:ImageButton
    lateinit var featured1City: TextView
    lateinit var featured1Country:TextView
    lateinit var recommended0:ImageButton
    lateinit var recommended0Title:TextView
    lateinit var recommended1:ImageButton
    lateinit var recommended1Title:TextView
    lateinit var recommended2:ImageButton
    lateinit var recommended2Title:TextView
    lateinit var recommended3:ImageButton
    lateinit var recommended3Title:TextView

    lateinit var music_chip:ImageButton
    lateinit var history_chip:ImageButton
    lateinit var art_chip:ImageButton
    lateinit var nature_chip:ImageButton
    lateinit var leisure_chip:ImageButton

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


        val storedTheme = readFromSharedPreferences(requireContext(), "app_theme", "2")
        AppCompatDelegate.setDefaultNightMode(storedTheme.toInt())

        // for(i in getLocations(3,LocationType.NATURE)){
        //     println(i.searchTerm)
        // }



        // Destacados
        featured0 = binding.root.findViewById(R.id.featured0)
        featured0City=binding.root.findViewById(R.id.destination1)
        featured0Country=binding.root.findViewById(R.id.country1)

        featured1 = binding.root.findViewById(R.id.featured1)
        featured1City=binding.root.findViewById(R.id.destination2)
        featured1Country=binding.root.findViewById(R.id.country2)

        updateFeatured()


        // Categorias
        music_chip = binding.root.findViewById(R.id.music_chip)
        history_chip = binding.root.findViewById(R.id.history_chip)
        art_chip = binding.root.findViewById(R.id.art_chip)
        nature_chip = binding.root.findViewById(R.id.nature_chip)
        leisure_chip = binding.root.findViewById(R.id.leisure_chip)

        music_chip.setOnClickListener(){
            updateChips(music_chip)
            updateRecommended(LocationType.MUSIC)
        }
        history_chip.setOnClickListener(){
            updateChips(history_chip)
            updateRecommended(LocationType.HISTORIC_SITE)
        }
        art_chip.setOnClickListener(){
            updateChips(art_chip)
            updateRecommended(LocationType.ART)
        }
        nature_chip.setOnClickListener(){
            updateChips(nature_chip)
            updateRecommended(LocationType.NATURE)
        }
        leisure_chip.setOnClickListener(){
            updateChips(leisure_chip)
            updateRecommended(LocationType.MISC)
        }
        // recomendados
        recommended0 = binding.root.findViewById(R.id.recommended0)
        recommended0Title = binding.root.findViewById(R.id.recommended0t)
        recommended1 = binding.root.findViewById(R.id.recommended1)
        recommended1Title = binding.root.findViewById(R.id.recommended1t)
        recommended2 = binding.root.findViewById(R.id.recommended2)
        recommended2Title = binding.root.findViewById(R.id.recommended2t)
        recommended3 = binding.root.findViewById(R.id.recommended3)
        recommended3Title = binding.root.findViewById(R.id.recommended3t)

        updateRecommended(LocationType.ANY)

        return binding.root
    }

    fun readFromSharedPreferences(context: Context, key: String, defaultValue: String): String {
        val sharedPref = context.getSharedPreferences("travelmate_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }


    private fun updateChips(activeChip: ImageButton) {
        val allChips = listOf(music_chip, history_chip, art_chip, nature_chip, leisure_chip)
        allChips.forEach { chip ->
            if (chip == activeChip) {
                chip.isEnabled = false
                chip.setBackgroundResource(R.drawable.chip_background_pressed) // Asegúrate de tener un recurso para el fondo deshabilitado si es necesario
            } else {
                chip.isEnabled = true
                chip.setBackgroundResource(R.drawable.chip_background) // Vuelve al fondo original
            }
        }
    }

    fun updateFeatured(){
        featured_locations = getLocations(2,LocationType.CITY)

        featured0City.text = featured_locations[0].name
        featured0Country.text = featured_locations[0].country
        featured0.setImageResource(context?.getResources()!!.getIdentifier(featured_locations[0].image, "drawable", context?.getPackageName()))


        featured1City.text =  featured_locations[1].name
        featured1Country.text =  featured_locations[1].country
        featured1.setImageResource(context?.getResources()!!.getIdentifier(featured_locations[1].image, "drawable", context?.getPackageName()))


        featured0.setOnClickListener() {
            val bundle = Bundle().apply {
                putString("searchTerm", featured_locations[0].searchTerm)
            }
            findNavController().navigate(R.id.nav_list_search, bundle)
        }

        featured1.setOnClickListener() {
            val bundle = Bundle().apply {
                putString("searchTerm", featured_locations[1].searchTerm)
            }
            findNavController().navigate(R.id.nav_list_search, bundle)
        }

    }
    fun updateRecommended(tag:LocationType){


        recommended_locations = getLocations(4,tag)

        recommended0Title.text = recommended_locations[0].name
        recommended0.setImageResource(context?.getResources()!!.getIdentifier(recommended_locations[0].image, "drawable", context?.getPackageName()))


        recommended1Title.text =  recommended_locations[1].name
        recommended1.setImageResource(context?.getResources()!!.getIdentifier(recommended_locations[1].image, "drawable", context?.getPackageName()))


        recommended2Title.text = recommended_locations[2].name
        recommended2.setImageResource(context?.getResources()!!.getIdentifier(recommended_locations[2].image, "drawable", context?.getPackageName()))


        recommended3Title.text =  recommended_locations[3].name
        recommended3.setImageResource(context?.getResources()!!.getIdentifier(recommended_locations[3].image, "drawable", context?.getPackageName()))


        recommended0.setOnClickListener() {
            val bundle = Bundle().apply {
                putString("searchTerm", recommended_locations[0].searchTerm)
            }
            findNavController().navigate(R.id.nav_list_search, bundle)
        }

        recommended1.setOnClickListener() {
            val bundle = Bundle().apply {
                putString("searchTerm", recommended_locations[1].searchTerm)
            }
            findNavController().navigate(R.id.nav_list_search, bundle)
        }
        recommended2.setOnClickListener() {
            val bundle = Bundle().apply {
                putString("searchTerm", recommended_locations[2].searchTerm)
            }
            findNavController().navigate(R.id.nav_list_search, bundle)
        }

        recommended3.setOnClickListener() {
            val bundle = Bundle().apply {
                putString("searchTerm", recommended_locations[3].searchTerm)
            }
            findNavController().navigate(R.id.nav_list_search, bundle)
        }
    }
    // Función para elegir n ubicaciones con el tipo coincidente
    fun getLocations(n: Int, tag: LocationType): List<Location> {
        // Filtrar ubicaciones por tipo
        var filteredLocations:List<Location>
        if(tag==LocationType.ANY){
            filteredLocations= mock_locations.filter { it.type != LocationType.CITY }
        }else{
            filteredLocations= mock_locations.filter { it.type == tag }
        }

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