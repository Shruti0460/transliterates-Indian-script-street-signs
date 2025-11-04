package com.example.street_light.ui.navigation

/**
 * Navigation destinations
 */
sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object History : Screen("history")
    object Settings : Screen("settings")
}

