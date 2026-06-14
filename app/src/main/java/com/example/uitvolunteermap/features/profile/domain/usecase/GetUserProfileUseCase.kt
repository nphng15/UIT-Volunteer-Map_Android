package com.example.uitvolunteermap.features.profile.domain.usecase

import com.example.uitvolunteermap.features.profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke() = repository.getProfile()
}
