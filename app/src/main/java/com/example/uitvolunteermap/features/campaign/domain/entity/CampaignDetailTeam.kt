package com.example.uitvolunteermap.features.campaign.domain.entity

data class CampaignDetailTeam(
    val id: Int,
    val name: String,
    val shortName: String,
    val accentColors: List<Long>,
    val previewImageResId: Int = 0
)
