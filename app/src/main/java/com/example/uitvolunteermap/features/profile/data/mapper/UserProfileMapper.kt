package com.example.uitvolunteermap.features.profile.data.mapper

import com.example.uitvolunteermap.features.profile.data.model.UserProfileDto
import com.example.uitvolunteermap.features.profile.domain.entity.UserProfile

fun UserProfileDto.toDomain(): UserProfile = UserProfile(
    userId = userId,
    fullName = fullName,
    mssv = mssv,
    className = className,
    email = email,
    phoneNumber = phoneNumber,
    createdAt = createdAt
)
