package com.example.mireproductorconwidgetinteractivo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class PlayerActivity : AppCompatActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var song: Song
    private var currentSongIndex: Int = 0
    private val allSongs = mutableListOf<Song>()

    private lateinit var btnBack: ImageButton
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var imgAlbumArt: ImageView
    private lateinit var tvSongTitle: TextView
    private lateinit var tvArtistName: TextView


    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false

    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (!isUserSeeking && exoPlayer.isPlaying) {
                val currentPosition = exoPlayer.currentPosition.toInt()
                handler.postDelayed(this, 100)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)


        song = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("SONG", Song::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("SONG")
        } ?: run {
            finish()
            return
        }


        val receivedSongs = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("ALL_SONGS", Song::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra<Song>("ALL_SONGS")
        }

        if (receivedSongs != null && receivedSongs.isNotEmpty()) {
            allSongs.clear()
            allSongs.addAll(receivedSongs)
        } else {

            initDefaultSongs()
        }


        currentSongIndex = allSongs.indexOfFirst { it.id == song.id }
        if (currentSongIndex == -1) currentSongIndex = 0

        initViews()
        initPlayer()
        setupListeners()
        updateUI()


        updateWidget(isPlaying = false)


        handleWidgetAction()
    }

    private fun handleWidgetAction() {
        val widgetAction = intent.getStringExtra("WIDGET_ACTION")
        when (widgetAction) {
            "PLAY_PAUSE" -> {

                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                    handler.removeCallbacks(updateSeekBar)
                    updateWidget(isPlaying = false)
                } else {
                    exoPlayer.play()
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                    handler.post(updateSeekBar)
                    updateWidget(isPlaying = true)
                }
            }
            "NEXT" -> {
                playNextSong()
            }
            "PREVIOUS" -> {
                playPreviousSong()
            }
        }
    }

    private fun initDefaultSongs() {

        allSongs.addAll(
            listOf(
                Song(
                    id = 1,
                    title = "Bury The Light",
                    artist = "Casey Edwards ft. Victor Borba",
                    duration = 570000,
                    url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                    iconRes = R.drawable.ic_music_note
                ),
                Song(
                    id = 2,
                    title = "Devil Trigger",
                    artist = "Casey Edwards ft. Ali Edwards",
                    duration = 360000,
                    url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
                    iconRes = R.drawable.ic_music_note
                ),
                Song(
                    id = 3,
                    title = "Crimson Cloud",
                    artist = "Tetsuya Shibata",
                    duration = 240000,
                    url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
                    iconRes = R.drawable.ic_music_note
                ),
                Song(
                    id = 4,
                    title = "Vergil Battle Theme",
                    artist = "Capcom Sound Team",
                    duration = 300000,
                    url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
                    iconRes = R.drawable.ic_music_note
                ),
                Song(
                    id = 5,
                    title = "The Duel",
                    artist = "DMC5 OST",
                    duration = 420000,
                    url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
                    iconRes = R.drawable.ic_music_note
                ),
                Song(
                    id = 6,
                    title = "Shall Never Surrender",
                    artist = "Nevins ft. Jenn Stroud",
                    duration = 390000,
                    url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
                    iconRes = R.drawable.ic_music_note
                )
            )
        )
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        imgAlbumArt = findViewById(R.id.imgAlbumArt)
        tvSongTitle = findViewById(R.id.tvSongTitle)
        tvArtistName = findViewById(R.id.tvArtistName)
    }

    private fun initPlayer() {

        exoPlayer = ExoPlayer.Builder(this).build()


        val mediaItem = MediaItem.fromUri(song.url)


        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()


        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        val duration = exoPlayer.duration.toInt()
                       }
                    Player.STATE_ENDED -> {

                        if (currentSongIndex < allSongs.size - 1) {
                            playNextSong()
                        } else {
                            stopPlayback()
                        }
                    }
                }
            }
        })
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnPlayPause.setOnClickListener {
            if (exoPlayer.isPlaying) {

                exoPlayer.pause()
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
                handler.removeCallbacks(updateSeekBar)
                updateWidget(isPlaying = false)
            } else {

                exoPlayer.play()
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
                handler.post(updateSeekBar)
                updateWidget(isPlaying = true)
            }
        }

        btnPrevious.setOnClickListener {
            playPreviousSong()
        }

        btnNext.setOnClickListener {
            playNextSong()
        }


    }

    private fun playPreviousSong() {
        if (currentSongIndex > 0) {

            currentSongIndex--
            song = allSongs[currentSongIndex]
            MainActivity.lastPlayedSongIndex = currentSongIndex
            loadAndPlaySong()
        } else {

            exoPlayer.seekTo(0)
        }
    }

    private fun playNextSong() {
        if (currentSongIndex < allSongs.size - 1) {

            currentSongIndex++
            song = allSongs[currentSongIndex]
            MainActivity.lastPlayedSongIndex = currentSongIndex
            loadAndPlaySong()
        }
    }

    private fun loadAndPlaySong() {

        handler.removeCallbacks(updateSeekBar)


        val mediaItem = MediaItem.fromUri(song.url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()


        updateUI()
        btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        handler.post(updateSeekBar)


        updateWidget(isPlaying = true)
    }

    private fun updateUI() {
        tvSongTitle.text = song.title
        tvArtistName.text = song.artist


        val albumUrl = song.albumArtUrl
        if (albumUrl != null && albumUrl.isNotEmpty()) {
            com.squareup.picasso.Picasso.get()
                .load(albumUrl)
                .placeholder(R.drawable.ic_music_note)
                .error(R.drawable.ic_music_note)
                .into(imgAlbumArt)
        } else {
            imgAlbumArt.setImageResource(song.iconRes)
        }
    }

    private fun stopPlayback() {
        exoPlayer.stop()
        exoPlayer.seekTo(0)
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
        handler.removeCallbacks(updateSeekBar)
    }

    private fun formatTime(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBar)
        exoPlayer.release()
    }

    override fun onPause() {
        super.onPause()

        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            handler.removeCallbacks(updateSeekBar)
        }
    }


    private fun updateWidget(isPlaying: Boolean) {
        MusicPlayerWidget.updateWidgetInfo(
            context = this,
            title = song.title,
            artist = song.artist,
            playing = isPlaying
        )
    }
}