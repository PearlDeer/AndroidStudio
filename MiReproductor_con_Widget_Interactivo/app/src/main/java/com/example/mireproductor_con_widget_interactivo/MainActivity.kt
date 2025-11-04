package com.example.mireproductor_con_widget_interactivo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tv = TextView(this)
        tv.text = "Hello World - Mi Reproductor"
        tv.textSize = 24f

        setContentView(tv)
    }
}
