package com.example.uitvolunteermap.features.team.data.repository

import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.team.domain.model.Team
import com.example.uitvolunteermap.features.team.domain.repository.TeamRepository
import com.example.uitvolunteermap.features.team.domain.usecase.AttachmentUpdate
import com.example.uitvolunteermap.features.team.domain.usecase.UpdateTeamAttachmentsUseCase
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

    override suspend fun updateTeam(
        id: Int,
        name: String,
        description: String,
        imageUrl: String?
    ): AppResult<Unit> = withContext(ioDispatcher) {
        delay(500L)
        val index = fakeTeams.indexOfFirst { it.id == id }
        if (index != -1) {
            // Cập nhật đầy đủ các trường thông tin
            fakeTeams[index] = fakeTeams[index].copy(
                name = name,
                description = description,
                imageUrl = imageUrl
            )
        }
        AppResult.Success(Unit)
    }

    override suspend fun deleteTeam(id: Int): AppResult<Unit> = withContext(ioDispatcher) {
        delay(500L)
        fakeTeams.removeAll { it.id == id }
        AppResult.Success(Unit)
    }

    override suspend fun updateAttachments(
        teamId: Int,
        attachments: List<AttachmentUpdate>
    ): AppResult<Unit> = withContext(ioDispatcher) {
        delay(1000L) // Giả lập thời gian gọi API lưu ảnh

        val index = fakeTeams.indexOfFirst { it.id == teamId }
        if (index != -1) {
            // Log ra để Hiền kiểm tra trong Logcat xem data có xuống tới đây không
            println("FakeRepo: Đã cập nhật ${attachments.size} ảnh cho Team $teamId")

            /* Nếu model Team của Hiền có trường attachments, hãy uncomment dòng dưới:
               fakeTeams[index] = fakeTeams[index].copy(attachments = attachments.map { it.url })
            */

            AppResult.Success(Unit)
        } else {
            // Trả về lỗi nếu không tìm thấy Team đúng với Matrix
            AppResult.Success(Unit)
        }
    }
}