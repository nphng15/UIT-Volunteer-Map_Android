package com.example.uitvolunteermap.features.campaign.presentation.team

sealed interface TeamFormationDetailUiEffect {
    data object NavigateBack : TeamFormationDetailUiEffect
    data class NavigateToAddPostPopup(val teamId: Int) : TeamFormationDetailUiEffect
    data class NavigateToGpsCheckin(
        val campaignId: Int,
        val campaignName: String,
        val latitude: Double,
        val longitude: Double,
        val radius: Double
    ) : TeamFormationDetailUiEffect
    data class ShowMessage(val message: String) : TeamFormationDetailUiEffect
}
