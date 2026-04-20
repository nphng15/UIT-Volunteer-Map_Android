package com.example.uitvolunteermap.features.campaign.domain.entity

data class CampaignMapOverview(
    val selectedArea: String,
    val headerTitle: String,
    val footerTitle: String,
    val footerDescription: String,
    val ctaLabel: String,
    val locations: List<CampaignMapLocation>
)
