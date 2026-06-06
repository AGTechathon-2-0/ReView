package com.example.logic

import com.example.model.EcoReading
import com.example.api.RawSensorResponse
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object LogicEngine {
    
    fun processReading(
        raw: RawSensorResponse,
        lat: Double?,
        lng: Double?,
        sessionId: String,
        deviceId: String
    ): EcoReading {
        val temp = raw.temperature ?: 0.0
        val hum = raw.humidity ?: 0.0
        val gas = raw.gas ?: 0.0
        val uv = raw.uv_index ?: 0.0
        val pressure = raw.pressure ?: 0.0
        
        // 1. Heat Index Calculation (Steadman's logic approx)
        val heatIndex = calculateHeatIndex(temp, hum)
        val comfortStatus = when {
            heatIndex < 27 -> "Comfortable"
            heatIndex < 32 -> "Warm"
            heatIndex < 41 -> "Uncomfortable"
            heatIndex < 54 -> "Dangerous"
            else -> "Extreme Danger"
        }
        
        // 2. AQI Calculation from gas resistance directly mapped to Wemos AP
        val aqiStatus = raw.iaq_status?.uppercase(Locale.US) ?: when {
            gas > 400 -> "GOOD"
            gas > 200 -> "MODERATE"
            gas > 100 -> "POOR"
            else -> "HAZARDOUS"
        }
        val aqiScore = when (aqiStatus) {
            "GOOD" -> 0
            "MODERATE" -> 50
            "POOR" -> 150
            else -> 300
        }
        val aqiColor = when (aqiStatus) {
            "GOOD" -> "#00E400"
            "MODERATE" -> "#FFFF00"
            "POOR" -> "#FF7E00"
            else -> "#FF0000"
        }
        
        // 3. UV Status
        val uvStatus = when {
            uv < 3 -> "Low"
            uv < 6 -> "Moderate"
            uv < 8 -> "High"
            uv < 11 -> "Very High"
            else -> "Extreme"
        }
        val uvAdvice = when (uvStatus) {
            "Low" -> "No protection needed."
            "Moderate" -> "Wear sunscreen outdoors."
            "High" -> "Protection essential. Seek shade."
            "Very High" -> "Avoid outside sun."
            else -> "Stay indoors."
        }
        
        // 4. Weather Trend (naive simple)
        val weatherTrend = "Stable"
        
        // 5. Health Advice
        val healthAdvice = when (aqiStatus) {
            "GOOD" -> "Air quality is great. Enjoy the outdoors."
            "MODERATE" -> "Air quality moderate. Sensitive groups should take caution."
            "POOR" -> "Reduce prolonged outdoor exertion."
            else -> "Health warning of emergency conditions. Stay indoors."
        }
        
        // 6. Alert logic
        val alert = if (aqiStatus == "HAZARDOUS" || comfortStatus == "Extreme Danger") {
            "Critical condition detected!"
        } else null
        
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        
        return EcoReading(
            id = UUID.randomUUID().toString(),
            timestamp = sdf.format(Date()),
            lat = lat,
            lng = lng,
            temperature = temp,
            humidity = hum,
            pressure = pressure,
            gas = gas,
            uv = uv,
            aqi_score = aqiScore,
            aqi_status = aqiStatus,
            aqi_color = aqiColor,
            heat_index = heatIndex,
            comfort_status = comfortStatus,
            uv_status = uvStatus,
            uv_advice = uvAdvice,
            weather_trend = weatherTrend,
            health_advice = healthAdvice,
            alert = alert,
            session_id = sessionId,
            device_id = deviceId,
            synced_to_sheets = false
        )
    }
    
    private fun calculateHeatIndex(t: Double, h: Double): Double {
        // Simplified formula for Celsius
        return t + (0.33 * h) - 5.33
    }
    
    private fun calculateIaqFromGas(gas: Double): Int {
        // Mock translation. If it's MOX (e.g., BME680), typically gas resistance is higher = cleaner air.
        // Assuming user's 'gas: 290' means typical raw gas resistance
        val baselineGas = 500.0
        val ratio = baselineGas / (gas + 1.0)
        return (ratio * 50).coerceIn(0.0, 500.0).toInt()
    }
}
