package com.example.uitvolunteermap.features.admin.campaign.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.core.network.apiCallUnit
import com.example.uitvolunteermap.features.admin.campaign.data.datasource.AdminCampaignApiService
import com.example.uitvolunteermap.features.admin.campaign.data.datasource.CreateAdminCampaignRequest
import com.example.uitvolunteermap.features.admin.campaign.data.datasource.UpdateAdminCampaignRequest
import com.example.uitvolunteermap.features.admin.campaign.data.mapper.toDomain
import com.example.uitvolunteermap.features.admin.campaign.domain.entity.AdminCampaign
import com.example.uitvolunteermap.features.admin.campaign.domain.repository.AdminCampaignRepository
import javax.inject.Inject

class RemoteAdminCampaignRepository @Inject constructor(
    private val api: AdminCampaignApiService
) : AdminCampaignRepository {

    override suspend fun getCampaigns(): AppResult<List<AdminCampaign>> = apiCall(
        request = { api.getCampaigns() },
        map = { campaigns -> campaigns.map { it.toDomain() } }
    )

    override suspend fun getCampaign(campaignId: Int): AppResult<AdminCampaign> = apiCall(
        request = { api.getCampaign(campaignId) },
        map = { it.toDomain() }
    )

    override suspend fun createCampaign(
        campaignName: String,
        description: String?,
        startDate: String,
        endDate: String
    ): AppResult<AdminCampaign> = apiCall(
        request = {
            api.createCampaign(
                CreateAdminCampaignRequest(
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
        endDate: String?,
        latitude: Double?,
        longitude: Double?,
        checkInRadius: Double?
    ): AppResult<AdminCampaign> = apiCall(
        request = {
            api.updateCampaign(
                campaignId = campaignId,
                body = UpdateAdminCampaignRequest(
                    campaignName = campaignName,
                    description = description,
                    startDate = startDate,
                    endDate = endDate,
                    latitude = latitude,
                    longitude = longitude,
                    checkInRadius = checkInRadius
                )
            )
        },
        map = { it.toDomain() }
    )

    override suspend fun deleteCampaign(campaignId: Int): AppResult<Unit> = apiCallUnit(
        request = { api.deleteCampaign(campaignId) }
    )
}
