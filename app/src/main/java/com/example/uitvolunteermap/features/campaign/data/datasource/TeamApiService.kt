package com.example.uitvolunteermap.features.campaign.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.campaign.data.model.TeamAttachmentsDto
import com.example.uitvolunteermap.features.campaign.data.model.TeamDto
import com.example.uitvolunteermap.features.campaign.data.model.TeamListItemDto
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    @PATCH("teams/{id}/check-in-location")
    suspend fun updateTeamCheckInLocation(
        @Path("id") teamId: Int,
        @Body body: UpdateTeamCheckInLocationRequest
    ): ApiEnvelope<JsonElement>
}

data class UpdateTeamCheckInLocationRequest(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("radius") val radius: Double
)
