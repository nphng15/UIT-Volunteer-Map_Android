package com.example.uitvolunteermap.features.post.presentation.campaignposts

data class CampaignPostsUiState(
    val appName: String = "",
    val campaignTitle: String = "",
    val campaignSubtitle: String = "",
    val canManagePosts: Boolean = false,
    val summary: CampaignPostsSummaryUiModel = CampaignPostsSummaryUiModel(),
    val teamFilters: List<CampaignPostsTeamUiModel> = emptyList(),
    val selectedTeamId: Int? = null,
    val posts: List<CampaignPostCardUiModel> = emptyList(),
    val expandedPostId: Int? = null,
    val editor: CampaignPostEditorUiModel? = null,
    val deleteTarget: CampaignPostDeleteTargetUiModel? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

data class CampaignPostsSummaryUiModel(
    val totalPosts: Int = 0,
    val totalTeams: Int = 0,
    val latestUpdate: String = "--"
)

data class CampaignPostsTeamUiModel(
    val id: Int,
    val label: String,
    val accentColors: List<Long> = emptyList(),
    val postCount: Int = 0
)

data class CampaignPostCardUiModel(
    val id: Int,
    val teamId: Int,
    val teamName: String,
    val title: String,
    val excerpt: String,
    val content: String,
    val authorName: String,
    val publishedAt: String,
    val updatedAt: String,
    val thumbnailUrl: String?,
    val accentColors: List<Long>,
    val attachmentLabels: List<String>
)

data class CampaignPostDeleteTargetUiModel(
    val id: Int,
    val title: String
)

data class CampaignPostEditorUiModel(
    val mode: CampaignPostEditorMode,
    val postId: Int? = null,
    val title: String = "",
    val content: String = "",
    val selectedTeamId: Int? = null,
    val attachmentInput: String = "",
    val attachmentNames: List<String> = emptyList()
)

enum class CampaignPostEditorMode {
    Create,
    Edit
}
