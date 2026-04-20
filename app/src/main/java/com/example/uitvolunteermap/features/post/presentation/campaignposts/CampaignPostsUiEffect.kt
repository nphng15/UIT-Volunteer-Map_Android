package com.example.uitvolunteermap.features.post.presentation.campaignposts

sealed interface CampaignPostsUiEffect {
    data object NavigateBack : CampaignPostsUiEffect
    data class ShowMessage(val message: String) : CampaignPostsUiEffect
}
