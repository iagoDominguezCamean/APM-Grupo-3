package com.muei.travelmate.ui.Music

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muei.travelmate.R

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view){

    val songName = view.findViewById<TextView>(R.id.songName)
    val artistName = view.findViewById<TextView>(R.id.artistName)
    //val item_image = view.findViewById<TextView>(R.id.item_image)

    fun bind(trackName: String, trackArtist: String){
        songName.text = trackName
        artistName.text = trackArtist
    }
}