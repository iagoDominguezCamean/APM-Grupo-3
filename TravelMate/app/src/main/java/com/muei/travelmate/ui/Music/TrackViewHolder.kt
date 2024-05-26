package com.muei.travelmate.ui.Music

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muei.travelmate.R
import com.squareup.picasso.Picasso

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view){

    val songName = view.findViewById<TextView>(R.id.songName)
    val artistName = view.findViewById<TextView>(R.id.artistName)
    val albumCover = view.findViewById<ImageView>(R.id.item_image)

    fun bind(trackName: String, trackArtist: String, cover: String){
        songName.text = trackName
        artistName.text = trackArtist

        println("albumCover ")
        println(albumCover)

        Picasso.get()
            .load(cover)
            .into(albumCover) // Cambia 'destinationImage' al ID de tu ImageView

    }
}