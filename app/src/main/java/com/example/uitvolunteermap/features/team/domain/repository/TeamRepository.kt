package com.example.uitvolunteermap.features.team.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.team.domain.model.Team

interface TeamRepository {
    suspend fun getTeams(): AppResult<List<Team>>
    suspend fun createTeam(name: String, description: String): AppResult<Unit>
    suspend fun updateTeam(id: Int, name: String): AppResult<Unit>
    suspend fun deleteTeam(id: Int): AppResult<Unit>
}