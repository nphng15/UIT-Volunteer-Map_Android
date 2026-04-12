package com.example.uitvolunteermap.features.home.domain.usecase

import com.example.uitvolunteermap.features.home.domain.repository.VolunteerHomeRepository
import javax.inject.Inject

class GetVolunteerHomeContentUseCase @Inject constructor(
    private val volunteerHomeRepository: VolunteerHomeRepository
) {
    suspend operator fun invoke() = volunteerHomeRepository.getVolunteerHomeContent()
}
