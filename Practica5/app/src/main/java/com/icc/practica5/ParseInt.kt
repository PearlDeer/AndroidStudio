package com.icc.practica5

import android.app.Application
import com.parse.Parse

class ParseInt: Application(){
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("rZfeikEA6XrYK6u8AnNHg9auiBOg1tqz0lScuFp1")
                .clientKey("UnHc6JZxTugQA0WtMiHSNl3FCSR6uuQN88n4YA2z")
                .server("https://parseapi.back4app.com/")
                .build()
        )
    }
}