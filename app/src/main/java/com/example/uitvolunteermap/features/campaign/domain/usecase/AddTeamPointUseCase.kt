package com.example.uitvolunteermap.features.campaign.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamVisitPoint
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignAreaRepository
import javax.inject.Inject

class AddTeamPointUseCase @Inject constructor(
    private val repository: CampaignAreaRepository
) {
    suspend operator fun invoke(
        campaignId: Int,
        teamId: Int,
        teamName: String,
        name: String,
        latitude: Double,
        longitude: Double
    ): AppResult<TeamVisitPoint> {
        if (teamId <= 0) {
            return AppResult.Error(AppError.Validation("Hãy chọn đội cho điểm này."))
        }
        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            return AppResult.Error(AppError.Validation("Toạ độ không hợp lệ."))
        }
        return repository.addManualPoint(
            campaignId = campaignId,
            teamId = teamId,
            teamName = teamName,
            name = name.trim().ifBlank { "Điểm hoạt động" },
            latitude = latitude,
            longitude = longitude
        )
    }
}
