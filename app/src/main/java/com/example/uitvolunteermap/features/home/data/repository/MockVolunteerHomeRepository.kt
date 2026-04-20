package com.example.uitvolunteermap.features.home.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.home.domain.model.VolunteerCampaignSummary
import com.example.uitvolunteermap.features.home.domain.model.VolunteerHomeContent
import com.example.uitvolunteermap.features.home.domain.model.VolunteerOverviewStat
import com.example.uitvolunteermap.features.home.domain.repository.VolunteerHomeRepository
import javax.inject.Inject

class MockVolunteerHomeRepository @Inject constructor() : VolunteerHomeRepository {

    override suspend fun getVolunteerHomeContent(): AppResult<VolunteerHomeContent> {
        // TODO: Replace this mock data source once the volunteer home API flow is available.
        // The current backend docs expose campaign endpoints, but this dashboard still needs
        // a stable aggregation contract for stats, locations, and post counts.
        return AppResult.Success(
            VolunteerHomeContent(
                appName = "UIT Volunteer Map",
                stats = listOf(
                    VolunteerOverviewStat(value = "06", label = "Dang dien ra"),
                    VolunteerOverviewStat(value = "03", label = "Sap mo"),
                    VolunteerOverviewStat(value = "12", label = "Dia diem")
                ),
                campaigns = listOf(
                    VolunteerCampaignSummary(
                        id = 1,
                        title = "Mua He Xanh 2026",
                        dateRange = "10/06 - 28/07  -  Thu Duc va cac tinh lan can",
                        description = "Chien dich he quy mo lon voi cac doi hinh ho tro cong dong, giao duc va moi truong.",
                        meta = "12 doi  -  48 bai viet  -  5 dia diem chinh",
                        primaryActionLabel = "Xem chi tiet",
                        secondaryActionLabel = "Xem ban do",
                        accentColors = listOf(0xFFF7F1D8, 0xFFFFF4CC)
                    ),
                    VolunteerCampaignSummary(
                        id = 2,
                        title = "Tiep suc mua thi",
                        dateRange = "05/06 - 25/06  -  Khu vuc cac diem thi",
                        description = "Ho tro thi sinh, dieu phoi tinh nguyen vien va cap nhat bai viet theo tung cum dia diem.",
                        meta = "08 doi  -  22 bai viet  -  9 diem tiep suc",
                        primaryActionLabel = "Xem chi tiet",
                        secondaryActionLabel = "Xem ban do",
                        accentColors = listOf(0xFFFFFDF9, 0xFFF1F3F7)
                    )
                )
            )
        )
    }
}
