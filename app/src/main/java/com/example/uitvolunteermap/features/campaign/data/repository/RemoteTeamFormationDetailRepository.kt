package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.campaign.data.datasource.TeamApiService
import com.example.uitvolunteermap.features.campaign.data.mapper.toTeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.repository.TeamFormationDetailRepository
import com.example.uitvolunteermap.features.post.data.remote.PostApiService
import javax.inject.Inject
import timber.log.Timber

class RemoteTeamFormationDetailRepository @Inject constructor(
    private val teamApiService: TeamApiService,
    private val postApiService: PostApiService
) : TeamFormationDetailRepository {

    override suspend fun getTeamFormationDetail(teamId: Int): AppResult<TeamFormationDetail> = apiCall(
        request = { teamApiService.getTeam(teamId) },
        map = { team ->
            val attachments = runCatching { teamApiService.getTeamAttachments(teamId) }
                .onFailure { Timber.w(it, "Failed to load team attachments for teamId=$teamId") }
                .getOrNull()
                ?.data
            val posts = runCatching { postApiService.getPosts() }
                .onFailure { Timber.w(it, "Failed to load posts for teamId=$teamId") }
                .getOrNull()
                ?.data.orEmpty()
                .filter { it.team?.teamId == teamId }
            team.toTeamFormationDetail(attachments, posts)
        }
    )
}
