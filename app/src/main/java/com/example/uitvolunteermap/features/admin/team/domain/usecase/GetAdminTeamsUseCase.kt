package com.example.uitvolunteermap.features.admin.team.domain.usecase

import com.example.uitvolunteermap.features.admin.team.domain.repository.AdminTeamRepository
import javax.inject.Inject

class GetAdminTeamsUseCase @Inject constructor(
    private val repository: AdminTeamRepository
) {
    suspend operator fun invoke() = repository.getTeams()
}
