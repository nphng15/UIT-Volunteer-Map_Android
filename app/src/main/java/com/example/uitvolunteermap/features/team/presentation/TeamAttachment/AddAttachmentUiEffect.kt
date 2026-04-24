package com.example.uitvolunteermap.features.team.presentation.addattachment

sealed class AddAttachmentUiEffect {
    object NavigateBack : AddAttachmentUiEffect()
    data class ShowError(val message: String) : AddAttachmentUiEffect()
}