package com.example.uitvolunteermap.features.home.data.remote

import com.example.uitvolunteermap.features.home.data.repository.CampaignResponse
import retrofit2.http.GET

interface CampaignApi {

    @GET("campaigns")
    suspend fun getCampaigns(): CampaignResponse
}