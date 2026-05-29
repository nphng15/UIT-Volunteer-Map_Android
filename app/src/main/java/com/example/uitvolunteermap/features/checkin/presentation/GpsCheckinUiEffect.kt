package com.example.uitvolunteermap.features.checkin.presentation

sealed interface GpsCheckinUiEffect {
    data object NavigateBack : GpsCheckinUiEffect
    data object RequestLocationPermission : GpsCheckinUiEffect
    data object RequestLocation : GpsCheckinUiEffect
    data class ShowMessage(val message: String) : GpsCheckinUiEffect
}
