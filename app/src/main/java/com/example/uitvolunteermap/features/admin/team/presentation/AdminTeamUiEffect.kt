package com.example.uitvolunteermap.features.admin.team.presentation

sealed interface AdminTeamUiEffect {
    data class ShowMessage(val message: String) : AdminTeamUiEffect
}
