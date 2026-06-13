package com.example.uitvolunteermap.features.checkin.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.checkin.data.remote.CheckinApiService
import com.example.uitvolunteermap.features.checkin.data.remote.CheckinRequestDto
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinHistoryItem
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinResult
import com.example.uitvolunteermap.features.checkin.domain.repository.CheckinRepository
import javax.inject.Inject

class RemoteCheckinRepository @Inject constructor(
    private val api: CheckinApiService
) : CheckinRepository {

    override suspend fun checkin(
        campaignId: Int,
        latitude: Double,
        longitude: Double
    ): AppResult<CheckinResult> = apiCall(
        request = {
            api.checkin(CheckinRequestDto(campaignId, latitude, longitude))
        },
        map = { dto ->
            CheckinResult(
                checkInId = dto.checkInId,
                campaignId = dto.campaignId,
                distance = dto.distance,
                checkedInAt = dto.checkedInAt
            )
        }
    )

    override suspend fun getHistory(): AppResult<List<CheckinHistoryItem>> = apiCall(
        request = { api.getHistory() },
        map = { list ->
            list.map { dto ->
                CheckinHistoryItem(
                    checkInId = dto.checkInId,
                    campaignId = dto.campaignId,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    distance = dto.distance,
                    checkedInAt = dto.checkedInAt,
                    campaignName = dto.campaign?.campaignName ?: "Chiến dịch #${dto.campaignId}",
                    campaignLatitude = dto.campaign?.latitude,
                    campaignLongitude = dto.campaign?.longitude,
                    checkInRadius = dto.campaign?.checkInRadius
                )
            }
        }
    )
}
