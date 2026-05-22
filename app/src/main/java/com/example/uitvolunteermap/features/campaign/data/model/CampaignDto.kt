package com.example.uitvolunteermap.features.campaign.data.model

import com.google.gson.annotations.SerializedName

data class CampaignDto(
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("campaignName") val campaignName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String
)

data class CampaignApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?
)
