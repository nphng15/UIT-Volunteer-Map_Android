package com.example.uitvolunteermap.features.profile.presentation

sealed interface ProfileUiEvent {
    object ProfileSaved : ProfileUiEvent
    object NavigateToLogin : ProfileUiEvent
    data class ShowError(val message: String) : ProfileUiEvent
}
