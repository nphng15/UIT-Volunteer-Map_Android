package com.example.uitvolunteermap.features.team.presentation.TeamList

sealed class TeamListUiEvent {
    data class OnTeamClicked(val teamId: Int) : TeamListUiEvent()
    object OnRefresh : TeamListUiEvent()
}