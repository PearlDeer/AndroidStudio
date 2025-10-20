package com.icc.practica6

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.icc.practica6.databinding.ActivitylistviewBinding
import com.parse.ParseObject
import com.parse.ParseQuery
import java.text.NumberFormat
import java.util.*

class ListViewActivity: AppCompatActivity() {
    private lateinit var b: ActivitylistviewBinding
    private val datos = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitylistviewBinding.inflate(layoutInflater)
        setContentView(b.root)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            mutableListOf<String>()
        )
        b.listViewProductos.adapter = adapter

        // Item click -> mostrar AlertDialog con nombre + precio (formato MXN)
        b.listViewProductos.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val p = datos[position]
                val nf = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                val msg = "${p.nombre}\n${nf.format(p.precio)}"
                AlertDialog.Builder(this)
                    .setTitle("Producto")
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
                    .show()
            }

        cargarDesdeBack4App(adapter)
    }

    private fun cargarDesdeBack4App(adapter: ArrayAdapter<String>){
        val query = ParseQuery.getQuery<ParseObject>("Producto")
        query.orderByAscending("nombre")
        query.findInBackground{ list, e ->
            if(e == null && list != null){
                datos.clear()
                val nombres = mutableListOf<String>()
                val nf = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                for(po in list){
                    val p = Producto(
                        id = po.objectId,
                        nombre = po.getString("nombre") ?: "Sin nombre",
                        precio = po.getNumber("precio")?.toDouble() ?: 0.0,
                        imagenUrl = po.getString("imagenUrl")
                    )
                    datos.add(p)
                    nombres.add("${p.nombre}  ${nf.format(p.precio)}")
                }
                adapter.clear()
                adapter.addAll(nombres)
                adapter.notifyDataSetChanged()
            } else {
                // manejar error (opcional)
            }
        }
    }
}
