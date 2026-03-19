package com.example.uitvolunteermap.features.home.domain.usecase

import com.example.uitvolunteermap.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeInfoUseCase @Inject constructor(private val homeRepository: HomeRepository) {
    suspend operator fun invoke() = homeRepository.getHomeInfo()
}
