package com.example.uitvolunteermap.features.auth.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.auth.data.mapper.toDomain
import com.example.uitvolunteermap.features.auth.data.remote.AuthApiService
import com.example.uitvolunteermap.features.auth.data.remote.LoginRequest
import com.example.uitvolunteermap.features.auth.domain.entity.AuthUser
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RemoteAuthRepository @Inject constructor(
    private val authApiService: AuthApiService
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<AuthUser> {
        return apiCall(
            request = { authApiService.login(LoginRequest(username = email, password = password)) },
            map = { it.toDomain() }
        )
    }
}
