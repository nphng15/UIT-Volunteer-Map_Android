package com.example.uitvolunteermap.features.campaign.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.campaign.data.model.TeamAttachmentsDto
import com.example.uitvolunteermap.features.campaign.data.model.TeamDto
import com.example.uitvolunteermap.features.campaign.data.model.TeamListItemDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TeamApiService {

    @GET("teams")
    suspend fun getTeams(): ApiEnvelope<List<TeamListItemDto>>

    @GET("teams/{id}")
    suspend fun getTeam(
        @Path("id") teamId: Int
    ): ApiEnvelope<TeamDto>

    @GET("teams/{id}/attachments")
    suspend fun getTeamAttachments(
        @Path("id") teamId: Int
    ): ApiEnvelope<TeamAttachmentsDto>
}
