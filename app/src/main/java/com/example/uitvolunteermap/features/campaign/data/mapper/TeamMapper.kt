package com.example.uitvolunteermap.features.campaign.data.mapper

import com.example.uitvolunteermap.features.campaign.data.model.TeamAttachmentsDto
import com.example.uitvolunteermap.features.campaign.data.model.TeamDto
import com.example.uitvolunteermap.features.campaign.data.model.TeamLeaderDto
import com.example.uitvolunteermap.features.campaign.data.model.TeamListItemDto
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetailTeam
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamActivityItem
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamHeroCard
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamLeader

fun TeamListItemDto.toCampaignDetailTeam(index: Int): CampaignDetailTeam = CampaignDetailTeam(
    id = teamId,
    name = teamName,
    shortName = teamName.toInitials(),
    accentColors = defaultAccentColors(index)
)

fun TeamDto.toTeamFormationDetail(attachments: TeamAttachmentsDto? = null): TeamFormationDetail {
    val activityItems = attachments?.attachments
        ?.sortedWith(compareBy({ it.position ?: Int.MAX_VALUE }, { it.attachmentId }))
        ?.map {
            TeamActivityItem(
                id = it.attachmentId,
                label = it.imageUrl.substringAfterLast('/').ifBlank { "Ảnh ${it.attachmentId}" },
                isAddButton = false
            )
        }
        .orEmpty()

    return TeamFormationDetail(
        id = teamId,
        appName = "UIT Volunteer Map",
        appSubtitle = "Dữ liệu từ backend",
        title = teamName,
        description = description.orEmpty(),
        heroCards = buildList {
            imageUrl?.let { add(TeamHeroCard(label = it.substringAfterLast('/'), isPrimary = true)) }
            attachments?.attachments?.take(2)?.forEach {
                add(TeamHeroCard(label = it.imageUrl.substringAfterLast('/'), isPrimary = false))
            }
        },
        leaders = leaders.map { it.toDomain() },
        activities = listOf(TeamActivityItem(id = 0, label = "+", isAddButton = true)) + activityItems
    )
}

fun TeamLeaderDto.toDomain(): TeamLeader = TeamLeader(
    id = userId,
    initials = fullName.toInitials(),
    role = role,
    name = fullName
)

private fun String.toInitials(): String {
    return split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .takeLast(2)
        .joinToString("")
        .ifBlank { take(2).uppercase() }
}

private fun defaultAccentColors(index: Int): List<Long> {
    val palettes = listOf(
        listOf(0xFF20303A, 0xFF6D839A),
        listOf(0xFF1C3977, 0xFF6B8FD6),
        listOf(0xFF22543D, 0xFF68A77B),
    )
    return palettes[index % palettes.size]
}
