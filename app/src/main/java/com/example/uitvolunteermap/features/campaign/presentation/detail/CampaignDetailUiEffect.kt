package com.example.uitvolunteermap.features.campaign.presentation.detail

sealed interface CampaignDetailUiEffect {
    data object NavigateBack : CampaignDetailUiEffect
    data class ShowMessage(val message: String) : CampaignDetailUiEffect
}
