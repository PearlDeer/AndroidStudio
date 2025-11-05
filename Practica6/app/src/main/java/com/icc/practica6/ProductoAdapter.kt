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
    private val onItemClick: (Cancion) -> Unit
) : ListAdapter<Cancion, CancionAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Cancion>() {
            override fun areItemsTheSame(oldItem: Cancion, newItem: Cancion): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Cancion, newItem: Cancion): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class VH(val b: ItemCancionBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        val binding = ItemCancionBinding.inflate(inf, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = getItem(position)
        holder.b.txtNombre.text = c.nombre
        holder.b.txtDuracion.text = c.duracion

        if (!c.imgUrl.isNullOrBlank()) {
            Glide.with(holder.itemView)
                .load(c.imgUrl)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.b.imgCancion)
        } else {
            holder.b.imgCancion.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        holder.itemView.setOnClickListener { onItemClick(c) }
    }
}