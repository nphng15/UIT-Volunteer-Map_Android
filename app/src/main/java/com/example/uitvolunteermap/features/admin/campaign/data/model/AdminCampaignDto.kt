package com.example.uitvolunteermap.features.admin.campaign.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO khớp chính xác với contract thật của backend cho khu quản trị.
 * Khác với volunteer-side CampaignDto: bổ sung latitude/longitude/checkInRadius
 * (các trường vị trí điểm danh mà admin có quyền cấu hình).
 */
data class AdminCampaignDto(
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("campaignName") val campaignName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("checkInRadius") val checkInRadius: Double?
)
