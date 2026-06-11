package com.example.uitvolunteermap.features.campaign.presentation.areamap

import com.example.uitvolunteermap.features.campaign.domain.entity.TeamVisitPoint
import com.example.uitvolunteermap.features.checkin.domain.entity.MyCampaign

data class TeamOption(val id: Int, val name: String)

data class LatLng(val latitude: Double, val longitude: Double)

data class CampaignAreaMapUiState(
    val campaignTitle: String = "",
    val teams: List<TeamOption> = emptyList(),
    val checkInTeam: TeamOption? = null,
    val points: List<TeamVisitPoint> = emptyList(),
    val myCampaign: MyCampaign? = null,
    val isLoading: Boolean = false,
    val canMark: Boolean = false,
    val canSelectAnyCheckInTeam: Boolean = false,
    val isAdmin: Boolean = false,
    /** GPS or map-tap location waiting for the leader to pick team + name. */
    val pendingLocation: LatLng? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val pointCount: Int get() = points.size

    val selectableTeams: List<TeamOption>
        get() {
            if (isAdmin) return teams
            val myTeamId = myCampaign?.teamId ?: return teams
            return teams.filter { it.id == myTeamId }.ifEmpty { teams }
        }
}
