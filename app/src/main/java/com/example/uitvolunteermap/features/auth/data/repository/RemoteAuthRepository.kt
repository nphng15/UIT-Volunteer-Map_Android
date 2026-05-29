package com.example.uitvolunteermap.features.auth.data.repository

import com.example.uitvolunteermap.BuildConfig
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.core.session.UserRole
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
        // Backend chưa cấp tài khoản role volunteer. Để test trải nghiệm tình nguyện viên,
        // tài khoản test dưới đây mượn token thật của một leader rồi hiển thị role = VOLUNTEER.
        // Chỉ bật ở bản debug để không trở thành cửa hậu trên bản release.
        if (BuildConfig.DEBUG &&
            email.trim().equals(TEST_VOLUNTEER_USERNAME, ignoreCase = true) &&
            password == TEST_VOLUNTEER_PASSWORD
        ) {
            return apiCall(
                request = {
                    authApiService.login(
                        LoginRequest(username = BACKING_USERNAME, password = BACKING_PASSWORD)
                    )
                },
                map = { it.toDomain().copy(role = UserRole.VOLUNTEER) }
            )
        }

        return apiCall(
            request = { authApiService.login(LoginRequest(username = email, password = password)) },
            map = { it.toDomain() }
        )
    }

    override suspend fun logout(): AppResult<Unit> {
        return apiCall(
            request = { authApiService.logout() },
            map = { }
        )
    }

    override suspend fun isTokenValid(): Boolean {
        return runCatching {
            val response = authApiService.verify()
            response.data?.isExpired == false
        }.getOrDefault(false)
    }

    private companion object {
        // Tài khoản test bạn gõ ở màn đăng nhập.
        const val TEST_VOLUNTEER_USERNAME = "volunteer"
        const val TEST_VOLUNTEER_PASSWORD = "volunteer123"

        // Credential leader CÓ THẬT trên backend, dùng để mượn token.
        // Đổi 2 dòng này nếu tài khoản leader thật của bạn khác.
        const val BACKING_USERNAME = "leader01"
        const val BACKING_PASSWORD = "leader123"
    }
}
