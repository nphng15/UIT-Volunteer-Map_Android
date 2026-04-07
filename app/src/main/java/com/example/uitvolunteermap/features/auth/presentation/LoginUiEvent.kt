package com.example.uitvolunteermap.features.auth.presentation

sealed interface LoginUiEvent {
    data object NavigateToHome : LoginUiEvent
}
