package com.example.uitvolunteermap.features.admin.campaign.presentation

/**
 * Trạng thái form dùng chung cho cả tạo mới và chỉnh sửa.
 * Các trường số (lat/lng/radius) giữ dưới dạng String để bind trực tiếp với TextField;
 * chuyển sang Double khi submit. editingId = null nghĩa là đang ở chế độ tạo mới.
 */
data class AdminCampaignFormState(
    val editingId: Int? = null,
    val campaignName: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val checkInRadius: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val isEditing: Boolean get() = editingId != null
}

data class AdminCampaignUiState(
    val campaigns: List<AdminCampaignItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val canManageCampaigns: Boolean = false,
    val pendingDeleteId: Int? = null,
    val isDeleting: Boolean = false,
    val form: AdminCampaignFormState? = null
) {
    /** Danh sách đã lọc theo từ khoá tìm kiếm (không phân biệt hoa thường). */
    val visibleCampaigns: List<AdminCampaignItemUiModel>
        get() = if (searchQuery.isBlank()) {
            campaigns
        } else {
            campaigns.filter { it.campaignName.contains(searchQuery.trim(), ignoreCase = true) }
        }
}

data class AdminCampaignItemUiModel(
    val campaignId: Int,
    val campaignName: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val dateRange: String,
    val latitude: Double?,
    val longitude: Double?,
    val checkInRadius: Double?,
    val hasLocation: Boolean
)
