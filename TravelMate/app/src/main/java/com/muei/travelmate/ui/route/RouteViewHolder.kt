package com.muei.travelmate.ui.route

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muei.travelmate.R

class RouteViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    val location_type = view.findViewById<TextView>(R.id.location_type)
    val location_name = view.findViewById<TextView>(R.id.location_name)
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
        location_name.text = routeList[position].toString()

        // Almacenar las localidades introducidas
        val txtWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                LocationProvider.routeList[position]=Location(s.toString())
                println("%%%%%%%%%% Introduce localidad: "+position+", lista:"+LocationProvider.routeList)
            }
        }
        location_name.addTextChangedListener(txtWatcher)


        // Accion de boton eliminar
        delete_button.setOnClickListener {
            location_name.text=""
            LocationProvider.routeList.removeAt(position)
            println("%%%%%%%%%% Elimina posicion: "+position+", lista:"+LocationProvider.routeList)
            onRemoveListener()
        }

        delete_button.isEnabled=(listSize >=2)

    }
}