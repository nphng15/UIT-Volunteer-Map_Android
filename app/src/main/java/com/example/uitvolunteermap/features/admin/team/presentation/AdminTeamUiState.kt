package com.example.uitvolunteermap.features.admin.team.presentation

/**
 * Trạng thái màn hình quản lý đội.
 *
 * [formState] khác null khi sheet tạo/sửa đang mở. [pendingDeleteId] khác null khi
 * dialog xác nhận xóa đang mở.
 */
data class AdminTeamUiState(
    val teams: List<AdminTeamUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val canManageTeams: Boolean = false,
    val formState: AdminTeamFormState? = null,
    val pendingDeleteId: Int? = null,
    val isDeleting: Boolean = false
) {
    // Lọc theo tên đội hoặc tên nhóm trưởng (không phân biệt hoa thường).
    val visibleTeams: List<AdminTeamUiModel>
        get() {
            val q = searchQuery.trim()
            if (q.isEmpty()) return teams
            return teams.filter { team ->
                team.teamName.contains(q, ignoreCase = true) ||
                    team.leadersLabel.contains(q, ignoreCase = true)
            }
        }
}

data class AdminTeamUiModel(
    val teamId: Int,
    val teamName: String,
    val description: String,
    val imageUrl: String?,
    val leadersLabel: String,
    val memberCount: Int,
    val isCheckInConfigured: Boolean
)

enum class AdminTeamFormMode { Create, Edit }

/**
 * Trạng thái form trong ModalBottomSheet.
 *
 * Chế độ Create hiển thị đủ teamName/leaderId/campaignId/description/imageUrl.
 * Chế độ Edit CHỈ hiển thị teamName/description/imageUrl (API không cho đổi leader/campaign),
 * nên leaderId/campaignId không dùng đến khi sửa.
 */
data class AdminTeamFormState(
    val mode: AdminTeamFormMode,
    val teamId: Int? = null,
    val teamName: String = "",
    val leaderId: String = "",
    val campaignId: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isSubmitting: Boolean = false
)
