package com.example.uitvolunteermap.features.checkin.data.remote

import com.google.gson.annotations.SerializedName

data class CheckinRequestDto(
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("imageUrl") val imageUrl: String? = null
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

data class UploadImageDto(
    @SerializedName("url") val url: String
)

data class MyCampaignDto(
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("campaignName") val campaignName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("checkInRadius") val checkInRadius: Double?,
    @SerializedName("teamCheckInLatitude") val teamCheckInLatitude: Double?,
    @SerializedName("teamCheckInLongitude") val teamCheckInLongitude: Double?,
    @SerializedName("teamCheckInRadius") val teamCheckInRadius: Double?,
    @SerializedName("teamId") val teamId: Int?,
    @SerializedName("teamName") val teamName: String?,
    @SerializedName("hasCheckedIn") val hasCheckedIn: Boolean,
    @SerializedName("checkedInAt") val checkedInAt: String?
)

data class CampaignPhotoDto(
    @SerializedName("campaignPhotoId") val campaignPhotoId: Int,
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("accId") val accId: Int,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("caption") val caption: String?,
    @SerializedName("isCheckinPhoto") val isCheckinPhoto: Int,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("author") val author: CampaignPhotoAuthorDto?
)

data class CampaignPhotoAuthorDto(
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("teamName") val teamName: String?
)

data class CampaignPhotoRequestDto(
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("caption") val caption: String? = null
)
