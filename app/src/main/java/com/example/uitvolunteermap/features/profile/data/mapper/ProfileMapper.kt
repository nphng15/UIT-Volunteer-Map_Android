package com.example.uitvolunteermap.features.profile.data.mapper

import com.example.uitvolunteermap.features.profile.data.model.ProfileDto
import com.example.uitvolunteermap.features.profile.domain.entity.ProfileInfo

/**
 * Chuyển đổi từ DTO (Data Layer) sang Entity (Domain Layer).
 */
fun ProfileDto.toDomain(): ProfileInfo {
    return ProfileInfo(
        userId = this.userId ?: "",
        fullName = this.fullName ?: "N/A",
        mssv = this.mssv ?: "N/A",
        className = this.className ?: "N/A",
        email = this.email ?: "",
        phoneNumber = this.phoneNumber ?: "",
        createdAt = this.createdAt ?: ""
    )
}

/**
 * Nếu cần cập nhật Profile, chúng ta có thể cần chuyển ngược lại từ Entity sang DTO
 * (Sử dụng khi gọi API PUT /users/profile).
 */
fun ProfileInfo.toDto(): ProfileDto {
    return ProfileDto(
        userId = this.userId,
        fullName = this.fullName,
        mssv = this.mssv,
        className = this.className,
        email = this.email,
        phoneNumber = this.phoneNumber,
        createdAt = this.createdAt
    )
    }