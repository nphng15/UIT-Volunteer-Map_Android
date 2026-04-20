package com.example.uitvolunteermap.features.campaign.presentation.team

sealed interface TeamFormationDetailUiEvent {
    data object RefreshRequested : TeamFormationDetailUiEvent
    data object BackClicked : TeamFormationDetailUiEvent
    data object HeroEditClicked : TeamFormationDetailUiEvent
    data object AddActivityClicked : TeamFormationDetailUiEvent
    data object AddPostDismissed : TeamFormationDetailUiEvent
    data class AddPostTitleChanged(val value: String) : TeamFormationDetailUiEvent
    data class AddPostContentChanged(val value: String) : TeamFormationDetailUiEvent
    data object AddPostUploadClicked : TeamFormationDetailUiEvent
    data class AddPostAttachmentRemoved(val index: Int) : TeamFormationDetailUiEvent
    data object AddPostPublishClicked : TeamFormationDetailUiEvent
    data class LeaderClicked(val leaderId: Int) : TeamFormationDetailUiEvent
    data class ActivityClicked(val activityId: Int) : TeamFormationDetailUiEvent
}
