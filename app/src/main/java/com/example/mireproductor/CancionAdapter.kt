package com.example.mireproductor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mireproductor.databinding.ItemproductoBinding

class CancionAdapter(
    private val onItemClick: (Cancion) -> Unit
) : ListAdapter<Cancion, CancionAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Cancion>() {
            override fun areItemsTheSame(oldItem: Cancion, newItem: Cancion) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Cancion, newItem: Cancion) = oldItem == newItem
        }
    }

    inner class VH(val b: ItemproductoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        val binding = ItemproductoBinding.inflate(inf, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = getItem(position)
        holder.b.txtNombre.text = c.nombre
        holder.b.txtPrecio.text = "${c.autor} | ${c.duracion}"

        if (!c.imagenUrl.isNullOrBlank()) {
            Glide.with(holder.itemView)
                .load(c.imagenUrl)
                .centerCrop()
                .into(holder.b.imgProducto)
        } else {
            holder.b.imgProducto.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        holder.itemView.setOnClickListener { onItemClick(c) }
    }
}
