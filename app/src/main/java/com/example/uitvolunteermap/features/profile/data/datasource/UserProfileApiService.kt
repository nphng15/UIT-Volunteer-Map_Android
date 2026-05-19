package com.example.uitvolunteermap.features.profile.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.profile.data.model.UserProfileDto
import retrofit2.http.GET

interface UserProfileApiService {

    @GET("users/profile")
    suspend fun getProfile(): ApiEnvelope<UserProfileDto>
}
