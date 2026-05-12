package com.example.uitvolunteermap.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uitvolunteermap.features.home.presentation.HomeRoute

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {
        composable(route = AppDestination.Home.route) {
            HomeRoute()
        }
    }
}
