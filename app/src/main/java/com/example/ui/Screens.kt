package com.example.ui

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable data object Connection : Screen()
    @Serializable data object Dashboard : Screen()
    @Serializable data object Heatmap : Screen()
    @Serializable data object History : Screen()
    @Serializable data object Alerts : Screen()
    @Serializable data object Settings : Screen()
}
