package com.example.uitvolunteermap.features.campaign.domain.model

data class CampaignMapLocation(
    val id: Int,
    val label: String,
    val supportingText: String,
    val xFraction: Float,
    val yFraction: Float,
    val isHighlighted: Boolean
)
