package com.example.uitvolunteermap.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uitvolunteermap.features.auth.presentation.LoginRoute
import com.example.uitvolunteermap.features.home.presentation.HomeRoute
import com.example.uitvolunteermap.features.profile.presentation.ProfileRoute

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Profile.route
    ) {
        composable(route = AppDestination.Login.route) {
            LoginRoute(
                onLoginSuccess = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = AppDestination.Home.route) {
            HomeRoute()
        }
        composable(route = AppDestination.Profile.route) {
            ProfileRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onProfileSaved = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(AppDestination.Profile.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onError = { errorMessage ->
                    // Error handling can be extended here (e.g., show snackbar)
                    navController.popBackStack()
                }
            )
        }
    }
}
