package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.campaign.data.datasource.CampaignApiService
import com.example.uitvolunteermap.features.campaign.data.datasource.CreateCampaignRequest
import com.example.uitvolunteermap.features.campaign.data.datasource.UpdateCampaignRequest
import com.example.uitvolunteermap.features.campaign.data.model.CampaignDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteCampaignRepositoryTest {

    @Test
    fun createCampaign_posts_backend_body_and_maps_campaign() = runTest {
        val api = FakeCampaignApiService()
        val repository = RemoteCampaignRepository(api)

        val result = repository.createCampaign(
            campaignName = "Mua He Xanh 2026",
            description = "Volunteer campaign",
            startDate = "2026-06-01",
            endDate = "2026-07-15"
        )

        assertTrue(result is AppResult.Success)
        assertEquals(
            CreateCampaignRequest(
                campaignName = "Mua He Xanh 2026",
                description = "Volunteer campaign",
                startDate = "2026-06-01",
                endDate = "2026-07-15"
            ),
            api.lastCreateRequest
        )
        val campaign = (result as AppResult.Success).data
        assertEquals(1, campaign.campaignId)
        assertEquals("Mua He Xanh 2026", campaign.campaignName)
    }

    private class FakeCampaignApiService : CampaignApiService {
        var lastCreateRequest: CreateCampaignRequest? = null

        override suspend fun getCampaigns(): ApiEnvelope<List<CampaignDto>> =
            ApiEnvelope(success = true, data = listOf(campaignDto()), message = null, error = null)

        override suspend fun getCampaign(campaignId: Int): ApiEnvelope<CampaignDto> =
            ApiEnvelope(success = true, data = campaignDto(campaignId), message = null, error = null)

        override suspend fun createCampaign(body: CreateCampaignRequest): ApiEnvelope<CampaignDto> {
            lastCreateRequest = body
            return ApiEnvelope(success = true, data = campaignDto(), message = null, error = null)
        }

        override suspend fun updateCampaign(
            campaignId: Int,
            body: UpdateCampaignRequest
        ): ApiEnvelope<CampaignDto> =
            ApiEnvelope(success = true, data = campaignDto(campaignId), message = null, error = null)

        override suspend fun deleteCampaign(campaignId: Int): ApiEnvelope<Unit> =
            ApiEnvelope(success = true, data = Unit, message = null, error = null)

        private fun campaignDto(id: Int = 1): CampaignDto = CampaignDto(
            campaignId = id,
            campaignName = "Mua He Xanh 2026",
            description = "Volunteer campaign",
            startDate = "2026-06-01",
            endDate = "2026-07-15"
        )
    }
}
