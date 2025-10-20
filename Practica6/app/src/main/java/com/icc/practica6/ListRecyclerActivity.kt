package com.icc.practica6

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.icc.practica6.databinding.ActivityrecyclerBinding
import com.parse.ParseObject
import com.parse.ParseQuery
import java.text.NumberFormat
import java.util.*

class ListRecyclerActivity: AppCompatActivity() {
    private lateinit var b: ActivityrecyclerBinding
    private lateinit var adapter: ProductoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityrecyclerBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter = ProductoAdapter { producto ->
            // Al hacer click en item -> abrir DetalleProductoActivity
            val i = Intent(this, DetalleProductoActivity::class.java)
            i.putExtra("nombre", producto.nombre)
            i.putExtra("precio", producto.precio)
            i.putExtra("imagenUrl", producto.imagenUrl)
            startActivity(i)
        }

        b.recyclerProductos.layoutManager = LinearLayoutManager(this)
        b.recyclerProductos.adapter = adapter

        // Swipe-to-refresh
        b.swipeRefresh.setOnRefreshListener {
            cargarDesdeBack4App()
        }

        cargarDesdeBack4App()
    }

    private fun cargarDesdeBack4App(){
        b.swipeRefresh.isRefreshing = true
        val query = ParseQuery.getQuery<ParseObject>("Producto")
        query.orderByAscending("nombre")
        query.findInBackground{ lista, e ->
            b.swipeRefresh.isRefreshing = false
            if(e == null && lista != null){
                val mapeo = lista.map{ po ->
                    Producto(
                        id = po.objectId,
                        nombre = po.getString("nombre")?: "Sin nombre",
                        precio = po.getNumber("precio")?.toDouble() ?: 0.0,
                        imagenUrl = po.getString("imagenUrl")
                    )
                }
                adapter.submitList(mapeo)
            } else {
                // mostrar error breve
                Toast.makeText(this, "Error al cargar productos: ${e?.message ?: "desconocido"}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
