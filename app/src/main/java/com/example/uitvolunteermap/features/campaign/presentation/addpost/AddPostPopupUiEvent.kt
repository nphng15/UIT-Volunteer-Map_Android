package com.example.uitvolunteermap.features.campaign.presentation.addpost

import android.net.Uri
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionMode

sealed interface AddPostPopupUiEvent {
    data object CloseClicked : AddPostPopupUiEvent
    data class TitleChanged(val value: String) : AddPostPopupUiEvent
    data class ContentChanged(val value: String) : AddPostPopupUiEvent
    data object UploadClicked : AddPostPopupUiEvent
    data class ImagesPicked(val uris: List<Uri>) : AddPostPopupUiEvent
    data class RemovePickedImageClicked(val index: Int) : AddPostPopupUiEvent
    data object RegenerateCaptionClicked : AddPostPopupUiEvent
    data object AcceptSuggestionClicked : AddPostPopupUiEvent
    data class CampaignNameChanged(val value: String) : AddPostPopupUiEvent
    data class CaptionModeChanged(val mode: CaptionMode) : AddPostPopupUiEvent
    data object PublishClicked : AddPostPopupUiEvent
}
