package com.example.uitvolunteermap.features.home.domain.model

data class VolunteerHomeContent(
    val appName: String,
    val stats: List<VolunteerOverviewStat>,
    val campaigns: List<VolunteerCampaignSummary>
)
