package com.example.uitvolunteermap.features.team.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.team.domain.model.Team
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

    // 1. Hàm Cập nhật mới thêm vào
    suspend fun updateTeam(
        role: UserRole,
        id: Int,
        name: String,
        description: String,
        imageUrl: String?
    ): AppResult<Unit> {
        return if (role == UserRole.VOLUNTEER) {
            // Leader chỉ được update team của mình (logic ownership đã có ở backend)
            repository.updateTeam(id, name, description, imageUrl)
        } else {
            AppResult.Error(AppError.Unauthorized("Chỉ Leader mới có quyền chỉnh sửa Team"))
        }
    }

    suspend fun getTeamById(id: Int): AppResult<Team> {
        return when (val result = repository.getTeams()) {
            is AppResult.Success -> {
                val team = result.data.find { it.id == id }
                if (team != null) AppResult.Success(team)
                else AppResult.Error(AppError.NotFound("Không tìm thấy đội"))
            }
            is AppResult.Error -> AppResult.Error(result.error)
        }
    }
}