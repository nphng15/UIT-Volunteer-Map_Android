package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamActivityItem
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamHeroCard
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamLeader
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
                appName = "UIT · Tình nguyện",
                appSubtitle = "Trang tổng hợp chiến dịch và điểm đến tình nguyện",
                title = "Đội nấu cơm",
                description = "Đội hình tình nguyện phụ trách điều phối nhân sự, kết nối địa bàn và theo dõi tiến độ hoạt động trong suốt chiến dịch.",
                heroCards = listOf(
                    TeamHeroCard(label = "Ảnh", isPrimary = false),
                    TeamHeroCard(label = "Ảnh chính", isPrimary = true),
                    TeamHeroCard(label = "Ảnh", isPrimary = false)
                ),
                leaders = listOf(
                    TeamLeader(id = 1, initials = "LT", role = "Đội trưởng", name = "Lê Thanh"),
                    TeamLeader(id = 2, initials = "MN", role = "Điều phối", name = "Minh Ngọc"),
                    TeamLeader(id = 3, initials = "TK", role = "Hậu cần", name = "Thu Kỳ")
                ),
                activities = listOf(
                    TeamActivityItem(id = 0, label = "+", isAddButton = true),
                    TeamActivityItem(id = 1, label = "Ảnh 1", isAddButton = false),
                    TeamActivityItem(id = 2, label = "Ảnh 2", isAddButton = false),
                    TeamActivityItem(id = 3, label = "Ảnh 3", isAddButton = false),
                    TeamActivityItem(id = 4, label = "Ảnh 4", isAddButton = false),
                    TeamActivityItem(id = 5, label = "Ảnh 5", isAddButton = false)
                )
            )

            102 -> TeamFormationDetail(
                id = teamId,
                appName = "UIT · Tình nguyện",
                appSubtitle = null,
                title = "Đội giáo dục",
                description = "Đội hình phụ trách các nội dung sinh hoạt, hướng dẫn học sinh và kết nối bài viết cập nhật từ từng cụm hoạt động.",
                heroCards = listOf(
                    TeamHeroCard(label = "Ảnh", isPrimary = false),
                    TeamHeroCard(label = "Ảnh chính", isPrimary = true),
                    TeamHeroCard(label = "Ảnh", isPrimary = false)
                ),
                leaders = listOf(
                    TeamLeader(id = 4, initials = "HL", role = "Chỉ huy", name = "Hải Lâm"),
                    TeamLeader(id = 5, initials = "PT", role = "Nội dung", name = "Phương Trinh"),
                    TeamLeader(id = 6, initials = "NA", role = "Điều phối", name = "Ngọc Anh")
                ),
                activities = listOf(
                    TeamActivityItem(id = 0, label = "+", isAddButton = true),
                    TeamActivityItem(id = 1, label = "Ảnh 1", isAddButton = false),
                    TeamActivityItem(id = 2, label = "Ảnh 2", isAddButton = false),
                    TeamActivityItem(id = 3, label = "Ảnh 3", isAddButton = false),
                    TeamActivityItem(id = 4, label = "Ảnh 4", isAddButton = false),
                    TeamActivityItem(id = 5, label = "Ảnh 5", isAddButton = false)
                )
            )

            103 -> TeamFormationDetail(
                id = teamId,
                appName = "UIT · Tình nguyện",
                appSubtitle = null,
                title = "Đội truyền thông",
                description = "Đội hình phụ trách ghi nhận hình ảnh, cập nhật bài viết nhanh và đồng bộ truyền thông cho từng địa điểm trong chiến dịch.",
                heroCards = listOf(
                    TeamHeroCard(label = "Ảnh", isPrimary = false),
                    TeamHeroCard(label = "Ảnh chính", isPrimary = true),
                    TeamHeroCard(label = "Ảnh", isPrimary = false)
                ),
                leaders = listOf(
                    TeamLeader(id = 7, initials = "TH", role = "Chỉ huy", name = "Thảo Hà"),
                    TeamLeader(id = 8, initials = "QV", role = "Nội dung", name = "Quốc Việt"),
                    TeamLeader(id = 9, initials = "AL", role = "Media", name = "Anh Linh")
                ),
                activities = listOf(
                    TeamActivityItem(id = 0, label = "+", isAddButton = true),
                    TeamActivityItem(id = 1, label = "Ảnh 1", isAddButton = false),
                    TeamActivityItem(id = 2, label = "Ảnh 2", isAddButton = false),
                    TeamActivityItem(id = 3, label = "Ảnh 3", isAddButton = false),
                    TeamActivityItem(id = 4, label = "Ảnh 4", isAddButton = false),
                    TeamActivityItem(id = 5, label = "Ảnh 5", isAddButton = false)
                )
            )

            else -> null
        }

        return detail?.let { AppResult.Success(it) } ?: AppResult.Error(
            AppError.NotFound(message = "Không tìm thấy đội hình phù hợp.")
        )
    }
}
