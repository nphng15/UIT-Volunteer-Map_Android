package com.example.uitvolunteermap.features.campaign.presentation.areamap

sealed interface CampaignAreaMapUiEffect {
    data object NavigateBack : CampaignAreaMapUiEffect
    data object RequestLocation : CampaignAreaMapUiEffect
    data class ShowMessage(val message: String) : CampaignAreaMapUiEffect
}
