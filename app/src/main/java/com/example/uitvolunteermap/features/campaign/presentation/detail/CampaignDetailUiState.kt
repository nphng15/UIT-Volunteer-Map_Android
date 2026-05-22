package com.example.uitvolunteermap.features.campaign.presentation.detail

data class CampaignDetailUiState(
    val appName: String = "",
    val title: String = "",
    val schedule: String = "",
    val heroHeadline: String = "",
    val heroSupportingText: String = "",
    val stats: List<CampaignDetailStatUiModel> = emptyList(),
    val description: String = "",
    val teamSectionTitle: String = "",
    val teams: List<CampaignDetailTeamUiModel> = emptyList(),
    val posts: List<CampaignDetailPostUiModel> = emptyList(),
    val mapOverview: CampaignMapOverviewUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class CampaignDetailStatUiModel(
    val value: String,
    val label: String
)

data class CampaignDetailTeamUiModel(
    val id: Int,
    val name: String,
    val shortName: String,
    val accentColors: List<Long>
)

data class CampaignDetailPostUiModel(
    val id: Int,
    val teamName: String,
    val title: String,
    val publishedAt: String,
    val summary: String,
    val accentColors: List<Long>,
    val isLightBadge: Boolean
)

data class CampaignMapOverviewUiModel(
    val selectedArea: String,
    val headerTitle: String,
    val footerTitle: String,
    val footerDescription: String,
    val ctaLabel: String,
    val locations: List<CampaignMapLocationUiModel>
)

data class CampaignMapLocationUiModel(
    val id: Int,
    val label: String,
    val supportingText: String,
    val xFraction: Float,
    val yFraction: Float,
    val isHighlighted: Boolean
)
