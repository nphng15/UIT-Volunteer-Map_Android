package com.example.uitvolunteermap.features.team.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.team.domain.repository.TeamRepository
import javax.inject.Inject

class ManageTeamUseCase @Inject constructor(
    private val repository: TeamRepository
) {
    // Giả sử lấy Role từ một SessionManager nào đó
    suspend fun createTeam(role: UserRole, name: String, desc: String): AppResult<Unit> {
        return if (role == UserRole.VOLUNTEER) {
            repository.createTeam(name, desc)
        } else {
            AppResult.Error(AppError.Unauthorized("Chỉ Volunteer mới có quyền tạo Team"))
        }
    }

    suspend fun deleteTeam(role: UserRole, teamId: Int): AppResult<Unit> {
        return if (role == UserRole.VOLUNTEER) {
            repository.deleteTeam(teamId)
        } else {
            AppResult.Error(AppError.Unauthorized("Bạn không có quyền xóa Team này"))
        }
    }
}