package com.example.uitvolunteermap.features.checkin.data.remote

import com.google.gson.annotations.SerializedName

data class CheckinRequestDto(
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

data class CheckinResponseDto(
    @SerializedName("checkInId") val checkInId: Int,
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("accId") val accId: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("distance") val distance: Double,
    @SerializedName("checkedInAt") val checkedInAt: String
)

data class CheckinHistoryItemDto(
    @SerializedName("checkInId") val checkInId: Int,
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("accId") val accId: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("distance") val distance: Double,
    @SerializedName("checkedInAt") val checkedInAt: String,
    @SerializedName("campaign") val campaign: CheckinCampaignDto?
)

data class CheckinCampaignDto(
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("campaignName") val campaignName: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("description") val description: String?,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("checkInRadius") val checkInRadius: Double?
)
