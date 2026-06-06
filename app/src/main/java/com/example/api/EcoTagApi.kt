package com.example.api

import com.squareup.moshi.JsonClass
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
data class RawSensorResponse(
    val temperature: Double?,
    val humidity: Double?,
    val pressure: Double?,
    val gas: Double?,
    val uv_raw: Int?,
    val uv_index: Double?,
    val iaq_status: String?
)

interface EcoTagApi {
    @GET("sensor")
    suspend fun getSensorReadings(): RawSensorResponse
}
