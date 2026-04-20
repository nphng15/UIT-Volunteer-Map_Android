package com.example.uitvolunteermap.features.campaign.domain.entity

data class TeamFormationDetail(
    val id: Int,
    val appName: String,
    val appSubtitle: String?,
    val title: String,
    val description: String,
    val heroCards: List<TeamHeroCard>,
    val leaders: List<TeamLeader>,
    val activities: List<TeamActivityItem>
)
