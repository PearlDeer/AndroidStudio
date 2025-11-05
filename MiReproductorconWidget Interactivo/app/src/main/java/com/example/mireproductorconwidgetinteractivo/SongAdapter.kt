package com.example.mireproductorconwidgetinteractivo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SongAdapter(
    private val songs: List<Song>,
    private val onSongClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSongIcon: ImageView = itemView.findViewById(R.id.imgSongIcon)
        val tvSongTitle: TextView = itemView.findViewById(R.id.tvSongTitle)
        val tvSongArtist: TextView = itemView.findViewById(R.id.tvSongArtist)
        val tvSongDuration: TextView = itemView.findViewById(R.id.tvSongDuration)

        fun bind(song: Song) {
            tvSongTitle.text = song.title
            tvSongArtist.text = song.artist
            tvSongDuration.text = song.getDurationFormatted()


            val albumUrl = song.albumArtUrl
            if (albumUrl != null && albumUrl.isNotEmpty()) {
                Picasso.get()
                    .load(albumUrl)
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .into(imgSongIcon)
            } else {
                imgSongIcon.setImageResource(song.iconRes)
            }

            itemView.setOnClickListener {
                onSongClick(song)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size
}
