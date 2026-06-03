package com.example.uitvolunteermap.features.attendance.domain.entity

data class ManagedTeam(
    val teamId: Int,
    val teamName: String,
    val campaignId: Int?,
    val campaignName: String?
)
