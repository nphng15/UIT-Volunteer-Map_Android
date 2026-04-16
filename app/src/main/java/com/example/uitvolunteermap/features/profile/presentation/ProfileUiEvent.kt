package com.example.uitvolunteermap.features.profile.presentation

sealed interface ProfileUiEvent {
    // Dùng 'data object' thay cho 'object' (Kotlin 1.9+) để có hàm toString() tốt hơn
    data object ProfileSaved : ProfileUiEvent
    data object NavigateToLogin : ProfileUiEvent
    data class ShowError(val message: String) : ProfileUiEvent
}