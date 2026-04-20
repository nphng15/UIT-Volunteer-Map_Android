package com.example.uitvolunteermap.features.team.domain.usecase

import com.example.uitvolunteermap.features.team.domain.repository.TeamRepository
import javax.inject.Inject

class GetTeamsUseCase @Inject constructor(
    private val repository: TeamRepository
) {
    suspend operator fun invoke() = repository.getTeams()
}