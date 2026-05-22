package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.Campaign
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignRepository
import javax.inject.Inject

// TODO: Replace with CampaignRepositoryImpl once the backend is reachable.
// CampaignRepositoryImpl will inject CampaignApiService + token provider, call the API,
// map CampaignDto.toDomain() on success, and use toAppError() on exception.
class MockCampaignRepository @Inject constructor() : CampaignRepository {

    // Mutable in-memory list simulating backend state across calls
    private val store = mutableListOf(
        Campaign(
            campaignId = 1,
            campaignName = "Mua He Xanh 2026",
            description = "Tinh nguyen he phuc vu cong dong",
            startDate = "2026-06-01",
            endDate = "2026-07-15"
        ),
        Campaign(
            campaignId = 2,
            campaignName = "Tiep Suc Mua Thi 2026",
            description = "Ho tro thi sinh mua thi dai hoc",
            startDate = "2026-06-05",
            endDate = "2026-06-25"
        )
    )

    private var nextId = 3

    // Real: apiService.getCampaigns(token).data!!.map { it.toDomain() }
    override suspend fun getCampaigns(): AppResult<List<Campaign>> =
        AppResult.Success(store.toList())

    // Real: apiService.getCampaign(token, campaignId).data!!.toDomain()
    override suspend fun getCampaign(campaignId: Int): AppResult<Campaign> {
        val campaign = store.find { it.campaignId == campaignId }
            ?: return AppResult.Error(AppError.NotFound(message = "Khong tim thay chien dich."))
        return AppResult.Success(campaign)
    }

    // Real: apiService.createCampaign(token, CreateCampaignRequest(...)).data!!.toDomain()
    override suspend fun createCampaign(
        campaignName: String,
        description: String?,
        startDate: String,
        endDate: String
    ): AppResult<Campaign> {
        val new = Campaign(
            campaignId = nextId++,
            campaignName = campaignName,
            description = description,
            startDate = startDate,
            endDate = endDate
        )
        store.add(new)
        return AppResult.Success(new)
    }

    // Real: apiService.updateCampaign(token, campaignId, UpdateCampaignRequest(...)).data!!.toDomain()
    override suspend fun updateCampaign(
        campaignId: Int,
        campaignName: String?,
        description: String?,
        startDate: String?,
        endDate: String?
    ): AppResult<Campaign> {
        val index = store.indexOfFirst { it.campaignId == campaignId }
        if (index == -1) return AppResult.Error(AppError.NotFound(message = "Khong tim thay chien dich."))
        val existing = store[index]
        val updated = existing.copy(
            campaignName = campaignName ?: existing.campaignName,
            description = description ?: existing.description,
            startDate = startDate ?: existing.startDate,
            endDate = endDate ?: existing.endDate
        )
        store[index] = updated
        return AppResult.Success(updated)
    }

    // Real: apiService.deleteCampaign(token, campaignId) — check success field
    override suspend fun deleteCampaign(campaignId: Int): AppResult<Unit> {
        val removed = store.removeIf { it.campaignId == campaignId }
        return if (removed) AppResult.Success(Unit)
        else AppResult.Error(AppError.NotFound(message = "Khong tim thay chien dich."))
    }
}
