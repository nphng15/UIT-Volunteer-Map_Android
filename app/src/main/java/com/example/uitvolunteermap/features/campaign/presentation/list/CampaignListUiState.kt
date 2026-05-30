package com.example.uitvolunteermap.features.campaign.presentation.list

data class CampaignListUiState(
    val campaigns: List<CampaignListItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    // ID chiến dịch đang chờ xác nhận xóa — null = không hiện dialog
    val pendingDeleteId: Int? = null,
    // Phân quyền: true → ẩn tất cả nút ghi (delete)
    val isGuest: Boolean = true
)

data class CampaignListItemUiModel(
    val campaignId: Int,
    val campaignName: String,
    val description: String,
    val dateRange: String
)
