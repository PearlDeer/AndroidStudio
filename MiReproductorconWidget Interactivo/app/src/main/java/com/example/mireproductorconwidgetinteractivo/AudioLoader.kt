package com.example.mireproductorconwidgetinteractivo

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log


object AudioLoader {
    
    private const val TAG = "AudioLoader"
    


    fun loadAudioFiles(context: Context): List<Song> {
        val audioList = mutableListOf<Song>()
        

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        
        var cursor: Cursor? = null
        
        try {
            cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )
            
            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                
                var songId = 1
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val title = it.getString(titleColumn) ?: "Desconocido"
                    val artist = it.getString(artistColumn) ?: "Artista Desconocido"
                    val duration = it.getLong(durationColumn)
                    val data = it.getString(dataColumn)
                    val albumId = it.getLong(albumIdColumn)
                    
                    // Crear URI del archivo de audio
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    

                    val albumArtUri = getAlbumArtUri(albumId)
                    
                    val song = Song(
                        id = songId++,
                        title = title,
                        artist = artist,
                        duration = duration,
                        url = contentUri.toString(),
                        albumArtUrl = albumArtUri,
                        iconRes = R.drawable.ic_music_note
                    )
                    
                    audioList.add(song)
                    
                    Log.d(TAG, "Canción encontrada: $title - $artist (${formatDuration(duration)})")
                }
            }
            
            Log.d(TAG, "Total de canciones encontradas: ${audioList.size}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar archivos de audio", e)
        } finally {
            cursor?.close()
        }
        

        if (audioList.isEmpty()) {
            Log.w(TAG, "No se encontraron canciones en el dispositivo, usando canciones de ejemplo")
            return getDefaultSongs()
        }
        
        return audioList
    }
    

    private fun getAlbumArtUri(albumId: Long): String? {
        return try {
            val albumArtUri = ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                albumId
            )
            albumArtUri.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener carátula del álbum", e)
            null
        }
    }
    

    private fun formatDuration(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun getDefaultSongs(): List<Song> {
        return listOf(
            Song(
                id = 1,
                title = "Midnight Echoes",
                artist = "Luna Waves",
                duration = 305000, // 5 min 5 sec
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
                iconRes = R.drawable.ic_music_note
            ),
            Song(
                id = 2,
                title = "Solar Drift",
                artist = "Neon Horizon",
                duration = 420000, // 7 min
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3",
                iconRes = R.drawable.ic_music_note
            ),
            Song(
                id = 3,
                title = "Crimson Voyage",
                artist = "Aether Bloom",
                duration = 275000, // 4 min 35 sec
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                iconRes = R.drawable.ic_music_note
            ),
            Song(
                id = 4,
                title = "Neon Dreams",
                artist = "Circuit Pulse",
                duration = 360000, // 6 min
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
                iconRes = R.drawable.ic_music_note
            ),
            Song(
                id = 5,
                title = "Echoes of Tomorrow",
                artist = "Sapphire Skies",
                duration = 480000, // 8 min
                url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
                iconRes = R.drawable.ic_music_note
            )
        )
    }


}
