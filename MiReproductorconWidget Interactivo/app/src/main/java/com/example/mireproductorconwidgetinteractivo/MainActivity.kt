package com.example.mireproductorconwidgetinteractivo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    
    private lateinit var recyclerViewSongs: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private val songsList = mutableListOf<Song>()
    
    companion object {
        var lastPlayedSongIndex = 0 // Para recordar la última canción
        private const val TAG = "MainActivity"
    }
    

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

            loadAudioFiles()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        recyclerViewSongs = findViewById(R.id.recyclerViewSongs)
        

        songAdapter = SongAdapter(songsList) { song ->
            openPlayer(song)
        }
        
        recyclerViewSongs.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = songAdapter
        }
        

        checkAndRequestPermissions()
        

        handleWidgetAction()
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleWidgetAction()
    }
    

    private fun checkAndRequestPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            Manifest.permission.READ_MEDIA_AUDIO
        } else {

            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {

                loadAudioFiles()
            }

            
            else -> {

                requestPermissionLauncher.launch(permission)
            }
        }
    }

    

    private fun loadAudioFiles() {
        songsList.clear()
        

        val audioFiles = AudioLoader.loadAudioFiles(this)
        songsList.addAll(audioFiles)
        
        songAdapter.notifyDataSetChanged()
        

        initializeMusicService()
        

        if (audioFiles.isNotEmpty()) {
            Toast.makeText(
                this,
                " ${audioFiles.size} canciones",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                "NO hay canciones",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    

    private fun loadDefaultSongs() {
        songsList.clear()
        songsList.addAll(AudioLoader.getDefaultSongs())
        songAdapter.notifyDataSetChanged()
        

        initializeMusicService()
        
        Toast.makeText(
            this,
            "🎵 Usando 6 canciones de ejemplo",
            Toast.LENGTH_SHORT
        ).show()
    }
    

    private fun initializeMusicService() {
        if (songsList.isNotEmpty()) {
            val serviceIntent = Intent(this, MusicPlayerService::class.java).apply {
                action = MusicPlayerService.ACTION_SET_SONGS
                putParcelableArrayListExtra(MusicPlayerService.EXTRA_SONGS, ArrayList(songsList))
                putExtra(MusicPlayerService.EXTRA_SONG_INDEX, lastPlayedSongIndex)
            }
            startService(serviceIntent)
        }
    }
    
    private fun handleWidgetAction() {
        val widgetAction = intent.getStringExtra("WIDGET_ACTION")
        if (widgetAction != null && songsList.isNotEmpty()) {

            val song = if (lastPlayedSongIndex < songsList.size) {
                songsList[lastPlayedSongIndex]
            } else {
                songsList[0]
            }
            
            val playerIntent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("SONG", song)
                putExtra("WIDGET_ACTION", widgetAction)
                putParcelableArrayListExtra("ALL_SONGS", ArrayList(songsList))
            }
            startActivity(playerIntent)
        }
    }
    
    private fun openPlayer(song: Song) {

        lastPlayedSongIndex = songsList.indexOfFirst { it.id == song.id }
        
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("SONG", song)
            putParcelableArrayListExtra("ALL_SONGS", ArrayList(songsList))
        }
        startActivity(intent)
    }
}