package com.example.uitvolunteermap.features.profile.presentation

import com.example.uitvolunteermap.core.session.UserRole

data class ProfileUiState(
    val username: String = "",
    val role: UserRole = UserRole.GUEST,
    val accountId: Int = 0,
    val isLoggingOut: Boolean = false,
    val fullName: String? = null,
    val mssv: String? = null,
    val className: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val createdAt: String? = null,
    val isProfileLoading: Boolean = false
)
