package com.example.uitvolunteermap.features.admin.campaign.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.campaign.domain.entity.AdminCampaign
import com.example.uitvolunteermap.features.admin.campaign.domain.repository.AdminCampaignRepository
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

/**
 * Validation nghiệp vụ cho tạo/sửa/xoá chiến dịch ở khu quản trị.
 * Toàn bộ thông báo lỗi bằng tiếng Việt, trả về AppError.Validation.
 */
class ManageAdminCampaignUseCase @Inject constructor(
    private val repository: AdminCampaignRepository
) {

    suspend fun create(
        campaignName: String,
        description: String?,
        startDate: String,
        endDate: String,
        latitude: Double?,
        longitude: Double?,
        checkInRadius: Double?
    ): AppResult<AdminCampaign> {
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

        validateLocation(latitude, longitude, checkInRadius)?.let { return it }

        return repository.createCampaign(
            campaignName = campaignName.trim(),
            description = description?.trim(),
            startDate = startDate,
            endDate = endDate,
            latitude = latitude,
            longitude = longitude,
            checkInRadius = checkInRadius
        )
    }

    suspend fun update(
        campaignId: Int,
        campaignName: String? = null,
        description: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        checkInRadius: Double? = null
    ): AppResult<AdminCampaign> {
        if (campaignId <= 0)
            return AppResult.Error(AppError.Validation("Mã chiến dịch không hợp lệ."))
        if (campaignName == null && description == null && startDate == null && endDate == null &&
            latitude == null && longitude == null && checkInRadius == null
        ) {
            return AppResult.Error(AppError.Validation("Cần cung cấp ít nhất một trường để cập nhật."))
        }

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

        validateLocation(latitude, longitude, checkInRadius)?.let { return it }

        return repository.updateCampaign(
            campaignId = campaignId,
            campaignName = campaignName?.trim(),
            description = description?.trim(),
            startDate = startDate,
            endDate = endDate,
            latitude = latitude,
            longitude = longitude,
            checkInRadius = checkInRadius
        )
    }

    suspend fun delete(campaignId: Int): AppResult<Unit> {
        if (campaignId <= 0)
            return AppResult.Error(AppError.Validation("Mã chiến dịch không hợp lệ."))
        return repository.deleteCampaign(campaignId)
    }

    /** Trả về AppResult.Error nếu vị trí không hợp lệ, ngược lại null. */
    private fun validateLocation(
        latitude: Double?,
        longitude: Double?,
        checkInRadius: Double?
    ): AppResult.Error? {
        if (latitude != null && (latitude < -90.0 || latitude > 90.0))
            return AppResult.Error(AppError.Validation("Vĩ độ phải nằm trong khoảng -90 đến 90."))
        if (longitude != null && (longitude < -180.0 || longitude > 180.0))
            return AppResult.Error(AppError.Validation("Kinh độ phải nằm trong khoảng -180 đến 180."))
        if (checkInRadius != null && checkInRadius <= 0.0)
            return AppResult.Error(AppError.Validation("Bán kính điểm danh phải là số dương."))
        return null
    }

    private fun parseDate(date: String): LocalDate? = try {
        LocalDate.parse(date) // ISO_LOCAL_DATE (yyyy-MM-dd) là default
    } catch (e: DateTimeParseException) {
        null
    }
}
