package com.example.uitvolunteermap.features.checkin.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.checkin.domain.usecase.GetCheckinHistoryUseCase
import com.example.uitvolunteermap.features.checkin.domain.usecase.PerformCheckinUseCase
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
class GpsCheckinViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val performCheckinUseCase: PerformCheckinUseCase,
    private val getCheckinHistoryUseCase: GetCheckinHistoryUseCase
) : ViewModel() {

    private val campaignId: Int = checkNotNull(
        savedStateHandle[AppDestination.GpsCheckin.campaignIdArg]
    )

    private val campaignName: String = savedStateHandle[AppDestination.GpsCheckin.campaignNameArg] ?: ""
    private val campaignLat: Double = (savedStateHandle.get<Float>(AppDestination.GpsCheckin.latArg) ?: 0f).toDouble()
    private val campaignLng: Double = (savedStateHandle.get<Float>(AppDestination.GpsCheckin.lngArg) ?: 0f).toDouble()
    private val campaignRadius: Double = (savedStateHandle.get<Float>(AppDestination.GpsCheckin.radiusArg) ?: 100f).toDouble()

    private val _uiState = MutableStateFlow(GpsCheckinUiState())
    val uiState: StateFlow<GpsCheckinUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<GpsCheckinUiEffect>()
    val uiEffect: SharedFlow<GpsCheckinUiEffect> = _uiEffect.asSharedFlow()

    init {
        val campaign = CheckinCampaignUiModel(
            campaignId = campaignId,
            campaignName = campaignName,
            latitude = campaignLat,
            longitude = campaignLng,
            checkInRadius = campaignRadius
        )
        _uiState.update {
            it.copy(selectedCampaign = campaign)
        }
        emitEffect(GpsCheckinUiEffect.RequestLocationPermission)
    }

    fun onEvent(event: GpsCheckinUiEvent) {
        when (event) {
            GpsCheckinUiEvent.PermissionGranted -> {
                _uiState.update {
                    it.copy(
                        locationPermissionGranted = true,
                        screenState = CheckinScreenState.LoadingLocation
                    )
                }
                emitEffect(GpsCheckinUiEffect.RequestLocation)
            }

            GpsCheckinUiEvent.PermissionDenied -> {
                _uiState.update {
                    it.copy(
                        locationPermissionGranted = false,
                        errorMessage = "Cần cấp quyền vị trí để điểm danh GPS."
                    )
                }
            }

            is GpsCheckinUiEvent.CampaignSelected -> {
                _uiState.update { it.copy(selectedCampaign = event.campaign) }
            }

            GpsCheckinUiEvent.CheckinClicked -> performCheckin()

            GpsCheckinUiEvent.RetryClicked -> {
                _uiState.update {
                    it.copy(
                        screenState = CheckinScreenState.LoadingLocation,
                        errorMessage = null
                    )
                }
                emitEffect(GpsCheckinUiEffect.RequestLocation)
            }

            GpsCheckinUiEvent.ToggleHistory -> {
                val currentlyVisible = _uiState.value.isHistoryVisible
                if (!currentlyVisible && _uiState.value.history.isEmpty()) {
                    loadHistory()
                }
                _uiState.update { it.copy(isHistoryVisible = !currentlyVisible) }
            }

            GpsCheckinUiEvent.BackClicked -> emitEffect(GpsCheckinUiEffect.NavigateBack)

            is GpsCheckinUiEvent.LocationReceived -> {
                _uiState.update {
                    it.copy(
                        userLatitude = event.latitude,
                        userLongitude = event.longitude,
                        screenState = CheckinScreenState.LocationReady,
                        isLocationLoading = false
                    )
                }
            }

            is GpsCheckinUiEvent.LocationFailed -> {
                _uiState.update {
                    it.copy(
                        screenState = CheckinScreenState.Failed,
                        errorMessage = event.message,
                        isLocationLoading = false
                    )
                }
            }
        }
    }

    private fun performCheckin() {
        val state = _uiState.value
        val campaign = state.selectedCampaign ?: return
        val lat = state.userLatitude ?: return
        val lng = state.userLongitude ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(screenState = CheckinScreenState.CheckingIn) }

            when (val result = performCheckinUseCase(campaign.campaignId, lat, lng)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            screenState = CheckinScreenState.Success,
                            checkinDistance = result.data.distance,
                            checkinTime = result.data.checkedInAt,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            screenState = CheckinScreenState.Failed,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isHistoryLoading = true) }

            when (val result = getCheckinHistoryUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(history = result.data, isHistoryLoading = false)
                    }
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isHistoryLoading = false) }
                    emitEffect(GpsCheckinUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }

    private fun emitEffect(effect: GpsCheckinUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }
}
