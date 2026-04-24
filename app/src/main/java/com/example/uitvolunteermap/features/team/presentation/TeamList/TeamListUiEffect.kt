package com.example.uitvolunteermap.features.team.presentation.TeamList

sealed class TeamListUiEffect {
    data class NavigateToDetail(val teamId: Int) : TeamListUiEffect()
}