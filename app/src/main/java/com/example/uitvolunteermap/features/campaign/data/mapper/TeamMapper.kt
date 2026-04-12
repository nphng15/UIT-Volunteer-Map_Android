package com.example.uitvolunteermap.features.campaign.data.mapper

// TODO: Uncomment khi TeamApiService được kích hoạt.

/*
import com.example.uitvolunteermap.features.campaign.data.model.TeamDto
import com.example.uitvolunteermap.features.campaign.data.model.TeamLeaderDto
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamLeader

// TeamDto (GET /teams/:id) → TeamFormationDetail
// Lưu ý: heroCards và activities không có trong API — giữ emptyList(),
// sẽ được bổ sung khi backend mở rộng contract hoặc lấy từ endpoint khác.
fun TeamDto.toDomain(): TeamFormationDetail = TeamFormationDetail(
    id = teamId,
    appName = "UIT Volunteer Map",
    appSubtitle = null,
    title = teamName,
    description = description ?: "",
    heroCards = emptyList(),
    leaders = leaders.map { it.toDomain() },
    activities = emptyList()
)

// TeamLeaderDto → TeamLeader
fun TeamLeaderDto.toDomain(): TeamLeader = TeamLeader(
    id = userId,
    initials = fullName.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }.takeLast(2).joinToString(""),
    role = role,
    name = fullName
)
*/
