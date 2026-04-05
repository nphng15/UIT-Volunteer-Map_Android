package com.example.uitvolunteermap.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Text
import com.example.uitvolunteermap.features.home.presentation.HomeRoute
import com.example.uitvolunteermap.features.auth.presentation.SplashRoute
import android.util.Log

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        // BƯỚC 1: Luôn bắt đầu bằng splash để check token
        startDestination = "splash"
    ) {
        // BƯỚC 2: Định nghĩa màn hình Splash
        composable("splash") {
            SplashRoute(
                onNavigateToHome = {
                    // Khi thành công, nhảy vào Route Home có sẵn của bạn
                    Log.d("AppNavHost", "Đang chuyển sang HOME")
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    Log.d("AppNavHost", "Đang chuyển sang LOGIN")
                    // Khi thất bại, nhảy vào Login
                    navController.navigate(route = AppDestination.Login.route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // BƯỚC 3: Màn hình Home (Dùng lại HomeRoute bạn đã có)
        composable(route = AppDestination.Home.route) {
            Log.d("AppNavHost", "Đã chuyển sang HOME")
            HomeRoute()
        }

        // BƯỚC 4: Màn hình Login (Tạm thời để Text để check)
        composable(route = AppDestination.Login.route) {
            Log.d("AppNavHost", "Đã chuyển sang LOGIN")
            Text(text = "Màn hình Login nè!")
        }
    }
}
