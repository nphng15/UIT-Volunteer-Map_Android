package com.example.uitvolunteermap.features.campaign.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamFormationDetail

interface TeamFormationDetailRepository {
    suspend fun getTeamFormationDetail(teamId: Int): AppResult<TeamFormationDetail>
}
