package com.muei.travelmate.ui.route

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.net.PlacesClient
import com.muei.travelmate.R

class RouteAdapter(private val routeList: List<Location>, private val onRemoveListener:()-> Unit, private val placesClient: PlacesClient) : RecyclerView.Adapter<RouteViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RouteViewHolder(layoutInflater.inflate(R.layout.item_route,parent,false), placesClient)
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.render(routeList, position, onRemoveListener)
    }
}