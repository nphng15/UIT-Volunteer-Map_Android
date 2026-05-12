package com.example.uitvolunteermap.features.admin.campaign.domain.usecase

import com.example.uitvolunteermap.features.admin.campaign.domain.repository.AdminCampaignRepository
import javax.inject.Inject

class GetAdminCampaignsUseCase @Inject constructor(
    private val repository: AdminCampaignRepository
) {
    suspend operator fun invoke() = repository.getCampaigns()
}
