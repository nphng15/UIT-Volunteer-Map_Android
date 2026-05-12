package com.example.uitvolunteermap.features.admin.campaign.domain.entity

data class AdminCampaign(
    val campaignId: Int,
    val campaignName: String,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val latitude: Double?,
    val longitude: Double?,
    val checkInRadius: Double?
)
