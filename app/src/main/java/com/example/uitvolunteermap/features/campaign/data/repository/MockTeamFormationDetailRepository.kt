package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.model.TeamActivityItem
import com.example.uitvolunteermap.features.campaign.domain.model.TeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.model.TeamHeroCard
import com.example.uitvolunteermap.features.campaign.domain.model.TeamLeader
import com.example.uitvolunteermap.features.campaign.domain.repository.TeamFormationDetailRepository
import javax.inject.Inject

class MockTeamFormationDetailRepository @Inject constructor() : TeamFormationDetailRepository {

    override suspend fun getTeamFormationDetail(teamId: Int): AppResult<TeamFormationDetail> {
        // TODO: Replace with real implementation once backend is reachable.
        //
        // Real: val team = teamApiService.getTeam(teamId).data!!.toDomain()
        //   → GET /teams/:id — public, trả về { teamId, teamName, description, imageUrl, leaders }
        //
        // Lưu ý: heroCards và activities không có trong GET /teams/:id hiện tại.
        // Hai field này sẽ cần endpoint riêng hoặc được bổ sung sau khi backend mở rộng contract.
        // Mapper TeamDto.toDomain() đã sẵn sàng trong data/mapper/TeamMapper.kt (commented).
        val detail = when (teamId) {
            101 -> TeamFormationDetail(
                id = teamId,
                appName = "UIT Volunteer Map",
                appSubtitle = "Trang tong hop chien dich va diem den tinh nguyen",
                title = "Doi nau com",
                description = "Doi hinh tinh nguyen phu trach dieu phoi nhan su, ket noi dia ban va theo doi tien do hoat dong trong suot chien dich.",
                heroCards = listOf(
                    TeamHeroCard(label = "Anh", isPrimary = false),
                    TeamHeroCard(label = "Anh chinh", isPrimary = true),
                    TeamHeroCard(label = "Anh", isPrimary = false)
                ),
                leaders = listOf(
                    TeamLeader(id = 1, initials = "LT", role = "Chi huy truong", name = "Le Thanh"),
                    TeamLeader(id = 2, initials = "MN", role = "Dieu phoi", name = "Minh Ngoc"),
                    TeamLeader(id = 3, initials = "TK", role = "Hau can", name = "Thu Ky")
                ),
                activities = listOf(
                    TeamActivityItem(id = 0, label = "+", isAddButton = true),
                    TeamActivityItem(id = 1, label = "Anh 1", isAddButton = false),
                    TeamActivityItem(id = 2, label = "Anh 2", isAddButton = false),
                    TeamActivityItem(id = 3, label = "Anh 3", isAddButton = false),
                    TeamActivityItem(id = 4, label = "Anh 4", isAddButton = false),
                    TeamActivityItem(id = 5, label = "Anh 5", isAddButton = false)
                )
            )

            102 -> TeamFormationDetail(
                id = teamId,
                appName = "UIT Volunteer Map",
                appSubtitle = null,
                title = "Doi giao duc",
                description = "Doi hinh phu trach cac noi dung sinh hoat, huong dan hoc sinh va ket noi bai viet cap nhat tu tung cum hoat dong.",
                heroCards = listOf(
                    TeamHeroCard(label = "Anh", isPrimary = false),
                    TeamHeroCard(label = "Anh chinh", isPrimary = true),
                    TeamHeroCard(label = "Anh", isPrimary = false)
                ),
                leaders = listOf(
                    TeamLeader(id = 4, initials = "HL", role = "Chi huy", name = "Hai Lam"),
                    TeamLeader(id = 5, initials = "PT", role = "Noi dung", name = "Phuong Trinh"),
                    TeamLeader(id = 6, initials = "NA", role = "Dieu phoi", name = "Ngoc Anh")
                ),
                activities = listOf(
                    TeamActivityItem(id = 0, label = "+", isAddButton = true),
                    TeamActivityItem(id = 1, label = "Anh 1", isAddButton = false),
                    TeamActivityItem(id = 2, label = "Anh 2", isAddButton = false),
                    TeamActivityItem(id = 3, label = "Anh 3", isAddButton = false),
                    TeamActivityItem(id = 4, label = "Anh 4", isAddButton = false),
                    TeamActivityItem(id = 5, label = "Anh 5", isAddButton = false)
                )
            )

            103 -> TeamFormationDetail(
                id = teamId,
                appName = "UIT Volunteer Map",
                appSubtitle = null,
                title = "Doi truyen thong",
                description = "Doi hinh phu trach ghi nhan hinh anh, cap nhat bai viet nhanh va dong bo truyen thong cho tung dia diem trong chien dich.",
                heroCards = listOf(
                    TeamHeroCard(label = "Anh", isPrimary = false),
                    TeamHeroCard(label = "Anh chinh", isPrimary = true),
                    TeamHeroCard(label = "Anh", isPrimary = false)
                ),
                leaders = listOf(
                    TeamLeader(id = 7, initials = "TH", role = "Chi huy", name = "Thao Ha"),
                    TeamLeader(id = 8, initials = "QV", role = "Noi dung", name = "Quoc Viet"),
                    TeamLeader(id = 9, initials = "AL", role = "Media", name = "Anh Linh")
                ),
                activities = listOf(
                    TeamActivityItem(id = 0, label = "+", isAddButton = true),
                    TeamActivityItem(id = 1, label = "Anh 1", isAddButton = false),
                    TeamActivityItem(id = 2, label = "Anh 2", isAddButton = false),
                    TeamActivityItem(id = 3, label = "Anh 3", isAddButton = false),
                    TeamActivityItem(id = 4, label = "Anh 4", isAddButton = false),
                    TeamActivityItem(id = 5, label = "Anh 5", isAddButton = false)
                )
            )

            else -> null
        }

        return detail?.let { AppResult.Success(it) } ?: AppResult.Error(
            AppError.NotFound(message = "Khong tim thay doi hinh phu hop.")
        )
    }
}
