package com.example.uitvolunteermap.features.auth.data.repository

import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.auth.domain.entity.AuthUser
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class FakeAuthRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<AuthUser> =
        withContext(ioDispatcher) {
            delay(AUTH_DELAY_MILLIS)

            if (email.equals(DEMO_EMAIL, ignoreCase = true) && password == DEMO_PASSWORD) {
                AppResult.Success(
                    AuthUser(
                        id = "user_uit_volunteer",
                        email = DEMO_EMAIL,
                        displayName = "UIT Volunteer",
                    )
                )
            } else {
                AppResult.Error(
                    AppError.Unauthorized(
                        message = "Email hoac mat khau khong dung.",
                    )
                )
            }
        }

    private companion object {
        private const val AUTH_DELAY_MILLIS = 900L
        private const val DEMO_EMAIL = "volunteer@uit.edu.vn"
        private const val DEMO_PASSWORD = "volunteer123"
    }
}
