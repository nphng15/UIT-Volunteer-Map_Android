package com.example.uitvolunteermap.features.admin.team.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.core.network.apiCallUnit
import com.example.uitvolunteermap.core.network.toAppError
import com.example.uitvolunteermap.features.admin.team.data.datasource.AddAdminTeamAttachmentsRequest
import com.example.uitvolunteermap.features.admin.team.data.datasource.AddAdminTeamMemberRequest
import com.example.uitvolunteermap.features.admin.team.data.datasource.AdminTeamApiService
import com.example.uitvolunteermap.features.admin.team.data.datasource.AdminTeamAttachmentPayloadDto
import com.example.uitvolunteermap.features.admin.team.data.datasource.CreateAdminTeamRequest
import com.example.uitvolunteermap.features.admin.team.data.datasource.UpdateAdminTeamRequest
import com.example.uitvolunteermap.features.admin.team.data.mapper.toDomain
import com.example.uitvolunteermap.features.admin.team.domain.entity.AdminTeam
import com.example.uitvolunteermap.features.admin.team.domain.repository.AdminTeamRepository
import com.example.uitvolunteermap.core.common.error.toAppError
import javax.inject.Inject

class RemoteAdminTeamRepository @Inject constructor(
    private val api: AdminTeamApiService
) : AdminTeamRepository {

    override suspend fun getTeams(): AppResult<List<AdminTeam>> = apiCall(
        request = { api.getTeams() },
        map = { teams -> teams.map { it.toDomain() } }
    )

    override suspend fun getTeam(teamId: Int): AppResult<AdminTeam> = apiCall(
        request = { api.getTeam(teamId) },
        map = { it.toDomain() }
    )

    // POST trả raw entity shape khác GET → chỉ quan tâm success/error, bỏ qua body. Caller refetch.
    override suspend fun createTeam(
        teamName: String,
        leaderId: Int,
        campaignId: Int,
        description: String?,
        imageUrl: String?
    ): AppResult<Unit> = apiCall(
        request = {
            api.createTeam(
                CreateAdminTeamRequest(
                    teamName = teamName,
                    leaderId = leaderId,
                    campaignId = campaignId,
                    description = description,
                    imageUrl = imageUrl
                )
            )
        },
        map = { Unit }
    )

    // PUT chỉ đổi teamName/description/imageUrl — leaderId/campaignId không thể thay đổi qua API này.
    override suspend fun updateTeam(
        teamId: Int,
        teamName: String?,
        description: String?,
        imageUrl: String?
    ): AppResult<Unit> = apiCall(
        request = {
            api.updateTeam(
                teamId = teamId,
                body = UpdateAdminTeamRequest(
                    teamName = teamName,
                    description = description,
                    imageUrl = imageUrl
                )
            )
        },
        map = { Unit }
    )

    override suspend fun addTeamAttachments(
        teamId: Int,
        imageUrls: List<String>
    ): AppResult<Unit> = apiCallUnit {
        api.addTeamAttachments(
            teamId = teamId,
            body = AddAdminTeamAttachmentsRequest(
                attachments = imageUrls.mapIndexed { index, imageUrl ->
                    AdminTeamAttachmentPayloadDto(
                        imageUrl = imageUrl,
                        position = index + 1
                    )
                }
            )
        )
    }

    override suspend fun addTeamMember(teamId: Int, userId: Int): AppResult<Unit> = apiCallUnit {
        api.addTeamMember(teamId = teamId, body = AddAdminTeamMemberRequest(userId))
    }

    override suspend fun removeTeamMember(teamId: Int, userId: Int): AppResult<Unit> = apiCallUnit {
        api.removeTeamMember(teamId = teamId, userId = userId)
    }

    // DELETE trả về { success, data:null, message }. apiCall coi data==null là lỗi nên ta
    // tự xử lý: chỉ cần success=true là thành công, bỏ qua data.
    override suspend fun deleteTeam(teamId: Int): AppResult<Unit> = try {
        val response = api.deleteTeam(teamId)
        if (response.success) {
            AppResult.Success(Unit)
        } else {
            AppResult.Error(response.toAppError())
        }
    } catch (throwable: Throwable) {
        AppResult.Error(throwable.toAppError())
    }
}
