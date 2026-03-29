package com.example.uitvolunteermap.features.home.presentation.guest

data class GuestHomeUiState(
    val isLoading: Boolean = false,
    val campaigns: List<CampaignUiModel> = emptyList(),
    val error: String? = null
)

data class CampaignUiModel(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String
)