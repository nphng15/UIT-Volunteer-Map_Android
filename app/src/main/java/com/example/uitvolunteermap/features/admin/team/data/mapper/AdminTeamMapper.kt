package com.example.uitvolunteermap.features.admin.team.data.mapper

import com.example.uitvolunteermap.features.admin.team.data.model.AdminTeamDetailDto
import com.example.uitvolunteermap.features.admin.team.data.model.AdminTeamLeaderDto
import com.example.uitvolunteermap.features.admin.team.data.model.AdminTeamListItemDto
import com.example.uitvolunteermap.features.admin.team.data.model.AdminTeamMemberDto
import com.example.uitvolunteermap.features.admin.team.domain.entity.AdminTeam
import com.example.uitvolunteermap.features.admin.team.domain.entity.AdminTeamLeader
import com.example.uitvolunteermap.features.admin.team.domain.entity.AdminTeamMember

fun AdminTeamListItemDto.toDomain(): AdminTeam = AdminTeam(
    teamId = teamId,
    teamName = teamName,
    description = description,
    imageUrl = imageUrl,
    checkInLatitude = checkInLatitude,
    checkInLongitude = checkInLongitude,
    checkInRadius = checkInRadius,
    leaders = leaders.orEmpty().map { it.toDomain() },
    members = members.orEmpty().map { it.toDomain() }
)

// GET /teams/{id} không trả về members → danh sách thành viên rỗng.
fun AdminTeamDetailDto.toDomain(): AdminTeam = AdminTeam(
    teamId = teamId,
    teamName = teamName,
    description = description,
    imageUrl = imageUrl,
    checkInLatitude = checkInLatitude,
    checkInLongitude = checkInLongitude,
    checkInRadius = checkInRadius,
    leaders = leaders.orEmpty().map { it.toDomain() },
    members = emptyList()
)

fun AdminTeamLeaderDto.toDomain(): AdminTeamLeader = AdminTeamLeader(
    userId = userId,
    fullName = fullName,
    role = role,
    avatarUrl = avatarUrl
)

fun AdminTeamMemberDto.toDomain(): AdminTeamMember = AdminTeamMember(
    userId = userId,
    fullName = fullName
)
