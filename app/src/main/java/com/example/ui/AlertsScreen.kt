package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.EcoViewModel

@Composable
fun AlertsScreen(viewModel: EcoViewModel) {
    val readings by viewModel.allReadings.collectAsState()
    val alertReadings = readings.filter { it.alert != null }

    if (alertReadings.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active alerts.")
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(alertReadings) { alertItem ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("ALERT FIRED", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(alertItem.alert ?: "")
                    Text("Time: ${alertItem.timestamp}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Triggered by:")
                    Text("- AQI Status: ${alertItem.aqi_status}")
                    Text("- Comfort: ${alertItem.comfort_status}")
                }
            }
        }
    }
}
