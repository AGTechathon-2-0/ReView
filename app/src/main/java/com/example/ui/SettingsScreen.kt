package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.EcoViewModel

@Composable
fun SettingsScreen(viewModel: EcoViewModel) {
    val syncStatus by viewModel.syncStatus.collectAsState()
    var sheetUrl by remember { mutableStateOf(viewModel.sheetsWebhookUrl.value) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Device Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Device ID: ecotag_001")
        Text("Endpoint: http://192.168.4.1/sensor")
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Google Sheets Sync", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("URL: ...${sheetUrl.takeLast(20)}")
        Text("Current Sync Status: $syncStatus", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.syncNow() }) {
            Text("Force Sync to Sheets")
        }
    }
}
