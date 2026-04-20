package com.example.uitvolunteermap.features.home.presentation.volunteer

data class VolunteerHomeUiState(
    val appName: String = "",
    val isGuest: Boolean = true,
    val stats: List<VolunteerStatUiModel> = emptyList(),
    val campaigns: List<VolunteerCampaignUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class VolunteerStatUiModel(
    val value: String,
    val label: String
)

data class VolunteerCampaignUiModel(
    val id: Int,
    val title: String,
    val dateRange: String,
    val description: String,
    val meta: String,
    val primaryActionLabel: String,
    val secondaryActionLabel: String,
    val accentColors: List<Long>,
    val coverImageResId: Int = 0
)
