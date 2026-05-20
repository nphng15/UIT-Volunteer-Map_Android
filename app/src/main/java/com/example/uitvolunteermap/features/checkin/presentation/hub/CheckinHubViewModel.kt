package com.example.uitvolunteermap.features.checkin.presentation.hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.Campaign
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignsUseCase
import com.example.uitvolunteermap.features.checkin.domain.entity.CheckinHistoryItem
import com.example.uitvolunteermap.features.checkin.domain.usecase.GetCheckinHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CheckinHubViewModel @Inject constructor(
    private val getCampaignsUseCase: GetCampaignsUseCase,
    private val getCheckinHistoryUseCase: GetCheckinHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckinHubUiState())
    val uiState: StateFlow<CheckinHubUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CheckinHubUiEffect>()
    val uiEffect: SharedFlow<CheckinHubUiEffect> = _uiEffect.asSharedFlow()

    init {
        onEvent(CheckinHubUiEvent.RefreshRequested)
        emitEffect(CheckinHubUiEffect.RequestLocationPermission)
    }

    fun onEvent(event: CheckinHubUiEvent) {
        when (event) {
            CheckinHubUiEvent.RefreshRequested -> loadCampaigns()
            CheckinHubUiEvent.PermissionGranted -> {
                _uiState.update { it.copy(locationPermissionGranted = true) }
                emitEffect(CheckinHubUiEffect.RequestLocation)
            }

            CheckinHubUiEvent.PermissionDenied -> {
                _uiState.update { it.copy(locationPermissionGranted = false) }
            }

            is CheckinHubUiEvent.LocationReceived -> {
                _uiState.update {
                    it.copy(
                        userLatitude = event.latitude,
                        userLongitude = event.longitude
                    )
                }
            }
        }
    }

    private fun loadCampaigns() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val campaignsResult = getCampaignsUseCase()
            if (campaignsResult is AppResult.Error) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = campaignsResult.error.userMessage
                    )
                }
                return@launch
            }

            val campaigns = (campaignsResult as AppResult.Success).data

            // Lịch sử là nguồn DUY NHẤT có toạ độ chiến dịch + cho biết đã điểm danh chưa.
            val history = when (val historyResult = getCheckinHistoryUseCase()) {
                is AppResult.Success -> historyResult.data
                is AppResult.Error -> emptyList()
            }
            val historyByCampaign = history.associateBy { it.campaignId }

            _uiState.update {
                it.copy(
                    campaigns = campaigns.map { campaign ->
                        campaign.toHubModel(historyByCampaign[campaign.campaignId])
                    },
                    totalCheckins = history.size,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    private fun emitEffect(effect: CheckinHubUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }
}

private fun Campaign.toHubModel(history: CheckinHistoryItem?): CheckinHubCampaignUiModel =
    CheckinHubCampaignUiModel(
        campaignId = campaignId,
        campaignName = campaignName,
        dateRange = "$startDate - $endDate",
        isCheckedIn = history != null,
        checkedInAt = history?.checkedInAt,
        latitude = history?.campaignLatitude,
        longitude = history?.campaignLongitude,
        checkInRadius = history?.checkInRadius
    )
