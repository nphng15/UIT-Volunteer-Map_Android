package com.example.uitvolunteermap.features.checkin.data.remote

import com.example.uitvolunteermap.core.network.ApiEnvelope
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CheckinApiService {

    @POST("checkin")
    suspend fun checkin(@Body request: CheckinRequestDto): ApiEnvelope<CheckinResponseDto>

    @GET("checkin/history")
    suspend fun getHistory(): ApiEnvelope<List<CheckinHistoryItemDto>>
}
