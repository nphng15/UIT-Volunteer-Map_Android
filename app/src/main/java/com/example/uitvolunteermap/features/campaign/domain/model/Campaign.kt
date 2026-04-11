package com.example.uitvolunteermap.features.campaign.domain.model

data class Campaign(
    val campaignId: Int,
    val campaignName: String,
    val description: String?,
    val startDate: String,
    val endDate: String
)
