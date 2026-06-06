package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "readings")
@JsonClass(generateAdapter = true)
data class EcoReading(
    @PrimaryKey(autoGenerate = false) val id: String,
    val timestamp: String,
    val lat: Double?,
    val lng: Double?,
    val temperature: Double,
    val humidity: Double,
    val pressure: Double,
    val gas: Double,
    val uv: Double,
    @Json(name = "iaq_score") val aqi_score: Int,
    @Json(name = "iaq_status") val aqi_status: String,
    @Json(name = "iaq_color") val aqi_color: String,
    val heat_index: Double,
    val comfort_status: String,
    val uv_status: String,
    val uv_advice: String,
    val weather_trend: String,
    val health_advice: String,
    val alert: String?,
    val session_id: String,
    val device_id: String,
    val synced_to_sheets: Boolean
)
