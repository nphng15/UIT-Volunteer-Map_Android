package com.example.uitvolunteermap.features.checkin.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.checkin.domain.entity.CampaignMoment
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinHistoryItem
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinResult
import com.example.uitvolunteermap.features.checkin.domain.entity.MyCampaign
import java.io.File

interface CheckinRepository {
    suspend fun checkin(
        campaignId: Int,
        latitude: Double,
        longitude: Double,
        imageUrl: String?
    ): AppResult<CheckinResult>

    suspend fun getHistory(): AppResult<List<CheckinHistoryItem>>

    /** Upload một file ảnh, trả về URL công khai. */
    suspend fun uploadImage(file: File): AppResult<String>

    /** Chiến dịch của TNV hiện tại (null nếu chưa thuộc chiến dịch nào). */
    suspend fun getMyCampaign(): AppResult<MyCampaign?>

    /** Wall ảnh khoảnh khắc của một chiến dịch. */
    suspend fun getCampaignMoments(campaignId: Int): AppResult<List<CampaignMoment>>

    /** Đăng một ảnh khoảnh khắc (sau khi đã điểm danh). */
    suspend fun addMoment(campaignId: Int, imageUrl: String, caption: String?): AppResult<CampaignMoment>

    /** Xóa (soft) một ảnh khoảnh khắc. */
    suspend fun deleteMoment(campaignId: Int, momentId: Int): AppResult<Unit>
}
