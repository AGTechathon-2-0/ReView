package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.EcoTagApi
import com.example.api.SheetsApi
import com.example.db.EcoDatabase
import com.example.repo.EcoRepository
import com.example.utils.LocationTracker
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class EcoViewModel(application: Application) : AndroidViewModel(application) {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    // Using custom WifiSocketFactory to ensure requests strictly go over the connected WiFi AP
    // even if it lacks internet, leaving cellular data available for other apps.
    private val sensorRetrofit = Retrofit.Builder()
        .baseUrl("http://192.168.4.1/")
        .client(OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .socketFactory(com.example.utils.WifiSocketFactory(application))
            .build())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    // For Sheets Sync
    private val sheetsRetrofit = Retrofit.Builder()
        .baseUrl("https://script.google.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    // For Strings & UI
    var _insightsText = MutableStateFlow<String?>(null)
    val insightsText: StateFlow<String?> = _insightsText

    private val geminiRetrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = sensorRetrofit.create(EcoTagApi::class.java)
    private val sheetsApi = sheetsRetrofit.create(SheetsApi::class.java)
    private val geminiApi = geminiRetrofit.create(com.example.api.GeminiApi::class.java)
    
    private val database = EcoDatabase.getDatabase(application)
    private val locationTracker = LocationTracker(application)

    private val repository = EcoRepository(
        api = api,
        sheetsApi = sheetsApi,
        dao = database.readingDao(),
        locationTracker = locationTracker
    )

    val allReadings = repository.allReadings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var _connectionStatus = MutableStateFlow("Disconnected")
    val connectionStatus: StateFlow<String> = _connectionStatus
    
    var _syncStatus = MutableStateFlow("Idle")
    val syncStatus: StateFlow<String> = _syncStatus

    var sheetsWebhookUrl = MutableStateFlow("https://script.google.com/macros/s/AKfycbxi0x3P6XFbfI4pHtD2xgEoXwKRW7XLobev7vQZV1SZNadAzo3NYkO5jFEY9his-ls/exec")

    private var pollingJob: Job? = null

    fun startPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (true) {
                val result = repository.fetchAndStoreReading()
                if (result.isSuccess) {
                    _connectionStatus.value = "Connected"
                } else {
                    _connectionStatus.value = "Error: Timeout"
                }
                delay(3000)
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
        _connectionStatus.value = "Stopped"
    }

    fun syncNow() {
        val url = sheetsWebhookUrl.value
        if (url.isBlank()) {
           _syncStatus.value = "No Webhook URL"
           return
        }
        viewModelScope.launch {
            _syncStatus.value = "Syncing..."
            val count = repository.syncUnsyncedReadings(url)
            _syncStatus.value = "Synced $count items"
            delay(3000)
            _syncStatus.value = "Idle"
        }
    }

    fun fetchInsights() {
        val latest = allReadings.value.firstOrNull() ?: return
        viewModelScope.launch {
            _insightsText.value = "Generating insights..."
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                
                val prompt = """
                    You are an environmental and health advisor. 
                    Given the following real-time environmental data:
                    Temp: ${latest.temperature}°C
                    Humidity: ${latest.humidity}%
                    Gas Resistance: ${latest.gas}
                    UV Index: ${latest.uv}
                    AQI Status: ${latest.aqi_status} (Score: ${latest.aqi_score})
                    
                    Provide a short (max 3 sentences), punchy, and actionable insight about the current environment focusing on health or comfort.
                """.trimIndent()

                val request = com.example.api.GenerateContentRequest(
                    contents = listOf(
                        com.example.api.Content(
                            parts = listOf(com.example.api.Part(text = prompt))
                        )
                    )
                )

                val response = geminiApi.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                _insightsText.value = text ?: "Could not generate insights."
            } catch (e: Exception) {
                _insightsText.value = "Error generating insights: ${e.message}"
            }
        }
    }
}
