package com.example.uitvolunteermap.features.checkin.domain.usecase

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.checkin.domain.entity.CampaignMoment
import com.example.uitvolunteermap.features.checkin.domain.repository.CheckinRepository
import javax.inject.Inject

class GetCampaignMomentsUseCase @Inject constructor(
    private val repository: CheckinRepository
) {
    suspend operator fun invoke(campaignId: Int): AppResult<List<CampaignMoment>> =
        repository.getCampaignMoments(campaignId)
}
