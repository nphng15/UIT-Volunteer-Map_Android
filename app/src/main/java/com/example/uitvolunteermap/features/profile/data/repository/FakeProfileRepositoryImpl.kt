package com.example.uitvolunteermap.features.profile.data.repository

import com.example.uitvolunteermap.core.UserRole
import com.example.uitvolunteermap.core.common.result.AppResult //
import com.example.uitvolunteermap.features.profile.domain.entity.ProfileInfo
import com.example.uitvolunteermap.features.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Thực thi giả của ProfileRepository để phục vụ quá trình phát triển UI.
 * Một khi nguồn dữ liệu thật sẵn sàng, hãy thay thế bằng implementation thực tế.
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
        role = UserRole.LEADER, // Để LEADER để bạn có thể test tính năng Edit
    )

    override suspend fun getProfile(): AppResult<ProfileInfo> {
        delay(1000)
        // Trả về AppResult.Success thay vì Result.success
        return AppResult.Success(fakeUser)
    }

    override suspend fun updateProfile(profile: ProfileInfo): AppResult<ProfileInfo> {
        delay(1000)
        fakeUser = profile
        return AppResult.Success(fakeUser)
    }

    override suspend fun logout(): AppResult<Unit> {
        delay(500)
        sessionToken = null
        return AppResult.Success(Unit)
    }
}