package com.example.uitvolunteermap.features.admin.campaign.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.admin.campaign.data.model.AdminCampaignDto
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * API service riêng cho khu quản trị chiến dịch. Tách khỏi volunteer-side
 * CampaignApiService vì request DTO ở đây chứa đầy đủ 7 trường (bao gồm
 * latitude/longitude/checkInRadius) mà bản volunteer còn thiếu.
 */
interface AdminCampaignApiService {

    @GET("campaigns")
    suspend fun getCampaigns(): ApiEnvelope<List<AdminCampaignDto>>

    @GET("campaigns/{id}")
    suspend fun getCampaign(
        @Path("id") campaignId: Int
    ): ApiEnvelope<AdminCampaignDto>

    @POST("campaigns")
    suspend fun createCampaign(
        @Body body: CreateAdminCampaignRequest
    ): ApiEnvelope<AdminCampaignDto>

    @PUT("campaigns/{id}")
    suspend fun updateCampaign(
        @Path("id") campaignId: Int,
        @Body body: UpdateAdminCampaignRequest
    ): ApiEnvelope<AdminCampaignDto>

    @DELETE("campaigns/{id}")
    suspend fun deleteCampaign(
        @Path("id") campaignId: Int
    ): ApiEnvelope<Unit>
}

data class CreateAdminCampaignRequest(
    @SerializedName("campaignName") val campaignName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("checkInRadius") val checkInRadius: Double?
)

data class UpdateAdminCampaignRequest(
    @SerializedName("campaignName") val campaignName: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("checkInRadius") val checkInRadius: Double? = null
)
