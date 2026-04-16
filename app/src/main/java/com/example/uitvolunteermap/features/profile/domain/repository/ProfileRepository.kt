package com.example.uitvolunteermap.features.profile.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult // Import AppResult của dự án
import com.example.uitvolunteermap.features.profile.domain.entity.ProfileInfo

interface ProfileRepository {
    // Đổi Result sang AppResult
    suspend fun getProfile(): AppResult<ProfileInfo>
    suspend fun updateProfile(profile: ProfileInfo): AppResult<ProfileInfo>
    suspend fun logout(): AppResult<Unit>
}