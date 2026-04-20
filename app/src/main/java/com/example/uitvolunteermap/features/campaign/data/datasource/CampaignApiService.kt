package com.example.uitvolunteermap.features.campaign.data.datasource

// TODO: Uncomment when Retrofit is wired up in DI and the backend is reachable.

/*
import com.example.uitvolunteermap.features.campaign.data.model.CampaignApiResponse
import com.example.uitvolunteermap.features.campaign.data.model.CampaignDto
import retrofit2.http.*

interface CampaignApiService {

    // GET /campaigns — requires Bearer token (admin or leader)
    @GET("campaigns")
    suspend fun getCampaigns(
        @Header("Authorization") token: String
    ): CampaignApiResponse<List<CampaignDto>>

    // GET /campaigns/:id
    @GET("campaigns/{id}")
    suspend fun getCampaign(
        @Header("Authorization") token: String,
        @Path("id") campaignId: Int
    ): CampaignApiResponse<CampaignDto>

    // POST /campaigns
    @POST("campaigns")
    suspend fun createCampaign(
        @Header("Authorization") token: String,
        @Body body: CreateCampaignRequest
    ): CampaignApiResponse<CampaignDto>

    // PUT /campaigns/:id — partial update supported
    @PUT("campaigns/{id}")
    suspend fun updateCampaign(
        @Header("Authorization") token: String,
        @Path("id") campaignId: Int,
        @Body body: UpdateCampaignRequest
    ): CampaignApiResponse<CampaignDto>

    // DELETE /campaigns/:id
    @DELETE("campaigns/{id}")
    suspend fun deleteCampaign(
        @Header("Authorization") token: String,
        @Path("id") campaignId: Int
    ): CampaignApiResponse<Unit>
}

data class CreateCampaignRequest(
    val campaignName: String,
    val description: String?,
    val startDate: String,
    val endDate: String
)

data class UpdateCampaignRequest(
    val campaignName: String? = null,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
)
*/
