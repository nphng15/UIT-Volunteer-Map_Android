package com.example.uitvolunteermap.features.attendance.domain.entity

data class TeamAttendance(
    val teamId: Int,
    val teamName: String,
    val campaignId: Int?,
    val campaignName: String?,
    val date: String,
    val members: List<MemberAttendance>
)
