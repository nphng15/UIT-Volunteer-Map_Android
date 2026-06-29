package com.example.uitvolunteermap.features.checkin.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.core.network.toAppError
import com.example.uitvolunteermap.core.common.error.toAppError
import com.example.uitvolunteermap.features.checkin.data.remote.CampaignPhotoDto
import com.example.uitvolunteermap.features.checkin.data.remote.CampaignPhotoRequestDto
import com.example.uitvolunteermap.features.checkin.data.remote.CheckinApiService
import com.example.uitvolunteermap.features.checkin.data.remote.CheckinRequestDto
import com.example.uitvolunteermap.features.checkin.data.remote.MyCampaignDto
import com.example.uitvolunteermap.features.checkin.domain.entity.CampaignMoment
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinHistoryItem
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinResult
import com.example.uitvolunteermap.features.checkin.domain.entity.MyCampaign
import com.example.uitvolunteermap.features.checkin.domain.repository.CheckinRepository
import java.io.File
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class RemoteCheckinRepository @Inject constructor(
    private val api: CheckinApiService
) : CheckinRepository {

    override suspend fun checkin(
        campaignId: Int,
        latitude: Double,
        longitude: Double,
        imageUrl: String?
    ): AppResult<CheckinResult> = apiCall(
        request = {
            api.checkin(CheckinRequestDto(campaignId, latitude, longitude, imageUrl))
        },
        map = { dto ->
            CheckinResult(
                checkInId = dto.checkInId,
                campaignId = dto.campaignId,
                distance = dto.distance,
                checkedInAt = dto.checkedInAt,
                imageUrl = imageUrl
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

    override suspend fun uploadImage(file: File): AppResult<String> {
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", file.name, requestBody)
        return apiCall(
            request = { api.uploadImage(part) },
            map = { it.url }
        )
    }

    override suspend fun getMyCampaign(): AppResult<MyCampaign?> = try {
        val response = api.getMyCampaign()
        if (response.success) {
            // data == null là hợp lệ: TNV chưa được gán chiến dịch nào.
            AppResult.Success(response.data?.toDomain())
        } else {
            AppResult.Error(response.toAppError())
        }
    } catch (throwable: Throwable) {
        AppResult.Error(throwable.toAppError())
    }

    override suspend fun getCampaignMoments(campaignId: Int): AppResult<List<CampaignMoment>> = apiCall(
        request = { api.getCampaignPhotos(campaignId) },
        map = { list -> list.map { it.toDomain() } }
    )

    override suspend fun addMoment(
        campaignId: Int,
        imageUrl: String,
        caption: String?
    ): AppResult<CampaignMoment> = apiCall(
        request = { api.addCampaignPhoto(campaignId, CampaignPhotoRequestDto(imageUrl, caption)) },
        map = { it.toDomain() }
    )

    override suspend fun deleteMoment(campaignId: Int, momentId: Int): AppResult<Unit> = apiCall(
        request = { api.deleteCampaignPhoto(campaignId, momentId) },
        map = { }
    )
}

private fun MyCampaignDto.toDomain() = MyCampaign(
    campaignId = campaignId,
    campaignName = campaignName,
    description = description,
    startDate = startDate,
    endDate = endDate,
    latitude = latitude,
    longitude = longitude,
    checkInRadius = checkInRadius,
    teamCheckInLatitude = teamCheckInLatitude,
    teamCheckInLongitude = teamCheckInLongitude,
    teamCheckInRadius = teamCheckInRadius,
    teamId = teamId,
    teamName = teamName,
    hasCheckedIn = hasCheckedIn,
    checkedInAt = checkedInAt
)

private fun CampaignPhotoDto.toDomain() = CampaignMoment(
    id = campaignPhotoId,
    accId = accId,
    imageUrl = imageUrl,
    caption = caption,
    isCheckinPhoto = isCheckinPhoto == 1,
    createdAt = createdAt,
    authorName = author?.fullName ?: "Tình nguyện viên",
    authorAvatarUrl = author?.avatarUrl,
    authorTeamName = author?.teamName
)
