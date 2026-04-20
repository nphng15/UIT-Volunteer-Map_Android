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
            return AppResult.Error(AppError.Validation("Tên chiến dịch không được để trống."))
        if (campaignName.length > 100)
            return AppResult.Error(AppError.Validation("Tên chiến dịch không được vượt quá 100 ký tự."))
        if (description != null && description.length > 500)
            return AppResult.Error(AppError.Validation("Mô tả không được vượt quá 500 ký tự."))
        if (startDate.isBlank())
            return AppResult.Error(AppError.Validation("Ngày bắt đầu không được để trống."))
        if (endDate.isBlank())
            return AppResult.Error(AppError.Validation("Ngày kết thúc không được để trống."))

        val parsedStart = parseDate(startDate)
            ?: return AppResult.Error(AppError.Validation("Ngày bắt đầu không hợp lệ. Định dạng yêu cầu: yyyy-MM-dd."))
        val parsedEnd = parseDate(endDate)
            ?: return AppResult.Error(AppError.Validation("Ngày kết thúc không hợp lệ. Định dạng yêu cầu: yyyy-MM-dd."))

        if (parsedEnd < parsedStart)
            return AppResult.Error(AppError.Validation("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu."))

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
            return AppResult.Error(AppError.Validation("Mã chiến dịch không hợp lệ."))
        if (campaignName == null && description == null && startDate == null && endDate == null)
            return AppResult.Error(AppError.Validation("Cần cung cấp ít nhất một trường để cập nhật."))

        if (campaignName != null) {
            if (campaignName.isBlank())
                return AppResult.Error(AppError.Validation("Tên chiến dịch không được để trống."))
            if (campaignName.length > 100)
                return AppResult.Error(AppError.Validation("Tên chiến dịch không được vượt quá 100 ký tự."))
        }
        if (description != null && description.length > 500)
            return AppResult.Error(AppError.Validation("Mô tả không được vượt quá 500 ký tự."))

        // Validate date format nếu được cung cấp; chỉ check thứ tự khi CẢ HAI cùng được truyền vào
        val parsedStart = if (startDate != null) {
            parseDate(startDate)
                ?: return AppResult.Error(AppError.Validation("Ngày bắt đầu không hợp lệ. Định dạng yêu cầu: yyyy-MM-dd."))
        } else null

        val parsedEnd = if (endDate != null) {
            parseDate(endDate)
                ?: return AppResult.Error(AppError.Validation("Ngày kết thúc không hợp lệ. Định dạng yêu cầu: yyyy-MM-dd."))
        } else null

        if (parsedStart != null && parsedEnd != null && parsedEnd < parsedStart)
            return AppResult.Error(AppError.Validation("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu."))

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
            return AppResult.Error(AppError.Validation("Mã chiến dịch không hợp lệ."))
        return repository.deleteCampaign(campaignId)
    }

    private fun parseDate(date: String): LocalDate? = try {
        LocalDate.parse(date) // ISO_LOCAL_DATE (yyyy-MM-dd) là default
    } catch (e: DateTimeParseException) {
        null
    }
}
