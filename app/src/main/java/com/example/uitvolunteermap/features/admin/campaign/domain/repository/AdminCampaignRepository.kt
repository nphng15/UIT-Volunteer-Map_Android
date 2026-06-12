package com.example.uitvolunteermap.features.admin.campaign.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.campaign.domain.entity.AdminCampaign

interface AdminCampaignRepository {
    suspend fun getCampaigns(): AppResult<List<AdminCampaign>>
    suspend fun getCampaign(campaignId: Int): AppResult<AdminCampaign>
    suspend fun createCampaign(
        campaignName: String,
        description: String?,
        startDate: String,
        endDate: String
    ): AppResult<AdminCampaign>
    suspend fun updateCampaign(
        campaignId: Int,
        campaignName: String? = null,
        description: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        checkInRadius: Double? = null
    ): AppResult<AdminCampaign>
    suspend fun deleteCampaign(campaignId: Int): AppResult<Unit>
}
