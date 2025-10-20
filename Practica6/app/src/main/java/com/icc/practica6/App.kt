package com.icc.practica6

import android.app.Application
import com.parse.Parse

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("ri5edz4GZyMLvX5DSmvlA9Lnra8JNiTiNRwFs9ZZ")
                .clientKey("REJAGjicJqvsd5yPCPsDzhb4tIltTP2GfNWkazJC")
                .server(getString(R.string.back4app_server))
                .build()
        )
    }
}