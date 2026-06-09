package com.example.uitvolunteermap.features.campaign.domain.entity

/** Where a team activity point came from. */
enum class TeamPointSource {
    /** Seeded demo data (backend has no coordinates yet). */
    MOCK,

    /** Marked manually by a team leader. */
    MANUAL
}

/** A real geo point where a team operated during a campaign. */
data class TeamVisitPoint(
    val id: String,
    val campaignId: Int,
    val teamId: Int,
    val teamName: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val source: TeamPointSource,
    val createdAt: String
)
