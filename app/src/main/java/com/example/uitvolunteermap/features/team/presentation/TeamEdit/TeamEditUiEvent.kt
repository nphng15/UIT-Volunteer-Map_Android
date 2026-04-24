package com.example.uitvolunteermap.features.team.presentation.TeamEdit

sealed class TeamEditUiEvent {
    data class OnNameChanged(val newName: String) : TeamEditUiEvent()
    data class OnDescriptionChanged(val newDesc: String) : TeamEditUiEvent()
    data class OnImageUrlChanged(val newUrl: String?) : TeamEditUiEvent()
    object OnSaveClicked : TeamEditUiEvent()
    object OnBackClicked : TeamEditUiEvent()
    object OnEditAttachmentsClicked : TeamEditUiEvent()
}