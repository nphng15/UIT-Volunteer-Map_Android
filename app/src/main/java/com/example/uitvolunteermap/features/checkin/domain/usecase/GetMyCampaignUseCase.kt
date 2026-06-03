package com.example.uitvolunteermap.features.checkin.domain.usecase

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.checkin.domain.entity.MyCampaign
import com.example.uitvolunteermap.features.checkin.domain.repository.CheckinRepository
import javax.inject.Inject

class GetMyCampaignUseCase @Inject constructor(
    private val repository: CheckinRepository
) {
    suspend operator fun invoke(): AppResult<MyCampaign?> = repository.getMyCampaign()
}
