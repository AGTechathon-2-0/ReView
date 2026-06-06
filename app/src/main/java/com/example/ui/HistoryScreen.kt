package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.EcoViewModel

@Composable
fun HistoryScreen(viewModel: EcoViewModel) {
    val readings by viewModel.allReadings.collectAsState()
    
    val grouped = readings.groupBy { it.session_id }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        grouped.forEach { (session, sessionReadings) ->
            item {
                Text("Session: $session", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(sessionReadings.take(5)) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.timestamp, style = MaterialTheme.typography.bodySmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("AQI: ${item.aqi_score}")
                            Text("Temp: ${String.format("%.1f", item.temperature)}")
                            Text(if (item.synced_to_sheets) "Synced \u2713" else "Unsynced")
                        }
                    }
                }
            }
            if (sessionReadings.size > 5) {
                item {
                    Text("... and ${sessionReadings.size - 5} more", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
