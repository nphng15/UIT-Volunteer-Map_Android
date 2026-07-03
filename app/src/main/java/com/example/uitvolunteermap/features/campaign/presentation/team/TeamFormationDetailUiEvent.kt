package com.example.uitvolunteermap.features.campaign.presentation.team

import android.net.Uri
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionMode

sealed interface TeamFormationDetailUiEvent {
    data object RefreshRequested : TeamFormationDetailUiEvent
    data object BackClicked : TeamFormationDetailUiEvent
    data object HeroEditClicked : TeamFormationDetailUiEvent
    data object AddHeroImageDismissed : TeamFormationDetailUiEvent
    data class HeroImageUrlChanged(val value: String) : TeamFormationDetailUiEvent
    data object SubmitHeroImageClicked : TeamFormationDetailUiEvent
    data object AddActivityClicked : TeamFormationDetailUiEvent
    data object AddPostDismissed : TeamFormationDetailUiEvent
    data class AddPostTitleChanged(val value: String) : TeamFormationDetailUiEvent
    data class AddPostContentChanged(val value: String) : TeamFormationDetailUiEvent
    data object AddPostUploadClicked : TeamFormationDetailUiEvent
    data class AddPostImagesPicked(val uris: List<Uri>) : TeamFormationDetailUiEvent
    data class AddPostAttachmentRemoved(val index: Int) : TeamFormationDetailUiEvent
    data object AddPostRegenerateCaptionClicked : TeamFormationDetailUiEvent
    data object AddPostAcceptSuggestionClicked : TeamFormationDetailUiEvent
    data class AddPostCampaignNameChanged(val value: String) : TeamFormationDetailUiEvent
    data class AddPostCaptionModeChanged(val mode: CaptionMode) : TeamFormationDetailUiEvent
    data object AddPostPublishClicked : TeamFormationDetailUiEvent
    data class LeaderClicked(val leaderId: Int) : TeamFormationDetailUiEvent
    data class ActivityClicked(val activityId: Int) : TeamFormationDetailUiEvent
}
