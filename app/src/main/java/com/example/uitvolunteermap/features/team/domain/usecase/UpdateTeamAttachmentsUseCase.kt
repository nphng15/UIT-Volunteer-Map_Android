package com.example.uitvolunteermap.features.team.domain.usecase

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.team.domain.repository.TeamRepository
import javax.inject.Inject

// Định nghĩa Object Update theo API
data class AttachmentUpdate(
    val url: String,
    val position: Int
)

class UpdateTeamAttachmentsUseCase @Inject constructor(
    private val repository: TeamRepository
) {
    suspend operator fun invoke(teamId: Int, attachments: List<AttachmentUpdate>): AppResult<Unit> {
        // Gọi xuống repository để thực hiện PUT /teams/:id/attachments
        return repository.updateAttachments(teamId, attachments)
    }
}