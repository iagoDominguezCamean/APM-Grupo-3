package com.muei.travelmate.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muei.travelmate.R
import com.squareup.picasso.Picasso

data class SpotifyTrack(val name: String, val artist: String, val imageUrl: String?)


class fav_custom_Fragment(private val tracks: List<SpotifyTrack>) : RecyclerView.Adapter<fav_custom_Fragment.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.lista_fragment, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val track = tracks[i]
        viewHolder.itemTitle.text = track.name
        viewHolder.itemDetail.text = track.artist

        if (track.imageUrl != null) {
            Picasso.get().load(track.imageUrl).into(viewHolder.itemImage) // Carga la imagen desde URL
        } else {
            viewHolder.itemImage.setImageResource(R.drawable.nota_musical) // Imagen por defecto si no hay URL
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.item_image)
        var itemTitle: TextView = itemView.findViewById(R.id.item_title)
        var itemDetail: TextView = itemView.findViewById(R.id.item_detail)
    }
}
