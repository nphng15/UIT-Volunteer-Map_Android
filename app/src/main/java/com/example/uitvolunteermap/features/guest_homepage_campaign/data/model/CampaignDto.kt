package com.example.uitvolunteermap.features.guest_homepage_campaign.data.model

data class CampaignDto(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val teams: List<String>,
    val activities: List<String>
)