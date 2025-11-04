package com.example.mireproductor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mireproductor.databinding.ActivityrecyclerBinding
import com.parse.ParseObject
import com.parse.ParseQuery

class ListRecyclerActivity : AppCompatActivity() {
    private lateinit var b: ActivityrecyclerBinding
    private lateinit var adapter: CancionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityrecyclerBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter = CancionAdapter { cancion ->
            val i = Intent(this, DetalleCancionActivity::class.java)
            i.putExtra("nombre", cancion.nombre)
            i.putExtra("autor", cancion.autor)
            i.putExtra("duracion", cancion.duracion)
            i.putExtra("imagenUrl", cancion.imagenUrl)
            i.putExtra("cancionUrl", cancion.cancionUrl)
            startActivity(i)
        }

        b.recyclerProductos.layoutManager = LinearLayoutManager(this)
        b.recyclerProductos.adapter = adapter

        b.swipeRefresh.setOnRefreshListener { cargarCanciones() }

        cargarCanciones()
    }

    private fun cargarCanciones() {
        b.swipeRefresh.isRefreshing = true
        val query = ParseQuery.getQuery<ParseObject>("Cancion")
        query.orderByAscending("nombre")

        query.findInBackground { list, e ->
            b.swipeRefresh.isRefreshing = false
            if (e == null && list != null) {
                val canciones = list.map { po ->
                    Cancion(
                        id = po.objectId,
                        nombre = po.getString("nombre") ?: "Sin nombre",
                        autor = po.getString("autor") ?: "Desconocido",
                        imagenUrl = po.getString("imagenUrl"),
                        duracion = po.getString("duracion") ?: "",
                        cancionUrl = po.getParseFile("cancion")?.url
                    )
                }
                adapter.submitList(canciones)
            } else {
                Toast.makeText(this, "Error al cargar canciones: ${e?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
