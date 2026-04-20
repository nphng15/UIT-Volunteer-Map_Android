package com.example.uitvolunteermap.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.uitvolunteermap.app.navigation.AppNavHost

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    Surface(color = MaterialTheme.colorScheme.background) {
        AppNavHost(navController = navController)
    }
}
