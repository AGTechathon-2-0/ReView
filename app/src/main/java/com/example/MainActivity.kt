package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.EcoViewModel

import androidx.activity.compose.rememberLauncherForActivityResult

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: EcoViewModel = viewModel()
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: EcoViewModel) {
    val navController = rememberNavController()

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Custom logic for permissions
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EcoTag") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val items = listOf(
                    Triple(Screen.Connection, "Network", Icons.Default.Wifi),
                    Triple(Screen.Dashboard, "Dash", Icons.Default.Dashboard),
                    Triple(Screen.Heatmap, "Map", Icons.Default.Map),
                    Triple(Screen.History, "History", Icons.Default.History),
                    Triple(Screen.Alerts, "Alerts", Icons.Default.Warning),
                    Triple(Screen.Settings, "Config", Icons.Default.Settings)
                )

                items.forEach { (screen, label, icon) ->
                    val routeStr = screen::class.qualifiedName
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentDestination?.hierarchy?.any { it.route == routeStr } == true,
                        onClick = {
                            navController.navigate(screen) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Connection,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            composable<Screen.Connection> { ConnectionScreen(viewModel) }
            composable<Screen.Dashboard> { DashboardScreen(viewModel) }
            composable<Screen.Heatmap> { HeatmapScreen(viewModel) }
            composable<Screen.History> { HistoryScreen(viewModel) }
            composable<Screen.Alerts> { AlertsScreen(viewModel) }
            composable<Screen.Settings> { SettingsScreen(viewModel) }
        }
    }
}
