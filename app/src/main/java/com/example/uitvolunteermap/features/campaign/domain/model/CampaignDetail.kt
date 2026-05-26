package com.example.uitvolunteermap.features.campaign.domain.model

data class CampaignDetail(
    val id: Int,
    val appName: String,
    val title: String,
    val schedule: String,
    val heroHeadline: String,
    val heroSupportingText: String,
    val stats: List<CampaignDetailStat>,
    val description: String,
    val teamSectionTitle: String,
    val teams: List<CampaignDetailTeam>,
    val posts: List<CampaignDetailPost>,
    val mapOverview: CampaignMapOverview
)
