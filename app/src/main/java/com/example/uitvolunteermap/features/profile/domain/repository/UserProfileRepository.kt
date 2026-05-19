package com.example.uitvolunteermap.features.profile.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.profile.domain.entity.UserProfile

interface UserProfileRepository {
    suspend fun getProfile(): AppResult<UserProfile>
}
