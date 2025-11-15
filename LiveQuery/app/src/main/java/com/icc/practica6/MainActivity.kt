package com.icc.practica6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear un TextView que diga "Hola"
        val textView = TextView(this)
        textView.text = "Hola"
        textView.textSize = 30f

        // Usar el TextView como vista principal
        setContentView(textView)
    }
}
