package com.example.uitvolunteermap.features.attendance.domain.usecase

import com.example.uitvolunteermap.features.attendance.domain.repository.AttendanceRepository
import javax.inject.Inject

class GetTeamAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(teamId: Int? = null, date: String? = null) =
        repository.getTeamAttendance(teamId, date)
}
