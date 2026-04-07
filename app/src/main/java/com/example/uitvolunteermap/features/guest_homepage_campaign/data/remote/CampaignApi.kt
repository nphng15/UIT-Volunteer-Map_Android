package com.example.uitvolunteermap.features.guest_homepage_campaign.data.remote

import com.example.uitvolunteermap.features.guest_homepage_campaign.data.model.CampaignDto
import retrofit2.http.GET
import retrofit2.http.Header

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?
)

interface CampaignApi {

    @GET("campaigns")
    suspend fun getCampaigns(
        @Header("Authorization") token: String
    ): ApiResponse<List<CampaignDto>>
}