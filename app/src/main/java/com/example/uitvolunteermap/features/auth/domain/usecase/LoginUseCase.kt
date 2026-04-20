package com.example.uitvolunteermap.features.auth.domain.usecase

import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String) =
        authRepository.login(
            email = email.trim(),
            password = password,
        )
}
