package com.example.uitvolunteermap.features.attendance.data.mapper

import com.example.uitvolunteermap.features.attendance.data.model.ManagedTeamDto
import com.example.uitvolunteermap.features.attendance.data.model.MemberAttendanceDto
import com.example.uitvolunteermap.features.attendance.data.model.TeamAttendanceDto
import com.example.uitvolunteermap.features.attendance.domain.entity.ManagedTeam
import com.example.uitvolunteermap.features.attendance.domain.entity.MemberAttendance
import com.example.uitvolunteermap.features.attendance.domain.entity.TeamAttendance

fun TeamAttendanceDto.toDomain(): TeamAttendance = TeamAttendance(
    teamId = teamId,
    teamName = teamName,
    campaignId = campaignId,
    campaignName = campaignName,
    date = date,
    members = members.map { it.toDomain() }
)

fun MemberAttendanceDto.toDomain(): MemberAttendance = MemberAttendance(
    userId = userId,
    fullName = fullName,
    mssv = mssv,
    avatarUrl = avatarUrl,
    hasCheckedIn = hasCheckedIn,
    checkedInAt = checkedInAt,
    distance = distance,
    imageUrl = imageUrl
)

fun ManagedTeamDto.toDomain(): ManagedTeam = ManagedTeam(
    teamId = teamId,
    teamName = teamName,
    campaignId = campaignId,
    campaignName = campaignName
)
