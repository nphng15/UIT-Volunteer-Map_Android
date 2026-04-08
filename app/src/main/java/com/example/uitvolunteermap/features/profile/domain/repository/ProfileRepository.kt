package com.example.uitvolunteermap.features.profile.domain.repository

import com.example.uitvolunteermap.features.profile.domain.entity.ProfileInfo

/**
 * Interface định nghĩa các phương thức thao tác với dữ liệu Profile.
 */
interface ProfileRepository {
    // Lấy thông tin hồ sơ hiện tại
    suspend fun getProfile(): Result<ProfileInfo>

    // Cập nhật thông tin hồ sơ
    suspend fun updateProfile(profile: ProfileInfo): Result<ProfileInfo>
}