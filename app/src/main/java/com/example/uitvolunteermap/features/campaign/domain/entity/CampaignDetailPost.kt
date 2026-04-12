package com.example.uitvolunteermap.features.campaign.domain.entity

data class CampaignDetailPost(
    val id: Int,
    val teamName: String,
    val title: String,
    val publishedAt: String,
    val summary: String,
    val accentColors: List<Long>,
    val isLightBadge: Boolean
)
