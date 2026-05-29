package com.example.uitvolunteermap.features.checkin.presentation.hub

sealed interface CheckinHubUiEvent {
    data object RefreshRequested : CheckinHubUiEvent
    data object PermissionGranted : CheckinHubUiEvent
    data object PermissionDenied : CheckinHubUiEvent
    data class LocationReceived(val latitude: Double, val longitude: Double) : CheckinHubUiEvent
}
