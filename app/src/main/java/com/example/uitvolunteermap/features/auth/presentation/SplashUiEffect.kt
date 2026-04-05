package com.example.uitvolunteermap.features.auth.presentation

sealed interface SplashUiEffect {
    data object NavigateToHome : SplashUiEffect
    data object NavigateToLogin : SplashUiEffect
    data class ShowError(val message: String) : SplashUiEffect
}