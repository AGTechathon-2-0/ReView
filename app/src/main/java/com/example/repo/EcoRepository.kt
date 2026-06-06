package com.example.repo

import com.example.api.EcoTagApi
import com.example.api.SheetsApi
import com.example.db.ReadingDao
import com.example.logic.LogicEngine
import com.example.model.EcoReading
import com.example.utils.LocationTracker
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class EcoRepository(
    private val api: EcoTagApi,
    private val sheetsApi: SheetsApi,
    private val dao: ReadingDao,
    private val locationTracker: LocationTracker,
    val deviceId: String = "ecotag_001",
    val sessionId: String = "session_${System.currentTimeMillis()}"
) {
    val allReadings: Flow<List<EcoReading>> = dao.getAllReadings()
    
    suspend fun fetchAndStoreReading(): Result<EcoReading> {
        return try {
            val raw = api.getSensorReadings()
            val location = locationTracker.getCurrentLocation()
            val reading = LogicEngine.processReading(
                raw = raw,
                lat = location?.latitude,
                lng = location?.longitude,
                sessionId = sessionId,
                deviceId = deviceId
            )
            dao.insertReading(reading)
            Result.success(reading)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncUnsyncedReadings(sheetsUrl: String): Int {
        val unsynced = dao.getUnsyncedReadings()
        if (unsynced.isEmpty()) return 0
        
        try {
            val response = sheetsApi.syncReadings(sheetsUrl, unsynced)
            if (response.status == "success") {
                val updated = unsynced.map { it.copy(synced_to_sheets = true) }
                dao.updateReadings(*updated.toTypedArray())
                return updated.size
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}
