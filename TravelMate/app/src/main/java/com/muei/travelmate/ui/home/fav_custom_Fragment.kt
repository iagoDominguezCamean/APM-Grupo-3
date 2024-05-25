package com.muei.travelmate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muei.travelmate.R
import com.squareup.picasso.Picasso


data class Song(val name: String, val artist: String)

class SongAdapter(private val songs: List<Song>) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songName: TextView = itemView.findViewById(R.id.songName)
        val artistName: TextView = itemView.findViewById(R.id.artistName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemsongadapterfav, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songName.text = song.name
        holder.artistName.text = song.artist
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}
