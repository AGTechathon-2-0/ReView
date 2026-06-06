package com.example.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import com.example.model.EcoReading

interface SheetsApi {
    @POST
    suspend fun syncReadings(
        @Url url: String,
        @Body readings: List<EcoReading>
    ): SheetsResponse
}

data class SheetsResponse(
    val status: String,
    val synced_count: Int?
)
