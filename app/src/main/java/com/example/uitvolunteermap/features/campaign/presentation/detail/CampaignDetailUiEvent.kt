package com.example.uitvolunteermap.features.campaign.presentation.detail

sealed interface CampaignDetailUiEvent {
    data object RefreshRequested : CampaignDetailUiEvent
    data object BackClicked : CampaignDetailUiEvent
    data object ReadMoreClicked : CampaignDetailUiEvent
    data object ViewAllPostsClicked : CampaignDetailUiEvent
    data object OpenGoogleMapsClicked : CampaignDetailUiEvent
    data class TeamClicked(val teamId: Int) : CampaignDetailUiEvent
    data class PostClicked(val postId: Int) : CampaignDetailUiEvent
}
