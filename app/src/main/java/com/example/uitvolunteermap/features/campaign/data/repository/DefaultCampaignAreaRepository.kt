package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.data.local.LocalTeamPointsStore
import com.example.uitvolunteermap.features.campaign.data.local.MockTeamPointsSource
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamPointSource
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamVisitPoint
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignAreaRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Merges seeded demo points with locally marked points for a campaign.
 * (Backend has no coordinate endpoint yet — see RemoteCampaignDetailRepository.)
 */
@Singleton
class DefaultCampaignAreaRepository @Inject constructor(
    private val mockSource: MockTeamPointsSource,
    private val localStore: LocalTeamPointsStore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CampaignAreaRepository {

    override suspend fun getTeamPoints(campaignId: Int): AppResult<List<TeamVisitPoint>> =
        withContext(ioDispatcher) {
            val mock = mockSource.pointsFor(campaignId)
            val manual = localStore.loadForCampaign(campaignId)
            AppResult.Success((manual + mock).sortedBy { it.teamId })
        }

    override suspend fun addManualPoint(
        campaignId: Int,
        teamId: Int,
        teamName: String,
        name: String,
        latitude: Double,
        longitude: Double
    ): AppResult<TeamVisitPoint> = withContext(ioDispatcher) {
        val point = TeamVisitPoint(
            id = "manual-${System.currentTimeMillis()}",
            campaignId = campaignId,
            teamId = teamId,
            teamName = teamName,
            name = name,
            latitude = latitude,
            longitude = longitude,
            source = TeamPointSource.MANUAL,
            createdAt = nowIsoString()
        )
        localStore.add(point)
        AppResult.Success(point)
    }

    override suspend fun removeManualPoint(id: String): AppResult<Unit> = withContext(ioDispatcher) {
        localStore.remove(id)
        AppResult.Success(Unit)
    }

    private fun nowIsoString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return formatter.format(Date())
    }
}
