package com.example.uitvolunteermap.features.home.presentation.volunteer

sealed interface VolunteerHomeUiEffect {
    data class ShowMessage(val message: String) : VolunteerHomeUiEffect
    data class NavigateToCampaignDetail(val campaignId: Int) : VolunteerHomeUiEffect
}
