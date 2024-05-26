package com.muei.travelmate.ui.Music

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.net.PlacesClient
import com.muei.travelmate.R

class TrackAdapter (private val tracks: List<Pair<String, String>>) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TrackViewHolder(layoutInflater.inflate(R.layout.itemsongadapterfav,parent,false))
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val (trackName, trackArtist) = tracks[position]
        holder.bind(trackName, trackArtist)
    }
}