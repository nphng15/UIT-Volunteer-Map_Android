package com.example.uitvolunteermap.features.home.presentation.volunteer

sealed interface VolunteerHomeUiEvent {
    data object RefreshRequested : VolunteerHomeUiEvent
    data class CampaignPrimaryClicked(val campaignId: Int) : VolunteerHomeUiEvent
    data class CampaignSecondaryClicked(val campaignId: Int) : VolunteerHomeUiEvent
}
