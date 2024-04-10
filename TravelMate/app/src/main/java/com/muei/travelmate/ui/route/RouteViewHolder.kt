package com.muei.travelmate.ui.route

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muei.travelmate.R

class RouteViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    val location_type = view.findViewById<TextView>(R.id.location_type)
    val location_name = view.findViewById<TextView>(R.id.location_name)

    fun render(location:Location, position: Int){
        // TODO: Añadir texto como cadena resources
        if(position == 0) {
            location_type.text = "Origen"
            location_name.hint = "Introduzca su Origen"
        }else{
            if(position == 1) {
                location_type.text = "Destino "
            }else {
                location_type.text = "Destino " + position
            }
            location_name.hint = "Introduzca su Destino"
        }
        //location_name.text = location.name
    }
}