package com.example.uitvolunteermap.features.home.data.repository

import com.example.uitvolunteermap.R
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
                appName = "UIT · Tình nguyện",
                stats = listOf(
                    VolunteerOverviewStat(value = "06", label = "Đang diễn ra"),
                    VolunteerOverviewStat(value = "03", label = "Sắp mở"),
                    VolunteerOverviewStat(value = "12", label = "Địa điểm")
                ),
                campaigns = listOf(
                    VolunteerCampaignSummary(
                        id = 1,
                        title = "Mùa Hè Xanh 2026",
                        dateRange = "01/06 - 30/08",
                        description = "Chiến dịch hè quy mô lớn với các đội hình an sinh, giáo dục, truyền thông và hỗ trợ cộng đồng.",
                        meta = "12 đội hình · 148 tình nguyện viên · 9 địa điểm",
                        primaryActionLabel = "Xem chiến dịch",
                        secondaryActionLabel = "Bản đồ",
                        accentColors = listOf(0xFFF7F1D8, 0xFFFFF4CC),
                        coverImageResId = R.drawable.banner_mxh
                    ),
                    VolunteerCampaignSummary(
                        id = 2,
                        title = "Xuân Tình Nguyện 2026",
                        dateRange = "05/01 - 25/01",
                        description = "Chiến dịch xuân tập trung vào chăm lo Tết, thăm mái ấm và mang hoạt động kết nối đến cộng đồng.",
                        meta = "08 đội hình · 96 tình nguyện viên · 6 điểm hoạt động",
                        primaryActionLabel = "Xem chiến dịch",
                        secondaryActionLabel = "Bản đồ",
                        accentColors = listOf(0xFFFFF3EC, 0xFFFFF8E6),
                        coverImageResId = R.drawable.banner_xtn
                    )
                )
            )
        )
    }
}
