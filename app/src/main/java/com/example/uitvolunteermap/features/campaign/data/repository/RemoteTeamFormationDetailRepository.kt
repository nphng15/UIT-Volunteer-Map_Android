package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.campaign.data.datasource.TeamApiService
import com.example.uitvolunteermap.features.campaign.data.mapper.toTeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.repository.TeamFormationDetailRepository
import javax.inject.Inject
import timber.log.Timber

class RemoteTeamFormationDetailRepository @Inject constructor(
    private val teamApiService: TeamApiService
) : TeamFormationDetailRepository {

    override suspend fun getTeamFormationDetail(teamId: Int): AppResult<TeamFormationDetail> = apiCall(
        request = { teamApiService.getTeam(teamId) },
        map = { team ->
            val attachments = runCatching { teamApiService.getTeamAttachments(teamId) }
                .onFailure { Timber.w(it, "Failed to load team attachments for teamId=$teamId") }
                .getOrNull()
                ?.data
            team.toTeamFormationDetail(attachments)
        }
    )
}
