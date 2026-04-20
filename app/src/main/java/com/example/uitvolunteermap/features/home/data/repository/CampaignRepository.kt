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
                    campaignName = "Mùa Hè Xanh 2026",
                    description = "Chiến dịch tình nguyện hè tại Linh Trung, Củ Chi và các địa bàn vùng ven.",
                    startDate = "2026-06-01",
                    endDate = "2026-08-30"
                ),
                CampaignDto(
                    campaignId = 2,
                    campaignName = "Xuân Tình Nguyện 2026",
                    description = "Chiến dịch xuân chăm lo cộng đồng, mái ấm và hoạt động an sinh dịp Tết.",
                    startDate = "2026-01-05",
                    endDate = "2026-01-25"
                )
            )
        )
    }
}
