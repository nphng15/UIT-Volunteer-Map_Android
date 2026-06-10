package com.example.uitvolunteermap.features.campaign.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignAreaRepository
import javax.inject.Inject

class UpdateTeamCheckInLocationUseCase @Inject constructor(
    private val repository: CampaignAreaRepository
) {
    suspend operator fun invoke(
        teamId: Int,
        latitude: Double,
        longitude: Double,
        radius: Double = 100.0
    ): AppResult<Unit> {
        if (teamId <= 0) {
            return AppResult.Error(AppError.Validation("Hãy chọn đội để cập nhật điểm check-in."))
        }
        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            return AppResult.Error(AppError.Validation("Toạ độ không hợp lệ."))
        }
        if (radius <= 0.0) {
            return AppResult.Error(AppError.Validation("Bán kính check-in không hợp lệ."))
        }
        return repository.updateTeamCheckInLocation(teamId, latitude, longitude, radius)
    }
}
