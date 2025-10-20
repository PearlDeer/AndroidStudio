package com.icc.practica6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.icc.practica6.databinding.ActivityDetalleProductoBinding
import java.text.NumberFormat
import java.util.*

class DetalleProductoActivity : AppCompatActivity() {
    private lateinit var b: ActivityDetalleProductoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDetalleProductoBinding.inflate(layoutInflater)
        setContentView(b.root)

        val nombre = intent.getStringExtra("nombre") ?: "Sin nombre"
        val precio = intent.getDoubleExtra("precio", 0.0)
        val imagenUrl = intent.getStringExtra("imagenUrl")

        val nf = NumberFormat.getCurrencyInstance(Locale("es","MX"))

        b.txtNombreDetalle.text = nombre
        b.txtPrecioDetalle.text = nf.format(precio)

        if (!imagenUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(imagenUrl)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(b.imgDetalle)
        } else {
            b.imgDetalle.setImageResource(android.R.drawable.ic_menu_report_image)
        }
    }
}
