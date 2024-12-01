package com.example.hub.room

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize the database using the thread-safe approach
        database = AppDatabase.getDatabase(applicationContext)
    }

    companion object {
        lateinit var database: AppDatabase
    }
}
