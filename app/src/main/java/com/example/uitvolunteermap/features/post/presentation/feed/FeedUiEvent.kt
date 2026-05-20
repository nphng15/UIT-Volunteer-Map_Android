package com.example.uitvolunteermap.features.post.presentation.feed

sealed interface FeedUiEvent {
    data object RefreshRequested : FeedUiEvent
    data class PostCardClicked(val postId: Int) : FeedUiEvent
}
