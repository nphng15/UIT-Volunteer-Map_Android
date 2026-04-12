package com.example.uitvolunteermap.features.profile.data.repository

import com.example.uitvolunteermap.features.profile.domain.entity.ProfileInfo
import com.example.uitvolunteermap.features.profile.domain.entity.UserRole
import com.example.uitvolunteermap.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Thực thi giả của ProfileRepository để phục vụ quá trình phát triển UI.
 */
class FakeProfileRepositoryImpl @Inject constructor() : ProfileRepository {

    private var sessionToken: String? = "fake-session-token"

    private var fakeUser = ProfileInfo(
        userId = "15",
        fullName = "Nguyễn Thị Thanh Hiền",
        mssv = "23520000",
        className = "IS2023",
        email = "student@gm.uit.edu.vn",
        phoneNumber = "0912345678",
        createdAt = "2026-03-01 09:30:00",
        role = UserRole.ADMIN,
    )

    override suspend fun getProfile(): Result<ProfileInfo> {
        // Giả lập độ trễ mạng
        delay(1000)
        return Result.success(fakeUser)
    }

    override suspend fun updateProfile(profile: ProfileInfo): Result<ProfileInfo> {
        delay(1000)
        // Cập nhật dữ liệu giả trong bộ nhớ tạm
        fakeUser = profile
        return Result.success(fakeUser)
    }

    override suspend fun logout(): Result<Unit> {
        delay(500)
        sessionToken = null
        return Result.success(Unit)
    }
}