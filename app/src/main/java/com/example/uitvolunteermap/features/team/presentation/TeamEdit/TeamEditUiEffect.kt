package com.example.uitvolunteermap.features.team.presentation.TeamEdit

sealed class TeamEditUiEffect {
    object NavigateBack : TeamEditUiEffect()
    data class ShowToast(val message: String) : TeamEditUiEffect()
}