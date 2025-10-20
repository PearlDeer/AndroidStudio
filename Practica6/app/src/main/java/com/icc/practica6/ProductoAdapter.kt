package com.icc.practica6

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icc.practica6.databinding.ItemproductoBinding
import java.text.NumberFormat
import java.util.*

class ProductoAdapter(
    private val onItemClick: (Producto) -> Unit
) : ListAdapter<Producto, ProductoAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Producto>() {
            override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class VH(val b: ItemproductoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        val binding = ItemproductoBinding.inflate(inf, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        holder.b.txtNombre.text = p.nombre

        // Formato monetario MXN
        val nf = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        holder.b.txtPrecio.text = nf.format(p.precio)

        // Cargar imagen con Glide: placeholder y error
        if (!p.imagenUrl.isNullOrBlank()) {
            Glide.with(holder.itemView)
                .load(p.imagenUrl)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_report_image) // puedes cambiar por tu drawable
                .error(android.R.drawable.stat_notify_error) // puedes cambiar por tu drawable
                .into(holder.b.imgProducto)
        } else {
            // Si no hay URL, mostrar placeholder por defecto
            holder.b.imgProducto.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        holder.itemView.setOnClickListener {
            onItemClick(p)
        }
    }
}
