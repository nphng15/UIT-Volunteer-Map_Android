package com.example.uitvolunteermap.features.campaign.presentation.team

data class TeamFormationDetailUiState(
    val appName: String = "",
    val appSubtitle: String? = null,
    val title: String = "",
    val description: String = "",
    val heroCards: List<TeamHeroCardUiModel> = emptyList(),
    val leaders: List<TeamLeaderUiModel> = emptyList(),
    val activities: List<TeamActivityUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Phân quyền: true → ẩn nút "E" (edit ảnh) và nút "+" (add activity)
    val isGuest: Boolean = true
)

data class TeamHeroCardUiModel(
    val label: String,
    val isPrimary: Boolean
)

data class TeamLeaderUiModel(
    val id: Int,
    val initials: String,
    val role: String,
    val name: String
)

data class TeamActivityUiModel(
    val id: Int,
    val label: String,
    val isAddButton: Boolean
)
