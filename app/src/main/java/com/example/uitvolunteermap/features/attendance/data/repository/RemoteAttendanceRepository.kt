package com.example.uitvolunteermap.features.attendance.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.attendance.data.datasource.AttendanceApiService
import com.example.uitvolunteermap.features.attendance.data.mapper.toDomain
import com.example.uitvolunteermap.features.attendance.domain.entity.ManagedTeam
import com.example.uitvolunteermap.features.attendance.domain.entity.TeamAttendance
import com.example.uitvolunteermap.features.attendance.domain.repository.AttendanceRepository
import javax.inject.Inject

class RemoteAttendanceRepository @Inject constructor(
    private val attendanceApiService: AttendanceApiService
) : AttendanceRepository {

    override suspend fun getManagedTeams(): AppResult<List<ManagedTeam>> = apiCall(
        request = { attendanceApiService.getManagedTeams() },
        map = { teams -> teams.map { it.toDomain() } }
    )

    override suspend fun getTeamAttendance(
        teamId: Int?,
        date: String?
    ): AppResult<TeamAttendance> = apiCall(
        request = { attendanceApiService.getTeamAttendance(teamId = teamId, date = date) },
        map = { it.toDomain() }
    )
}
