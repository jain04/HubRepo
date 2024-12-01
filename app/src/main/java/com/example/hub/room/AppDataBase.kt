// AppDatabase.kt
package com.example.hub.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hub.room.data.RepositoryEntity

@Database(entities = [RepositoryEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun repoDao(): RepoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton pattern for the Room database
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Optional: handle migrations manually if needed
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
