package com.example.uitvolunteermap.features.profile.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.profile.data.datasource.UserProfileApiService
import com.example.uitvolunteermap.features.profile.data.mapper.toDomain
import com.example.uitvolunteermap.features.profile.domain.entity.UserProfile
import com.example.uitvolunteermap.features.profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class RemoteUserProfileRepository @Inject constructor(
    private val userProfileApiService: UserProfileApiService
) : UserProfileRepository {

    override suspend fun getProfile(): AppResult<UserProfile> = apiCall(
        request = { userProfileApiService.getProfile() },
        map = { it.toDomain() }
    )
}
