package com.example.uitvolunteermap.features.post.presentation.campaignposts

sealed interface CampaignPostsUiEvent {
    data object RefreshRequested : CampaignPostsUiEvent
    data object BackClicked : CampaignPostsUiEvent
    data class TeamFilterSelected(val teamId: Int?) : CampaignPostsUiEvent
    data class PostCardClicked(val postId: Int) : CampaignPostsUiEvent
    data class CreatePostClicked(val preselectedTeamId: Int? = null) : CampaignPostsUiEvent
    data class EditPostClicked(val postId: Int) : CampaignPostsUiEvent
    data class DeletePostClicked(val postId: Int) : CampaignPostsUiEvent
    data object DeleteDismissed : CampaignPostsUiEvent
    data object DeleteConfirmed : CampaignPostsUiEvent
    data object EditorDismissed : CampaignPostsUiEvent
    data class EditorTitleChanged(val value: String) : CampaignPostsUiEvent
    data class EditorContentChanged(val value: String) : CampaignPostsUiEvent
    data class EditorTeamChanged(val teamId: Int) : CampaignPostsUiEvent
    data class EditorAttachmentInputChanged(val value: String) : CampaignPostsUiEvent
    data object AddAttachmentClicked : CampaignPostsUiEvent
    data class RemoveAttachmentClicked(val index: Int) : CampaignPostsUiEvent
    data object SaveEditorClicked : CampaignPostsUiEvent
}
