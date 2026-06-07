package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.AppState
import com.example.data.AppStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BimbaApplication : Application() {
    lateinit var database: AppDatabase
        private set

    lateinit var appStateRepository: AppStateRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bimba_database"
        )
        .fallbackToDestructiveMigration()
        .build()

        appStateRepository = AppStateRepository(database.appStateDao())

        // Populate initial data if database is empty
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            // Check if population is needed
            // This is non-blocking
            try {
                // Initialize state if first launch
                database.appStateDao().saveAppState(AppState())
            } catch (e: Exception) {
                // Already populated or error
            }
        }
    }
}
