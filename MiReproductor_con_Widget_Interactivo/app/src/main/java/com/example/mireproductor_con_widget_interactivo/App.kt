package com.example.mireproductor_con_widget_interactivo

import android.app.Application
import com.parse.Parse

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("Do9XvWadMxIBd9kXyZYMWXhYv8dqOl6I90SEqgGt")
                .clientKey("q5XOeiCy83ZUEZD21rl0K4RaAkM6swWvmVKIuDm7")
                .server(getString(R.string.back4app_server))
                .build()
        )
    }
}