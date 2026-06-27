package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.campaign.data.datasource.CampaignApiService
import com.example.uitvolunteermap.features.campaign.data.datasource.TeamApiService
import com.example.uitvolunteermap.features.campaign.data.mapper.toCampaignDetailTeam
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetailPost
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetailStat
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignMapOverview
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignDetailRepository
import com.example.uitvolunteermap.features.post.data.remote.PostApiService
import javax.inject.Inject

class RemoteCampaignDetailRepository @Inject constructor(
    private val campaignApiService: CampaignApiService,
    private val teamApiService: TeamApiService,
    private val postApiService: PostApiService
) : CampaignDetailRepository {

    override suspend fun getCampaignDetail(campaignId: Int): AppResult<CampaignDetail> = apiCall(
        request = { campaignApiService.getCampaign(campaignId) },
        map = { campaign ->
            // TODO: API hiện không hỗ trợ filter teams/posts theo campaignId — cần backend endpoint mới
            val teams = runCatching { teamApiService.getTeams() }.getOrNull()
                ?.data.orEmpty()
                .mapIndexed { index, team -> team.toCampaignDetailTeam(index) }
            val posts = runCatching { postApiService.getPosts() }.getOrNull()
                ?.data.orEmpty()
                .take(5)
                .mapIndexed { index, post ->
                    CampaignDetailPost(
                        id = post.postId,
                        teamName = post.team?.teamName ?: "Chưa rõ đội",
                        title = post.title,
                        publishedAt = post.createdAt,
                        summary = post.content,
                        accentColors = if (index % 2 == 0) listOf(0xFF20303A, 0xFF6D839A) else listOf(0xFF1C3977, 0xFF6B8FD6),
                        isLightBadge = index % 2 == 0
                    )
                }
            CampaignDetail(
                id = campaign.campaignId,
                appName = "UIT Volunteer Map",
                title = campaign.campaignName,
                schedule = "${campaign.startDate} - ${campaign.endDate}",
                heroHeadline = campaign.campaignName,
                heroSupportingText = campaign.description.orEmpty(),
                stats = listOf(
                    CampaignDetailStat(value = teams.size.toString(), label = "Đội hình"),
                    CampaignDetailStat(value = posts.size.toString(), label = "Bài viết")
                ),
                description = campaign.description.orEmpty(),
                teamSectionTitle = "Đội hình tham gia",
                teams = teams,
                posts = posts,
                mapOverview = CampaignMapOverview(
                    selectedArea = "UIT",
                    headerTitle = "Khu vực hoạt động",
                    footerTitle = "Bản đồ điểm hoạt động theo đội",
                    footerDescription = "Xem trên bản đồ thật các điểm mỗi đội đã đi trong chiến dịch.",
                    ctaLabel = "Xem bản đồ khu vực",
                    locations = emptyList()
                )
            )
        }
    )
}
