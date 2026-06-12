package com.example.uitvolunteermap.features.attendance.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.attendance.domain.entity.ManagedTeam
import com.example.uitvolunteermap.features.attendance.domain.entity.TeamAttendance

interface AttendanceRepository {
    suspend fun getManagedTeams(): AppResult<List<ManagedTeam>>
    suspend fun getTeamAttendance(teamId: Int?, date: String?): AppResult<TeamAttendance>
}
