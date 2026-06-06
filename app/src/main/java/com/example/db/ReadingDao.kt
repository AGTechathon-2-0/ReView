package com.example.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.EcoReading
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Query("SELECT * FROM readings ORDER BY timestamp DESC")
    fun getAllReadings(): Flow<List<EcoReading>>

    @Query("SELECT * FROM readings WHERE synced_to_sheets = 0")
    suspend fun getUnsyncedReadings(): List<EcoReading>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: EcoReading)
    
    @Update
    suspend fun updateReadings(vararg readings: EcoReading)

    @Query("DELETE FROM readings")
    suspend fun clearAll()
}
