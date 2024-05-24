package com.muei.travelmate.ui.route

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.muei.travelmate.R

class RouteViewHolder(view:View, private val placesClient: PlacesClient) : RecyclerView.ViewHolder(view) {

    val location_type = view.findViewById<TextView>(R.id.location_type)
    val location_name = view.findViewById<AutoCompleteTextView>(R.id.location_name)
    val delete_button = view.findViewById<ImageButton>(R.id.delete_button)


    fun render(routeList: List<Location>, position: Int, onRemoveListener:()-> Unit){
        val listSize: Int = routeList.size-1

        // TODO: Añadir texto como cadena resources
        if(position == 0) {
            location_type.text = "Origen"
            location_name.hint = "Introduzca su Origen"
        }else{
            if(position == listSize) {
                location_type.text = "Destino "
            }else {
                location_type.text = "Parada " + position
            }
            location_name.hint = "Introduzca su Destino"

        }
        Log.d("Autocompletado", "RouteList:" + routeList[position].toString())
        val editableText = SpannableStringBuilder(routeList[position].toString())
        location_name.setText(editableText)

        // Almacenar las localidades introducidas
        val txtWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                Log.d("Autocompletado", "valor de s: " +s.toString())
                if (s != null) {
                    val token = AutocompleteSessionToken.newInstance()
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(s.toString())
                        .setSessionToken(token)
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            // Procesa las sugerencias de autocompletado aquí
                            val predictions = response.autocompletePredictions
                            // Actualiza la UI con las sugerencias obtenidas
                            Log.d("Autocompletado", predictions.toString())
                            val adapter = ArrayAdapter<String>(itemView.context, android.R.layout.simple_dropdown_item_1line)
                            for (prediction in predictions) {
                                adapter.add(prediction.getFullText(null).toString())
                            }
                            location_name.setAdapter(adapter)
                            location_name.showDropDown()
                        }
                        .addOnFailureListener { exception ->
                            // Maneja los errores de la API aquí
                            Log.d("Autcompletado", exception.toString())
                        }
                }
                LocationProvider.routeList[position]=Location(s.toString(), "", LocationType.CITY, "","")
                println("%%%%%%%%%% Introduce localidad: "+position+", lista:"+LocationProvider.routeList)
            }
        }
        location_name.addTextChangedListener(txtWatcher)


        // Accion de boton eliminar
        delete_button.setOnClickListener {
            location_name.text=Editable.Factory.getInstance().newEditable("")
            LocationProvider.routeList.removeAt(position)
            println("%%%%%%%%%% Elimina posicion: "+position+", lista:"+LocationProvider.routeList)
            onRemoveListener()
        }

        delete_button.isEnabled=(listSize >=2)

    }
}