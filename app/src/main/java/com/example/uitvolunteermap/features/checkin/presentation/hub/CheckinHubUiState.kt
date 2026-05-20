package com.example.uitvolunteermap.features.checkin.presentation.hub

data class CheckinHubUiState(
    val campaigns: List<CheckinHubCampaignUiModel> = emptyList(),
    val totalCheckins: Int = 0,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val locationPermissionGranted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Một chiến dịch hiển thị trên hub điểm danh.
 * Toạ độ chỉ có khi chiến dịch đã từng được điểm danh (lấy từ lịch sử),
 * vì backend không trả lat/lng ở danh sách chiến dịch.
 */
data class CheckinHubCampaignUiModel(
    val campaignId: Int,
    val campaignName: String,
    val dateRange: String,
    val isCheckedIn: Boolean,
    val checkedInAt: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val checkInRadius: Double? = null
) {
    val hasLocation: Boolean
        get() = latitude != null && longitude != null
}
