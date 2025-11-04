package com.example.mireproductor

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mireproductor.databinding.ActivityDetalleBinding

class DetalleCancionActivity : AppCompatActivity() {

    private lateinit var b: ActivityDetalleBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetalleBinding.inflate(layoutInflater)
        setContentView(b.root)

        val nombre = intent.getStringExtra("nombre")
        val autor = intent.getStringExtra("autor")
        val duracion = intent.getStringExtra("duracion")
        val imagenUrl = intent.getStringExtra("imagenUrl")
        val cancionUrl = intent.getStringExtra("cancionUrl")

        b.txtNombreDetalle.text = nombre
        b.txtPrecioDetalle.text = "$autor | $duracion"

        Glide.with(this).load(imagenUrl).into(b.imgDetalle)

        if (!cancionUrl.isNullOrBlank()) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(cancionUrl)
                prepare()
            }
        }

        b.btnPlay.setOnClickListener { mediaPlayer?.start() }
        b.btnPause.setOnClickListener { mediaPlayer?.pause() }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
}
