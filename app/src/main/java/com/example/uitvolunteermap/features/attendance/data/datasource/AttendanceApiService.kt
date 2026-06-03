package com.example.uitvolunteermap.features.attendance.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.attendance.data.model.ManagedTeamDto
import com.example.uitvolunteermap.features.attendance.data.model.TeamAttendanceDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AttendanceApiService {

    @GET("checkin/team/managed")
    suspend fun getManagedTeams(): ApiEnvelope<List<ManagedTeamDto>>

    @GET("checkin/team")
    suspend fun getTeamAttendance(
        @Query("teamId") teamId: Int? = null,
        @Query("date") date: String? = null
    ): ApiEnvelope<TeamAttendanceDto>
}
