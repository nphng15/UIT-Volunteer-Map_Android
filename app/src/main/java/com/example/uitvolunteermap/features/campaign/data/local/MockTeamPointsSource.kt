package com.example.uitvolunteermap.features.campaign.data.local

import com.example.uitvolunteermap.features.campaign.domain.entity.TeamPointSource
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamVisitPoint
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeded demo activity points per team, around Ho Chi Minh City / Thu Duc.
 * The backend doesn't expose campaign coordinates yet, so these illustrate
 * "where each team has been" on a real map. Replace with API data later.
 */
@Singleton
class MockTeamPointsSource @Inject constructor() {

    private data class Seed(
        val teamId: Int,
        val teamName: String,
        val name: String,
        val lat: Double,
        val lng: Double
    )

    private val seeds = listOf(
        // Đội nấu cơm
        Seed(101, "Đội nấu cơm", "UIT - Bếp chính", 10.8700, 106.8030),
        Seed(101, "Đội nấu cơm", "Làng Đại học", 10.8760, 106.7980),
        Seed(101, "Đội nấu cơm", "Chợ Thủ Đức", 10.8499, 106.7537),
        // Đội giáo dục
        Seed(102, "Đội giáo dục", "TH Linh Trung", 10.8665, 106.8090),
        Seed(102, "Đội giáo dục", "Nhà thiếu nhi Thủ Đức", 10.8492, 106.7553),
        Seed(102, "Đội giáo dục", "Mái ấm Quận 1", 10.7769, 106.7009),
        // Đội truyền thông
        Seed(103, "Đội truyền thông", "Hội trường UIT", 10.8688, 106.8035),
        Seed(103, "Đội truyền thông", "Công viên Lê Văn Tám", 10.7905, 106.6975),
        Seed(103, "Đội truyền thông", "SVĐ Thống Nhất", 10.7626, 106.6779)
    )

    fun pointsFor(campaignId: Int): List<TeamVisitPoint> =
        seeds.mapIndexed { index, seed ->
            TeamVisitPoint(
                id = "mock-$campaignId-${seed.teamId}-$index",
                campaignId = campaignId,
                teamId = seed.teamId,
                teamName = seed.teamName,
                name = seed.name,
                latitude = seed.lat,
                longitude = seed.lng,
                source = TeamPointSource.MOCK,
                createdAt = "2026-06-20T08:00:00.000Z"
            )
        }
}
