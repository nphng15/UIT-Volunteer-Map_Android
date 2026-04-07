package com.example.uitvolunteermap.features.campaign.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.model.CampaignDetail

interface CampaignDetailRepository {
    suspend fun getCampaignDetail(campaignId: Int): AppResult<CampaignDetail>
}
