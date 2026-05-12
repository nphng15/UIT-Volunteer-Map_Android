package com.example.uitvolunteermap.features.admin.post.presentation

sealed interface AdminPostUiEvent {
    data object RefreshRequested : AdminPostUiEvent
    data object PullToRefreshTriggered : AdminPostUiEvent

    // Form
    data object CreateClicked : AdminPostUiEvent
    data class EditClicked(val postId: Int) : AdminPostUiEvent
    data class TitleChanged(val value: String) : AdminPostUiEvent
    data class ContentChanged(val value: String) : AdminPostUiEvent
    data class TeamIdChanged(val value: String) : AdminPostUiEvent
    data class AuthorIdChanged(val value: String) : AdminPostUiEvent
    data object FormSubmitted : AdminPostUiEvent
    data object FormDismissed : AdminPostUiEvent

    // Delete
    data class DeleteClicked(val postId: Int) : AdminPostUiEvent
    data object DeleteConfirmed : AdminPostUiEvent
    data object DeleteCancelled : AdminPostUiEvent
}
