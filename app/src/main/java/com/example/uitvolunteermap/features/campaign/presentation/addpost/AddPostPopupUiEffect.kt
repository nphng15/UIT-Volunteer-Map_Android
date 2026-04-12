package com.example.uitvolunteermap.features.campaign.presentation.addpost

sealed interface AddPostPopupUiEffect {
    data object NavigateBack : AddPostPopupUiEffect
    data class PostPublished(val message: String) : AddPostPopupUiEffect
    data class ShowMessage(val message: String) : AddPostPopupUiEffect
}
