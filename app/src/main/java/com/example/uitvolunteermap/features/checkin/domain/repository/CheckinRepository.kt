package com.example.uitvolunteermap.features.checkin.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinHistoryItem
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinResult

interface CheckinRepository {
    suspend fun checkin(campaignId: Int, latitude: Double, longitude: Double): AppResult<CheckinResult>
    suspend fun getHistory(): AppResult<List<CheckinHistoryItem>>
}
