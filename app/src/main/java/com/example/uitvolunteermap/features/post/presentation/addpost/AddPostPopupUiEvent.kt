package com.example.uitvolunteermap.features.post.presentation.addpost

sealed interface AddPostPopupUiEvent {
    data object CloseClicked : AddPostPopupUiEvent
    data class TitleChanged(val value: String) : AddPostPopupUiEvent
    data class ContentChanged(val value: String) : AddPostPopupUiEvent
    data object UploadClicked : AddPostPopupUiEvent
    data class RemoveAttachmentClicked(val index: Int) : AddPostPopupUiEvent
    data object PublishClicked : AddPostPopupUiEvent
}
