package com.example.uitvolunteermap.features.campaign.domain.usecase

import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignRepository
import javax.inject.Inject

class GetCampaignUseCase @Inject constructor(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(campaignId: Int) =
        repository.getCampaign(campaignId)
}
