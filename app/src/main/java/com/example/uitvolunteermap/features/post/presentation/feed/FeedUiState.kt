package com.example.uitvolunteermap.features.post.presentation.feed

import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostCardUiModel

data class FeedUiState(
    val posts: List<CampaignPostCardUiModel> = emptyList(),
    val expandedPostId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
