package com.example.uitvolunteermap.features.checkin.data.remote

import com.example.uitvolunteermap.core.network.ApiEnvelope
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CheckinApiService {

    @POST("checkin")
    suspend fun checkin(@Body request: CheckinRequestDto): ApiEnvelope<CheckinResponseDto>

    @GET("checkin/history")
    suspend fun getHistory(): ApiEnvelope<List<CheckinHistoryItemDto>>

    @Multipart
    @POST("uploads/image")
    suspend fun uploadImage(@Part image: MultipartBody.Part): ApiEnvelope<UploadImageDto>

    @GET("users/me/campaign")
    suspend fun getMyCampaign(): ApiEnvelope<MyCampaignDto?>

    @GET("campaigns/{id}/photos")
    suspend fun getCampaignPhotos(@Path("id") campaignId: Int): ApiEnvelope<List<CampaignPhotoDto>>

    @POST("campaigns/{id}/photos")
    suspend fun addCampaignPhoto(
        @Path("id") campaignId: Int,
        @Body request: CampaignPhotoRequestDto
    ): ApiEnvelope<CampaignPhotoDto>

    @DELETE("campaigns/{id}/photos/{photoId}")
    suspend fun deleteCampaignPhoto(
        @Path("id") campaignId: Int,
        @Path("photoId") photoId: Int
    ): ApiEnvelope<Unit>
}
