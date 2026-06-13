package com.example.uitvolunteermap.features.campaign.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.Campaign
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignRepository
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

class ManageCampaignUseCase @Inject constructor(
    private val repository: CampaignRepository
) {

    suspend fun create(
        campaignName: String,
        description: String?,
        startDate: String,
        endDate: String
    ): AppResult<Campaign> {
        if (campaignName.isBlank())
            return AppResult.Error(AppError.Validation("Ten chien dich khong duoc de trong."))
        if (campaignName.length > 100)
            return AppResult.Error(AppError.Validation("Ten chien dich khong duoc vuot qua 100 ky tu."))
        if (description != null && description.length > 500)
            return AppResult.Error(AppError.Validation("Mo ta khong duoc vuot qua 500 ky tu."))
        if (startDate.isBlank())
            return AppResult.Error(AppError.Validation("Ngay bat dau khong duoc de trong."))
        if (endDate.isBlank())
            return AppResult.Error(AppError.Validation("Ngay ket thuc khong duoc de trong."))

        val parsedStart = parseDate(startDate)
            ?: return AppResult.Error(AppError.Validation("Ngay bat dau khong hop le. Dinh dang yeu cau: yyyy-MM-dd."))
        val parsedEnd = parseDate(endDate)
            ?: return AppResult.Error(AppError.Validation("Ngay ket thuc khong hop le. Dinh dang yeu cau: yyyy-MM-dd."))

        if (parsedEnd < parsedStart)
            return AppResult.Error(AppError.Validation("Ngay ket thuc phai lon hon hoac bang ngay bat dau."))

        return repository.createCampaign(
            campaignName = campaignName.trim(),
            description = description?.trim(),
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun update(
        campaignId: Int,
        campaignName: String? = null,
        description: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): AppResult<Campaign> {
        if (campaignId <= 0)
            return AppResult.Error(AppError.Validation("ID chien dich khong hop le."))
        if (campaignName == null && description == null && startDate == null && endDate == null)
            return AppResult.Error(AppError.Validation("Can cung cap it nhat mot truong de cap nhat."))

        if (campaignName != null) {
            if (campaignName.isBlank())
                return AppResult.Error(AppError.Validation("Ten chien dich khong duoc de trong."))
            if (campaignName.length > 100)
                return AppResult.Error(AppError.Validation("Ten chien dich khong duoc vuot qua 100 ky tu."))
        }
        if (description != null && description.length > 500)
            return AppResult.Error(AppError.Validation("Mo ta khong duoc vuot qua 500 ky tu."))

        // Validate date format nếu được cung cấp; chỉ check thứ tự khi CẢ HAI cùng được truyền vào
        val parsedStart = if (startDate != null) {
            parseDate(startDate)
                ?: return AppResult.Error(AppError.Validation("Ngay bat dau khong hop le. Dinh dang yeu cau: yyyy-MM-dd."))
        } else null

        val parsedEnd = if (endDate != null) {
            parseDate(endDate)
                ?: return AppResult.Error(AppError.Validation("Ngay ket thuc khong hop le. Dinh dang yeu cau: yyyy-MM-dd."))
        } else null

        if (parsedStart != null && parsedEnd != null && parsedEnd < parsedStart)
            return AppResult.Error(AppError.Validation("Ngay ket thuc phai lon hon hoac bang ngay bat dau."))

        return repository.updateCampaign(
            campaignId = campaignId,
            campaignName = campaignName?.trim(),
            description = description?.trim(),
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun delete(campaignId: Int): AppResult<Unit> {
        if (campaignId <= 0)
            return AppResult.Error(AppError.Validation("ID chien dich khong hop le."))
        return repository.deleteCampaign(campaignId)
    }

    private fun parseDate(date: String): LocalDate? = try {
        LocalDate.parse(date) // ISO_LOCAL_DATE (yyyy-MM-dd) là default
    } catch (e: DateTimeParseException) {
        null
    }
}
