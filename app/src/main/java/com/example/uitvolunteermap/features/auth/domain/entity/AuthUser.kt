package com.example.uitvolunteermap.features.auth.domain.entity

import com.example.uitvolunteermap.core.session.UserRole

data class AuthUser(
    val id: String,
    val email: String,
    val displayName: String,
    val token: String? = null,
    val accountId: Int? = null,
    val username: String = email,
    val role: UserRole = UserRole.VOLUNTEER,
)
