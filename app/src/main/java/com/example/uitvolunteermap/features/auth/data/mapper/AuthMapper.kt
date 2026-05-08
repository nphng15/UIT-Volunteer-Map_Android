package com.example.uitvolunteermap.features.auth.data.mapper

import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.auth.data.remote.LoginResponseDto
import com.example.uitvolunteermap.features.auth.domain.entity.AuthUser

fun LoginResponseDto.toDomain(): AuthUser {
    val role = when (user.role.lowercase()) {
        "admin" -> UserRole.ADMIN
        "leader" -> UserRole.LEADER
        else -> UserRole.VOLUNTEER
    }
    return AuthUser(
        id = user.accId.toString(),
        email = user.username,
        displayName = user.username,
        token = token,
        accountId = user.accId,
        username = user.username,
        role = role
    )
}
