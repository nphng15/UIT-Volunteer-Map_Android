package com.example.uitvolunteermap.features.checkin.presentation

import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinHistoryItem

data class GpsCheckinUiState(
    val screenState: CheckinScreenState = CheckinScreenState.Idle,
    val campaigns: List<CheckinCampaignUiModel> = emptyList(),
    val selectedCampaign: CheckinCampaignUiModel? = null,
    val history: List<CheckinHistoryItem> = emptyList(),
    val isHistoryVisible: Boolean = false,
    val isHistoryLoading: Boolean = false,
    val checkinDistance: Double? = null,
    val checkinTime: String? = null,
    val errorMessage: String? = null,
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val isLocationLoading: Boolean = false,
    val locationPermissionGranted: Boolean = false
)

enum class CheckinScreenState {
    Idle,
    LoadingLocation,
    LocationReady,
    CheckingIn,
    Success,
    Failed
}

data class CheckinCampaignUiModel(
    val campaignId: Int,
    val campaignName: String,
    val latitude: Double,
    val longitude: Double,
    val checkInRadius: Double
)
