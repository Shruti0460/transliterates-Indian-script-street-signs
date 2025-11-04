package com.example.street_light.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.street_light.ui.camera.CameraScreen
import com.example.street_light.ui.history.HistoryScreen
import com.example.street_light.ui.settings.SettingsScreen

/**
 * Navigation setup for the app
 */
@Composable
fun StreetLightNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Camera.route
    ) {
        composable(Screen.Camera.route) {
            CameraScreen(
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Camera.route) { inclusive = false }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Camera.route) { inclusive = false }
                    }
                }
            )
        }
    }
}

