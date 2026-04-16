package com.example.uitvolunteermap.features.profile.domain.usecase

import com.example.uitvolunteermap.core.UserRole
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.profile.domain.entity.ProfileInfo
import com.example.uitvolunteermap.features.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): AppResult<ProfileInfo> {
        return when (val result = repository.getProfile()) {
            is AppResult.Success -> {
                val profile = result.data
                // Chuyển logic chuẩn hóa từ ViewModel về UseCase
                val normalized = if (profile.role == UserRole.GUEST) {
                    profile.copy(
                        fullName = "NA",
                        mssv = "",
                        className = "",
                        email = "",
                        phoneNumber = "",
                    )
                } else {
                    profile
                }
                AppResult.Success(normalized)
            }
            is AppResult.Error -> result
            else -> result
        }
    }
}