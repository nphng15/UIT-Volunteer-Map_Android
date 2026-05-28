package com.example.uitvolunteermap.features.campaign.presentation.list

sealed interface CampaignListUiEffect {
    data class NavigateToCampaignDetail(val campaignId: Int) : CampaignListUiEffect
    data class ShowMessage(val message: String) : CampaignListUiEffect
}
