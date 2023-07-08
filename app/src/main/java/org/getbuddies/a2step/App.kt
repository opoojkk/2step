package org.getbuddies.a2step

import android.app.Application
import android.content.Context

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: App

        @JvmStatic
        fun get(): App {
            return instance
        }

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }
}