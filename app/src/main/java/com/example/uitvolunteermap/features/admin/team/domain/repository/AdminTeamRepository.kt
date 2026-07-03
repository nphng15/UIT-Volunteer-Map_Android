package com.example.uitvolunteermap.features.admin.team.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.team.domain.entity.AdminTeam

/**
 * Hợp đồng dữ liệu cho khu quản trị team.
 *
 * Các thao tác mutate (create/update/delete) trả về [Unit] vì phản hồi POST/PUT của backend
 * là raw entity shape khác GET — không đáng tin để map. Caller phải refetch [getTeams] sau khi
 * mutate thành công để lấy trạng thái nhất quán.
 */
interface AdminTeamRepository {
    suspend fun getTeams(): AppResult<List<AdminTeam>>

    suspend fun getTeam(teamId: Int): AppResult<AdminTeam>

    suspend fun createTeam(
        teamName: String,
        leaderId: Int,
        campaignId: Int,
        description: String? = null,
        imageUrl: String? = null
    ): AppResult<Unit>

    suspend fun updateTeam(
        teamId: Int,
        teamName: String? = null,
        description: String? = null,
        imageUrl: String? = null
    ): AppResult<Unit>

    suspend fun addTeamAttachments(
        teamId: Int,
        imageUrls: List<String>
    ): AppResult<Unit>

    suspend fun addTeamMember(teamId: Int, userId: Int): AppResult<Unit>

    suspend fun removeTeamMember(teamId: Int, userId: Int): AppResult<Unit>

    suspend fun deleteTeam(teamId: Int): AppResult<Unit>
}
