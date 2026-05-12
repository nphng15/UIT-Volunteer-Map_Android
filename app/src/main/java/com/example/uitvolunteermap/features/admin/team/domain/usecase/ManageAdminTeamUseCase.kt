package com.example.uitvolunteermap.features.admin.team.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.team.domain.repository.AdminTeamRepository
import javax.inject.Inject

/**
 * Use case quản lý team (tạo/sửa/xóa) kèm validation phía client khớp hợp đồng API:
 *  - teamName: 3..100 ký tự (bắt buộc khi tạo; nếu truyền khi sửa thì min 1).
 *  - leaderId, campaignId: số nguyên dương (chỉ dùng khi tạo — KHÔNG sửa được).
 *  - imageUrl: nếu có phải là URL http/https hợp lệ.
 *
 * PUT không cho đổi leaderId/campaignId nên [update] không nhận hai tham số đó.
 */
class ManageAdminTeamUseCase @Inject constructor(
    private val repository: AdminTeamRepository
) {

    suspend fun create(
        teamName: String,
        leaderId: Int,
        campaignId: Int,
        description: String?,
        imageUrl: String?
    ): AppResult<Unit> {
        val trimmedName = teamName.trim()
        if (trimmedName.length < 3)
            return AppResult.Error(AppError.Validation("Tên đội phải có ít nhất 3 ký tự."))
        if (trimmedName.length > 100)
            return AppResult.Error(AppError.Validation("Tên đội không được vượt quá 100 ký tự."))
        if (leaderId <= 0)
            return AppResult.Error(AppError.Validation("ID nhóm trưởng phải là số nguyên dương."))
        if (campaignId <= 0)
            return AppResult.Error(AppError.Validation("ID chiến dịch phải là số nguyên dương."))

        val cleanedImageUrl = imageUrl?.trim()?.takeIf { it.isNotEmpty() }
        if (cleanedImageUrl != null && !isValidUrl(cleanedImageUrl))
            return AppResult.Error(AppError.Validation("Đường dẫn ảnh không hợp lệ."))

        return repository.createTeam(
            teamName = trimmedName,
            leaderId = leaderId,
            campaignId = campaignId,
            description = description?.trim()?.takeIf { it.isNotEmpty() },
            imageUrl = cleanedImageUrl
        )
    }

    suspend fun update(
        teamId: Int,
        teamName: String? = null,
        description: String? = null,
        imageUrl: String? = null
    ): AppResult<Unit> {
        if (teamId <= 0)
            return AppResult.Error(AppError.Validation("Mã đội không hợp lệ."))
        if (teamName == null && description == null && imageUrl == null)
            return AppResult.Error(AppError.Validation("Cần cung cấp ít nhất một trường để cập nhật."))

        val trimmedName = teamName?.trim()
        if (trimmedName != null) {
            if (trimmedName.isEmpty())
                return AppResult.Error(AppError.Validation("Tên đội không được để trống."))
            if (trimmedName.length > 100)
                return AppResult.Error(AppError.Validation("Tên đội không được vượt quá 100 ký tự."))
        }

        val cleanedImageUrl = imageUrl?.trim()
        if (cleanedImageUrl != null && cleanedImageUrl.isNotEmpty() && !isValidUrl(cleanedImageUrl))
            return AppResult.Error(AppError.Validation("Đường dẫn ảnh không hợp lệ."))

        return repository.updateTeam(
            teamId = teamId,
            teamName = trimmedName,
            description = description?.trim(),
            imageUrl = cleanedImageUrl
        )
    }

    suspend fun delete(teamId: Int): AppResult<Unit> {
        if (teamId <= 0)
            return AppResult.Error(AppError.Validation("Mã đội không hợp lệ."))
        return repository.deleteTeam(teamId)
    }

    private fun isValidUrl(url: String): Boolean =
        url.startsWith("http://", ignoreCase = true) ||
            url.startsWith("https://", ignoreCase = true)
}
