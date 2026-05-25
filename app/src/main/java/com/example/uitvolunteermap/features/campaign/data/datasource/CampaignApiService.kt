package com.example.uitvolunteermap.features.campaign.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.campaign.data.model.CampaignDto
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CampaignApiService {

    @GET("campaigns")
    suspend fun getCampaigns(): ApiEnvelope<List<CampaignDto>>

    @GET("campaigns/{id}")
    suspend fun getCampaign(
        @Path("id") campaignId: Int
    ): ApiEnvelope<CampaignDto>

    @POST("campaigns")
    suspend fun createCampaign(
        @Body body: CreateCampaignRequest
    ): ApiEnvelope<CampaignDto>

    @PUT("campaigns/{id}")
    suspend fun updateCampaign(
        @Path("id") campaignId: Int,
        @Body body: UpdateCampaignRequest
    ): ApiEnvelope<CampaignDto>

    @DELETE("campaigns/{id}")
    suspend fun deleteCampaign(
        @Path("id") campaignId: Int
    ): ApiEnvelope<Unit>
}

data class CreateCampaignRequest(
    @SerializedName("campaignName") val campaignName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String
)

data class UpdateCampaignRequest(
    @SerializedName("campaignName") val campaignName: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null
)
