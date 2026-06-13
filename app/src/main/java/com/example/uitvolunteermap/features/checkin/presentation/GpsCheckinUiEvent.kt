package com.example.uitvolunteermap.features.checkin.presentation

sealed interface GpsCheckinUiEvent {
    data object PermissionGranted : GpsCheckinUiEvent
    data object PermissionDenied : GpsCheckinUiEvent
    data class CampaignSelected(val campaign: CheckinCampaignUiModel) : GpsCheckinUiEvent
    data object CheckinClicked : GpsCheckinUiEvent
    data object RetryClicked : GpsCheckinUiEvent
    data object ToggleHistory : GpsCheckinUiEvent
    data object BackClicked : GpsCheckinUiEvent
    data class LocationReceived(val latitude: Double, val longitude: Double) : GpsCheckinUiEvent
    data class LocationFailed(val message: String) : GpsCheckinUiEvent
}
