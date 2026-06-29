package com.example.uitvolunteermap.features.campaign.presentation.areamap

import com.example.uitvolunteermap.features.campaign.domain.entity.TeamVisitPoint

data class TeamOption(val id: Int, val name: String)

data class LatLng(val latitude: Double, val longitude: Double)

data class CampaignAreaMapUiState(
    val campaignTitle: String = "",
    val teams: List<TeamOption> = emptyList(),
    val checkInTeam: TeamOption? = null,
    val points: List<TeamVisitPoint> = emptyList(),
    val isLoading: Boolean = false,
    val canMark: Boolean = false,
    val canSelectAnyCheckInTeam: Boolean = false,
    /** GPS captured for a manual mark, waiting for the leader to pick team + name. */
    val pendingLocation: LatLng? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val pointCount: Int get() = points.size
}
