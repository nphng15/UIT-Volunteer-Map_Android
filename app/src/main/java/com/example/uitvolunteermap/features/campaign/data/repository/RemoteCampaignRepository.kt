package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.campaign.data.datasource.CampaignApiService
import com.example.uitvolunteermap.features.campaign.data.datasource.CreateCampaignRequest
import com.example.uitvolunteermap.features.campaign.data.datasource.UpdateCampaignRequest
import com.example.uitvolunteermap.features.campaign.data.mapper.toDomain
import com.example.uitvolunteermap.features.campaign.domain.entity.Campaign
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignRepository
import javax.inject.Inject

class RemoteCampaignRepository @Inject constructor(
    private val campaignApiService: CampaignApiService
) : CampaignRepository {

    override suspend fun getCampaigns(): AppResult<List<Campaign>> = apiCall(
        request = { campaignApiService.getCampaigns() },
        map = { campaigns -> campaigns.map { it.toDomain() } }
    )

    override suspend fun getCampaign(campaignId: Int): AppResult<Campaign> = apiCall(
        request = { campaignApiService.getCampaign(campaignId) },
        map = { it.toDomain() }
    )

    override suspend fun createCampaign(
        campaignName: String,
        description: String?,
        startDate: String,
        endDate: String
    ): AppResult<Campaign> = apiCall(
        request = {
            campaignApiService.createCampaign(
                CreateCampaignRequest(
                    campaignName = campaignName,
                    description = description,
                    startDate = startDate,
                    endDate = endDate
                )
            )
        },
        map = { it.toDomain() }
    )

    override suspend fun updateCampaign(
        campaignId: Int,
        campaignName: String?,
        description: String?,
        startDate: String?,
        endDate: String?
    ): AppResult<Campaign> = apiCall(
        request = {
            campaignApiService.updateCampaign(
                campaignId = campaignId,
                body = UpdateCampaignRequest(
                    campaignName = campaignName,
                    description = description,
                    startDate = startDate,
                    endDate = endDate
                )
            )
        },
        map = { it.toDomain() }
    )

    override suspend fun deleteCampaign(campaignId: Int): AppResult<Unit> = apiCall(
        request = { campaignApiService.deleteCampaign(campaignId) },
        map = { Unit }
    )
}
