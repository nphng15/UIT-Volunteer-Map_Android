package com.example.uitvolunteermap.features.checkin.domain.usecase

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinResult
import com.example.uitvolunteermap.features.checkin.domain.repository.CheckinRepository
import javax.inject.Inject

class PerformCheckinUseCase @Inject constructor(
    private val repository: CheckinRepository
) {
    suspend operator fun invoke(
        campaignId: Int,
        latitude: Double,
        longitude: Double
    ): AppResult<CheckinResult> = repository.checkin(campaignId, latitude, longitude)
}
