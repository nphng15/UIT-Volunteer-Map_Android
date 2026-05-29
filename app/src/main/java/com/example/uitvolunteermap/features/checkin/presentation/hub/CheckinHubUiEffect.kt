package com.example.uitvolunteermap.features.checkin.presentation.hub

sealed interface CheckinHubUiEffect {
    data object RequestLocationPermission : CheckinHubUiEffect
    data object RequestLocation : CheckinHubUiEffect
    data class ShowMessage(val message: String) : CheckinHubUiEffect
}
