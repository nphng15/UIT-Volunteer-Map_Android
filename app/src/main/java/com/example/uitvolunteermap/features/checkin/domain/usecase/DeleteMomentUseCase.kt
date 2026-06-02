package com.example.uitvolunteermap.features.checkin.domain.usecase

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.checkin.domain.repository.CheckinRepository
import javax.inject.Inject

class DeleteMomentUseCase @Inject constructor(
    private val repository: CheckinRepository
) {
    suspend operator fun invoke(campaignId: Int, momentId: Int): AppResult<Unit> =
        repository.deleteMoment(campaignId, momentId)
}
