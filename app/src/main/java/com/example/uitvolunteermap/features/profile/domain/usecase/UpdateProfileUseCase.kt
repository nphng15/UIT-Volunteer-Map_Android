package com.example.uitvolunteermap.features.profile.domain.usecase

import com.example.uitvolunteermap.core.UserRole
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.profile.domain.entity.ProfileInfo
import com.example.uitvolunteermap.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(profile: ProfileInfo): AppResult<ProfileInfo> {
        // 1. Kiểm tra quyền (Quy tắc nghiệp vụ từ Permission Matrix)
        // Leader và Admin đều có quyền cập nhật Profile của chính họ
        if (profile.role == UserRole.GUEST) {
            return AppResult.Error(AppError.Forbidden("Khách không có quyền cập nhật hồ sơ."))
        }

        // 2. Gọi Repository khi quyền hợp lệ
        return repository.updateProfile(profile)
    }
}