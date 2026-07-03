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
    val leaderOptions: List<AdminTeamLeaderOption> = emptyList(),
    val volunteerOptions: List<AdminTeamMemberOption> = emptyList(),
    val campaignOptions: List<AdminTeamCampaignOption> = emptyList(),
    val isLoadingFormOptions: Boolean = false,
    val formState: AdminTeamFormState? = null,
    val memberFormState: AdminTeamMemberFormState? = null,
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

data class AdminTeamMemberFormState(
    val teamId: Int,
    val teamName: String,
    val selectedUserIds: Set<Int> = emptySet(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

data class AdminTeamMemberOption(
    val userId: Int,
    val displayName: String,
    val subtitle: String
)

data class AdminTeamLeaderOption(
    val userId: Int,
    val displayName: String,
    val subtitle: String
)

data class AdminTeamCampaignOption(
    val campaignId: Int,
    val name: String,
    val dateRange: String
)

enum class AdminTeamFormMode { Create, Edit }

/**
 * Trạng thái form trong ModalBottomSheet.
 *
 * Chế độ Create hiển thị đủ teamName/leader/campaign/description/imageUrl.
 * Chế độ Edit CHỈ hiển thị teamName/description/imageUrl (API không cho đổi leader/campaign),
 * nên selectedLeaderId/selectedCampaignId không dùng đến khi sửa.
 */
data class AdminTeamFormState(
    val mode: AdminTeamFormMode,
    val teamId: Int? = null,
    val teamName: String = "",
    val selectedLeaderId: Int? = null,
    val selectedCampaignId: Int? = null,
    val description: String = "",
    val imageUrl: String = "",
    val isSubmitting: Boolean = false
)
