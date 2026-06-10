package com.example.uitvolunteermap.features.campaign.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamVisitPoint

interface CampaignAreaRepository {

    /** All activity points for a campaign (seeded demo + leader-marked), per team. */
    suspend fun getTeamPoints(campaignId: Int): AppResult<List<TeamVisitPoint>>

    suspend fun addManualPoint(
        campaignId: Int,
        teamId: Int,
        teamName: String,
        name: String,
        latitude: Double,
        longitude: Double
    ): AppResult<TeamVisitPoint>

    suspend fun updateTeamCheckInLocation(
        teamId: Int,
        latitude: Double,
        longitude: Double,
        radius: Double
    ): AppResult<Unit>

    suspend fun removeManualPoint(id: String): AppResult<Unit>
}
