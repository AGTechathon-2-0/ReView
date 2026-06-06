package com.example.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.EcoReading
import com.example.viewmodel.EcoViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun HeatmapScreen(viewModel: EcoViewModel) {
    val readings by viewModel.allReadings.collectAsState()
    val context = LocalContext.current

    // Initialize osmdroid configuration safely
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    Configuration.getInstance().userAgentValue = context.packageName

    val defaultLocal = GeoPoint(20.2634, 73.0169)
    val startLoc = readings.firstOrNull { it.lat != null && it.lng != null }?.let { GeoPoint(it.lat!!, it.lng!!) } ?: defaultLocal

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(14.0)
                    controller.setCenter(startLoc)
                }
            },
            update = { mapView ->
                mapView.overlays.clear()
                readings.filter { it.lat != null && it.lng != null }.forEach { reading ->
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(reading.lat!!, reading.lng!!)
                    marker.title = reading.aqi_status
                    marker.snippet = "AQI: ${reading.aqi_score} | T: ${String.format("%.1f", reading.temperature)}"
                    
                    // Basic fallback for Marker status styling could be implemented here
                    // using custom drawables, but default works for a simple rendering
                    mapView.overlays.add(marker)
                }
                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
