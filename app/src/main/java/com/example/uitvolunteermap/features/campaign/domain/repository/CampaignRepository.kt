package com.example.uitvolunteermap.features.campaign.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.model.Campaign

interface CampaignRepository {
    suspend fun getCampaigns(): AppResult<List<Campaign>>
    suspend fun getCampaign(campaignId: Int): AppResult<Campaign>
    suspend fun createCampaign(
        campaignName: String,
        description: String?,
        startDate: String,
        endDate: String
    ): AppResult<Campaign>
    suspend fun updateCampaign(
        campaignId: Int,
        campaignName: String? = null,
        description: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): AppResult<Campaign>
    suspend fun deleteCampaign(campaignId: Int): AppResult<Unit>
}
