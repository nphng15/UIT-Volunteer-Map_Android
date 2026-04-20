package com.example.uitvolunteermap.features.team.data.repository

import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.team.domain.model.Team
import com.example.uitvolunteermap.features.team.domain.repository.TeamRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class FakeTeamRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : TeamRepository {

    private var fakeTeams = mutableListOf(
        Team(1, "Media Team", "Chuyên quay phim, chụp ảnh", null, "Nguyễn Văn A"),
        Team(2, "Logistics", "Hỗ trợ hậu cần, chuẩn bị đồ đạc", null, "Trần Thị B")
    )

    override suspend fun getTeams(): AppResult<List<Team>> = withContext(ioDispatcher) {
        delay(800L)
        AppResult.Success(fakeTeams)
    }

    override suspend fun createTeam(name: String, description: String): AppResult<Unit> = withContext(ioDispatcher) {
        delay(500L)
        fakeTeams.add(Team(fakeTeams.size + 1, name, description, null, "Current User"))
        AppResult.Success(Unit)
    }

    override suspend fun updateTeam(id: Int, name: String): AppResult<Unit> = withContext(ioDispatcher) {
        delay(500L)
        val index = fakeTeams.indexOfFirst { it.id == id }
        if (index != -1) {
            fakeTeams[index] = fakeTeams[index].copy(name = name)
        }
        AppResult.Success(Unit)
    }

    override suspend fun deleteTeam(id: Int): AppResult<Unit> = withContext(ioDispatcher) {
        delay(500L)
        fakeTeams.removeAll { it.id == id }
        AppResult.Success(Unit)
    }
}