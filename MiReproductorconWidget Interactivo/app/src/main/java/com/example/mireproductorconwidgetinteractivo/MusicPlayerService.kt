package com.example.mireproductorconwidgetinteractivo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayerService : Service() {
    
    private var exoPlayer: ExoPlayer? = null
    private val binder = MusicBinder()
    private var currentSongIndex = 0
    private var allSongs = mutableListOf<Song>()
    
    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_SET_SONGS = "ACTION_SET_SONGS"
        const val EXTRA_SONGS = "EXTRA_SONGS"
        const val EXTRA_SONG_INDEX = "EXTRA_SONG_INDEX"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_player_channel"
        private const val TAG = "MusicPlayerService"
        

        private var serviceInstance: MusicPlayerService? = null
        
        fun isServiceRunning(): Boolean = serviceInstance != null
        fun getCurrentSong(): Song? = serviceInstance?.getCurrentSongInternal()
        fun isPlaying(): Boolean = serviceInstance?.isPlayingInternal() ?: false
    }
    
    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }
    
    override fun onCreate() {
        super.onCreate()
        serviceInstance = this
        createNotificationChannel()
        initializePlayer()
        Log.d(TAG, "Service created")
    }
    
    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        
        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {

                        if (currentSongIndex < allSongs.size - 1) {
                            playNext()
                        } else {

                            exoPlayer?.pause()
                            updateNotification()
                            updateWidget()
                        }
                    }
                    Player.STATE_READY -> {
                        updateNotification()
                        updateWidget()
                    }
                }
            }
            
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateNotification()
                updateWidget()
            }
        })
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> play()
            ACTION_PAUSE -> pause()
            ACTION_PLAY_PAUSE -> playPause()
            ACTION_NEXT -> playNext()
            ACTION_PREVIOUS -> playPrevious()
            ACTION_STOP -> stopService()
            ACTION_SET_SONGS -> {
                val songs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableArrayListExtra(EXTRA_SONGS, Song::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableArrayListExtra<Song>(EXTRA_SONGS)
                }
                val songIndex = intent.getIntExtra(EXTRA_SONG_INDEX, 0)
                
                if (songs != null) {
                    setSongs(songs, songIndex)
                }
            }
        }
        return START_STICKY
    }
    
    fun setSongs(songs: List<Song>, startIndex: Int = 0) {
        allSongs.clear()
        allSongs.addAll(songs)
        currentSongIndex = startIndex.coerceIn(0, allSongs.size - 1)
        
        if (allSongs.isNotEmpty()) {
            loadSong(currentSongIndex)
        }
        
        Log.d(TAG, "Songs set: ${allSongs.size} songs, starting at index $currentSongIndex")
    }
    
    private fun loadSong(index: Int) {
        if (index < 0 || index >= allSongs.size) return
        
        currentSongIndex = index
        val song = allSongs[currentSongIndex]
        
        exoPlayer?.apply {
            setMediaItem(MediaItem.fromUri(song.url))
            prepare()
        }
        
        Log.d(TAG, "Loading song: ${song.title} - ${song.artist}")
        updateNotification()
        updateWidget()
    }
    
    fun play() {
        if (allSongs.isEmpty()) {

            allSongs.addAll(AudioLoader.getDefaultSongs())
            loadSong(0)
        }
        
        exoPlayer?.play()
        updateNotification()
        updateWidget()
        Log.d(TAG, "Playing")
    }
    
    fun pause() {
        exoPlayer?.pause()
        updateNotification()
        updateWidget()
        Log.d(TAG, "Paused")
    }
    
    fun playPause() {
        if (isPlayingInternal()) {
            pause()
        } else {
            play()
        }
    }
    
    fun playNext() {
        if (allSongs.isEmpty()) return
        
        currentSongIndex = (currentSongIndex + 1) % allSongs.size
        loadSong(currentSongIndex)
        exoPlayer?.play()
        Log.d(TAG, "Next song")
    }
    
    fun playPrevious() {
        if (allSongs.isEmpty()) return
        
        currentSongIndex = if (currentSongIndex > 0) {
            currentSongIndex - 1
        } else {
            allSongs.size - 1
        }
        loadSong(currentSongIndex)
        exoPlayer?.play()
        Log.d(TAG, "Previous song")
    }
    
    private fun stopService() {
        exoPlayer?.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    fun getCurrentSongInternal(): Song? {
        return if (currentSongIndex >= 0 && currentSongIndex < allSongs.size) {
            allSongs[currentSongIndex]
        } else null
    }
    
    fun isPlayingInternal(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }
    
    fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }
    
    fun getDuration(): Long {
        return exoPlayer?.duration ?: 0L
    }
    
    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Retro Gamer Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Reproductor de música estilo retro gamer"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun updateNotification() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun createNotification(): Notification {
        val song = getCurrentSongInternal()
        val isPlaying = isPlayingInternal()
        

        val playPauseIntent = Intent(this, MusicPlayerService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        

        val nextIntent = Intent(this, MusicPlayerService::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            this, 1, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        

        val previousIntent = Intent(this, MusicPlayerService::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val previousPendingIntent = PendingIntent.getService(
            this, 2, previousIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val mainPendingIntent = PendingIntent.getActivity(
            this, 3, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song?.title ?: "Retro Player")
            .setContentText(song?.artist ?: "No song playing")
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(mainPendingIntent)
            .addAction(
                android.R.drawable.ic_media_previous,
                "Previous",
                previousPendingIntent
            )
            .addAction(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (isPlaying) "Pause" else "Play",
                playPausePendingIntent
            )
            .addAction(
                android.R.drawable.ic_media_next,
                "Next",
                nextPendingIntent
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setOngoing(isPlaying)
            .build()
    }
    
    private fun updateWidget() {
        val song = getCurrentSongInternal()
        val isPlaying = isPlayingInternal()
        
        if (song != null) {
            MusicPlayerWidget.updateWidgetInfo(
                context = this,
                title = song.title,
                artist = song.artist,
                playing = isPlaying
            )
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
        serviceInstance = null
        Log.d(TAG, "Service destroyed")
    }
}