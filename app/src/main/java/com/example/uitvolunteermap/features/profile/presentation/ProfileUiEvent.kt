package com.example.uitvolunteermap.features.profile.presentation

sealed interface ProfileUiEvent {
    data object NavigateToLogin : ProfileUiEvent
}
