package com.example.uitvolunteermap.features.campaign.domain.usecase

import com.example.uitvolunteermap.features.campaign.domain.repository.TeamFormationDetailRepository
import javax.inject.Inject

class GetTeamFormationDetailUseCase @Inject constructor(
    private val teamFormationDetailRepository: TeamFormationDetailRepository
) {
    suspend operator fun invoke(teamId: Int) =
        teamFormationDetailRepository.getTeamFormationDetail(teamId)
}
