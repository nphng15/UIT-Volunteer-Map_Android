package com.example.uitvolunteermap.features.post.presentation.addpost

import android.net.Uri

sealed interface AddPostPopupUiEvent {
    data object CloseClicked : AddPostPopupUiEvent
    data class TitleChanged(val value: String) : AddPostPopupUiEvent
    data class ContentChanged(val value: String) : AddPostPopupUiEvent
    data object UploadClicked : AddPostPopupUiEvent
    data class ImagesPicked(val uris: List<Uri>) : AddPostPopupUiEvent
    data class RemoveAttachmentClicked(val index: Int) : AddPostPopupUiEvent
    data object RegenerateCaptionClicked : AddPostPopupUiEvent
    data object AcceptSuggestionClicked : AddPostPopupUiEvent
    data object PublishClicked : AddPostPopupUiEvent
}
