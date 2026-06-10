package com.example.uitvolunteermap.features.campaign.presentation.areamap

sealed interface CampaignAreaMapUiEvent {
    data object RefreshRequested : CampaignAreaMapUiEvent
    data object BackClicked : CampaignAreaMapUiEvent
    data object MarkHereClicked : CampaignAreaMapUiEvent
    data class LocationReceived(val latitude: Double, val longitude: Double) : CampaignAreaMapUiEvent
    data object LocationUnavailable : CampaignAreaMapUiEvent
    data class ConfirmPoint(val teamId: Int, val teamName: String, val name: String) : CampaignAreaMapUiEvent
    data class ConfirmCheckInLocation(val teamId: Int, val teamName: String) : CampaignAreaMapUiEvent
    data object DialogDismissed : CampaignAreaMapUiEvent
    data class RemovePointClicked(val id: String) : CampaignAreaMapUiEvent
}
