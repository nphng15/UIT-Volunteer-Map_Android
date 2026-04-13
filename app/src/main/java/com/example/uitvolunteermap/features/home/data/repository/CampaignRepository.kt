package com.example.uitvolunteermap.features.home.data.repository

import javax.inject.Inject

// 🔹 DTO trả về từ API
data class CampaignDto(
    val campaignId: Int,
    val campaignName: String,
    val description: String,
    val startDate: String,
    val endDate: String
)

// 🔹 Response wrapper
data class CampaignResponse(
    val success: Boolean,
    val data: List<CampaignDto>
)

class CampaignRepository @Inject constructor() {

    // Fake API GET /campaigns
    suspend fun getCampaigns(): CampaignResponse {
        return CampaignResponse(
            success = true,
            data = listOf(
                CampaignDto(
                    campaignId = 1,
                    campaignName = "Xuân Tình Nguyện 1",
                    description = "campaign 1",
                    startDate = "2026-06-01",
                    endDate = "2026-07-15"
                ),
                CampaignDto(
                    campaignId = 1,
                    campaignName = "Xuân Tình Nguyện 2",
                    description = "campaign 1",
                    startDate = "2026-06-01",
                    endDate = "2026-07-15"
                ),
                CampaignDto(
                    campaignId = 1,
                    campaignName = "Xuân Tình Nguyện 3",
                    description = "campaign 1",
                    startDate = "2026-06-01",
                    endDate = "2026-07-15"
                ),
                CampaignDto(
                    campaignId = 1,
                    campaignName = "Xuân Tình Nguyện 3",
                    description = "campaign 1",
                    startDate = "2026-06-01",
                    endDate = "2026-07-15"
                ),
                CampaignDto(
                    campaignId = 2,
                    campaignName = "Mùa Hè Xanh",
                    description = "campaign 2",
                    startDate = "2026-07-16",
                    endDate = "2026-08-01"
                )
            )
        )
    }
}