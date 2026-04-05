package com.example.uitvolunteermap.features.auth.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import android.util.Log
@Composable
fun SplashRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            Log.d("SplashRoute", "Nhận được hiệu ứng (Effect) = $effect")

            when (effect) {
                is SplashUiEffect.NavigateToHome -> {
                    Log.d("SplashRoute", "Đang gọi lệnh onNavigateToHome()")
                    onNavigateToHome()
                }

                is SplashUiEffect.NavigateToLogin -> {
                    Log.d("SplashRoute", "Đang gọi lệnh onNavigateToLogin()")
                    onNavigateToLogin()
                }
                is SplashUiEffect.ShowError -> {
                    Log.d("SplashRoute", "Đang gọi lệnh Toast.makeText()")
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    SplashScreen()
}