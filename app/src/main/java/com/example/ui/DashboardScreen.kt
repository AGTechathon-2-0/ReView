package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.EcoViewModel

@Composable
fun DashboardScreen(viewModel: EcoViewModel) {
    val readings by viewModel.allReadings.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val insightsText by viewModel.insightsText.collectAsState()
    
    val latest = readings.firstOrNull()

    if (latest == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No readings yet. Connect to EcoTag.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Live Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Updated at: ${latest.timestamp}")
                    Text("GPS: ${latest.lat}, ${latest.lng}")
                }
            }
        }
        
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard("Temperature", "${String.format("%.1f", latest.temperature)} °C", Modifier.weight(1f))
                MetricCard("Humidity", "${String.format("%.1f", latest.humidity)} %", Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard("Pressure", "${latest.pressure} hPa", Modifier.weight(1f))
                MetricCard("UV Index", "${latest.uv}", Modifier.weight(1f))
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().background(Color(android.graphics.Color.parseColor(latest.aqi_color))).padding(16.dp)) {
                    Column {
                        Text("AQI: ${latest.aqi_status}", style = MaterialTheme.typography.headlineSmall, color = Color.Black)
                        Text("Score: ${latest.aqi_score}", color = Color.Black)
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Comfort: ${latest.comfort_status}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Heat Index: ${String.format("%.1f", latest.heat_index)} °C")
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Health Advice", fontWeight = FontWeight.Bold)
                    Text(latest.health_advice)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("UV Advice: ${latest.uv_advice}")
                }
            }
        }
        
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Gemini AI Insights", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Button(onClick = { viewModel.fetchInsights() }) {
                            Text("Analyze")
                        }
                    }
                    if (insightsText != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(insightsText ?: "", color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sync: $syncStatus", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { viewModel.syncNow() }) {
                    Text("Force Sync")
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}
