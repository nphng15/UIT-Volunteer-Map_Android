package com.example.uitvolunteermap.features.campaign.domain.usecase

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignAreaRepository
import javax.inject.Inject

class RemoveTeamPointUseCase @Inject constructor(
    private val repository: CampaignAreaRepository
) {
    suspend operator fun invoke(id: String): AppResult<Unit> = repository.removeManualPoint(id)
}
