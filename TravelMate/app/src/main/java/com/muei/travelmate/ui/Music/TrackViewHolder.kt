package com.muei.travelmate.ui.Music

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muei.travelmate.R

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view){

    val songName = view.findViewById<TextView>(R.id.songName)
    val artistName = view.findViewById<TextView>(R.id.artistName)

    fun bind(trackName: String, trackArtist: String){
        songName.text = trackName
        artistName.text = trackArtist
    }
}