package com.example.uitvolunteermap.features.campaign.domain.usecase

import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignDetailRepository
import javax.inject.Inject

class GetCampaignDetailUseCase @Inject constructor(
    private val campaignDetailRepository: CampaignDetailRepository
) {
    suspend operator fun invoke(campaignId: Int) =
        campaignDetailRepository.getCampaignDetail(campaignId)
}
