package com.example.uitvolunteermap.features.attendance.domain.usecase

import com.example.uitvolunteermap.features.attendance.domain.repository.AttendanceRepository
import javax.inject.Inject

class GetManagedTeamsUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke() =
        repository.getManagedTeams()
}
