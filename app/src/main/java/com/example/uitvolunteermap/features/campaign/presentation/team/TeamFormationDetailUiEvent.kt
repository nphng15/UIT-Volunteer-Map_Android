package com.example.uitvolunteermap.features.campaign.presentation.team

sealed interface TeamFormationDetailUiEvent {
    data object RefreshRequested : TeamFormationDetailUiEvent
    data object BackClicked : TeamFormationDetailUiEvent
    data object HeroEditClicked : TeamFormationDetailUiEvent
    data object AddActivityClicked : TeamFormationDetailUiEvent
    data class LeaderClicked(val leaderId: Int) : TeamFormationDetailUiEvent
    data class ActivityClicked(val activityId: Int) : TeamFormationDetailUiEvent
}
