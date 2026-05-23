package com.example.uitvolunteermap.features.home.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.campaign.data.datasource.CampaignApiService
import com.example.uitvolunteermap.features.campaign.data.datasource.TeamApiService
import com.example.uitvolunteermap.features.home.domain.model.VolunteerCampaignSummary
import com.example.uitvolunteermap.features.home.domain.model.VolunteerHomeContent
import com.example.uitvolunteermap.features.home.domain.model.VolunteerOverviewStat
import com.example.uitvolunteermap.features.home.domain.repository.VolunteerHomeRepository
import com.example.uitvolunteermap.features.post.data.remote.PostApiService
import javax.inject.Inject

class RemoteVolunteerHomeRepository @Inject constructor(
    private val campaignApiService: CampaignApiService,
    private val teamApiService: TeamApiService,
    private val postApiService: PostApiService
) : VolunteerHomeRepository {

    override suspend fun getVolunteerHomeContent(): AppResult<VolunteerHomeContent> = apiCall(
        request = { campaignApiService.getCampaigns() },
        map = { campaigns ->
            val teamCount = runCatching { teamApiService.getTeams() }.getOrNull()?.data?.size ?: 0
            val postCount = runCatching { postApiService.getPosts() }.getOrNull()?.data?.size ?: 0
            VolunteerHomeContent(
                appName = "UIT Volunteer Map",
                stats = listOf(
                    VolunteerOverviewStat(campaigns.size.toString(), "Chiến dịch"),
                    VolunteerOverviewStat(teamCount.toString(), "Đội hình"),
                    VolunteerOverviewStat(postCount.toString(), "Bài viết")
                ),
                campaigns = campaigns.mapIndexed { index, campaign ->
                    VolunteerCampaignSummary(
                        id = campaign.campaignId,
                        title = campaign.campaignName,
                        dateRange = "${campaign.startDate} - ${campaign.endDate}",
                        description = campaign.description.orEmpty(),
                        meta = "$teamCount đội - $postCount bài viết",
                        primaryActionLabel = "Xem chiến dịch",
                        secondaryActionLabel = "Bài viết",
                        accentColors = if (index % 2 == 0) listOf(0xFFF7F1D8, 0xFFFFF4CC) else listOf(0xFFE5F3FF, 0xFFD9FBE8)
                    )
                }
            )
        }
    )
}
