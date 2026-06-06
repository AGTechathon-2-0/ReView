package com.example.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.EcoReading

@Database(entities = [EcoReading::class], version = 2, exportSchema = false)
abstract class EcoDatabase : RoomDatabase() {
    abstract fun readingDao(): ReadingDao

    companion object {
        @Volatile
        private var instance: EcoDatabase? = null

        fun getDatabase(context: Context): EcoDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    EcoDatabase::class.java,
                    "ecotag_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                .also { instance = it }
            }
        }
    }
}
