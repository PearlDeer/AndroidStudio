package com.example.mireproductorconwidgetinteractivo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: Long,
    val url: String,
    val albumArtUrl: String? = null,
    val iconRes: Int = R.drawable.ic_music_note
) : Parcelable {
    
    fun getDurationFormatted(): String {
        val totalSeconds = duration / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
