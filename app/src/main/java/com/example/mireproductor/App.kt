package com.example.mireproductor

import android.app.Application
import com.parse.Parse

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("Oh7qrXgWvTOPMnARquWcpzDm8Wc0NDUpk5zDHaSb")
                .clientKey("EQ3Jayd0kGShAdD8JJSoqde7E4wfJlWUGVbSK7Uf")
                .server(getString(R.string.back4app_server))
                .build()
        )
    }
}