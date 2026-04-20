package com.example.uitvolunteermap.features.auth.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.auth.domain.entity.AuthUser

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<AuthUser>
}
