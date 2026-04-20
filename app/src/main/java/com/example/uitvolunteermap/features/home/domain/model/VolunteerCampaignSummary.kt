package com.example.uitvolunteermap.features.home.domain.model

data class VolunteerCampaignSummary(
    val id: Int,
    val title: String,
    val dateRange: String,
    val description: String,
    val meta: String,
    val primaryActionLabel: String,
    val secondaryActionLabel: String,
    val accentColors: List<Long>,
    val coverImageResId: Int = 0
)
