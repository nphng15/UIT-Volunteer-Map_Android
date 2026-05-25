package com.example.uitvolunteermap.features.auth.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.auth.data.remote.AuthApiService
import com.example.uitvolunteermap.features.auth.data.remote.LoginRequest
import com.example.uitvolunteermap.features.auth.data.remote.LoginResponseDto
import com.example.uitvolunteermap.features.auth.data.remote.LoginUserDto
import com.example.uitvolunteermap.features.auth.data.remote.VerifyTokenDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteAuthRepositoryTest {

    @Test
    fun login_maps_backend_username_token_and_role() = runTest {
        val api = FakeAuthApiService()
        val repository = RemoteAuthRepository(api)

        val result = repository.login("leader01", "leader123")

        assertTrue(result is AppResult.Success)
        val user = (result as AppResult.Success).data
        assertEquals(LoginRequest("leader01", "leader123"), api.lastRequest)
        assertEquals("12", user.id)
        assertEquals(12, user.accountId)
        assertEquals("leader01", user.username)
        assertEquals("jwt_token_here", user.token)
        assertEquals(UserRole.LEADER, user.role)
    }

    private class FakeAuthApiService : AuthApiService {
        var lastRequest: LoginRequest? = null

        override suspend fun login(body: LoginRequest): ApiEnvelope<LoginResponseDto> {
            lastRequest = body
            return ApiEnvelope(
                success = true,
                data = LoginResponseDto(
                    token = "jwt_token_here",
                    user = LoginUserDto(
                        accId = 12,
                        username = "leader01",
                        role = "leader"
                    )
                ),
                message = null,
                error = null
            )
        }

        override suspend fun logout(): ApiEnvelope<Unit> =
            ApiEnvelope(success = true, data = Unit, message = null, error = null)

        override suspend fun verify(): ApiEnvelope<VerifyTokenDto> =
            ApiEnvelope(success = true, data = VerifyTokenDto(isExpired = false), message = null, error = null)
    }
}
