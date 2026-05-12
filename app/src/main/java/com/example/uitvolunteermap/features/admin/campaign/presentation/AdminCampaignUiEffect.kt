package com.example.uitvolunteermap.features.admin.campaign.presentation

sealed interface AdminCampaignUiEffect {
    data class ShowMessage(val message: String) : AdminCampaignUiEffect
}
