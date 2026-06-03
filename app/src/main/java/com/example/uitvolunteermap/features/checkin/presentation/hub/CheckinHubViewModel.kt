package com.example.uitvolunteermap.features.checkin.presentation.hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.checkin.domain.entity.MyCampaign
import com.example.uitvolunteermap.features.checkin.domain.usecase.AddMomentUseCase
import com.example.uitvolunteermap.features.checkin.domain.usecase.GetCampaignMomentsUseCase
import com.example.uitvolunteermap.features.checkin.domain.usecase.GetMyCampaignUseCase
import com.example.uitvolunteermap.features.checkin.domain.usecase.PerformCheckinUseCase
import com.example.uitvolunteermap.features.checkin.domain.usecase.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
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
    private val getMyCampaignUseCase: GetMyCampaignUseCase,
    private val getCampaignMomentsUseCase: GetCampaignMomentsUseCase,
    private val performCheckinUseCase: PerformCheckinUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val addMomentUseCase: AddMomentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckinHubUiState())
    val uiState: StateFlow<CheckinHubUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CheckinHubUiEffect>()
    val uiEffect: SharedFlow<CheckinHubUiEffect> = _uiEffect.asSharedFlow()

    init {
        loadCampaign()
        emitEffect(CheckinHubUiEffect.RequestLocationPermission)
    }

    fun onEvent(event: CheckinHubUiEvent) {
        when (event) {
            CheckinHubUiEvent.RefreshRequested -> loadCampaign()

            CheckinHubUiEvent.PermissionGranted -> {
                _uiState.update { it.copy(locationPermissionGranted = true) }
                emitEffect(CheckinHubUiEffect.RequestLocation)
            }

            CheckinHubUiEvent.PermissionDenied -> {
                _uiState.update { it.copy(locationPermissionGranted = false) }
            }

            is CheckinHubUiEvent.LocationReceived -> {
                _uiState.update { current ->
                    current.copy(
                        userLatitude = event.latitude,
                        userLongitude = event.longitude,
                        distanceMeters = distanceToCampaign(
                            event.latitude,
                            event.longitude,
                            current.campaign
                        )
                    )
                }
            }

            is CheckinHubUiEvent.PhotoCaptured -> handlePhoto(event.file)

            CheckinHubUiEvent.SuccessOverlayDismissed -> {
                _uiState.update { it.copy(showSuccessOverlay = false) }
            }

            is CheckinHubUiEvent.DeleteMomentRequested -> deleteMoment(event.momentId)
        }
    }

    private fun loadCampaign() {
        viewModelScope.launch {
            _uiState.update { it.copy(stage = CheckinHubStage.Loading, errorMessage = null) }

            when (val result = getMyCampaignUseCase()) {
                is AppResult.Success -> {
                    val campaign = result.data
                    if (campaign == null) {
                        _uiState.update { it.copy(stage = CheckinHubStage.NoCampaign) }
                        return@launch
                    }
                    _uiState.update { current ->
                        current.copy(
                            stage = CheckinHubStage.Ready,
                            campaign = campaign,
                            distanceMeters = distanceToCampaign(
                                current.userLatitude,
                                current.userLongitude,
                                campaign
                            )
                        )
                    }
                    loadMoments(campaign.campaignId)
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            stage = CheckinHubStage.Error,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    private fun loadMoments(campaignId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMoments = true) }
            when (val result = getCampaignMomentsUseCase(campaignId)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(moments = result.data, isLoadingMoments = false) }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoadingMoments = false) }
                }
            }
        }
    }

    private fun handlePhoto(file: File) {
        val state = _uiState.value
        val campaign = state.campaign ?: return
        if (state.isCapturing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCapturing = true, errorMessage = null) }

            // 1. Upload ảnh lên Cloudinary -> URL.
            val uploadResult = uploadImageUseCase(file)
            runCatching { file.delete() }
            if (uploadResult is AppResult.Error) {
                _uiState.update { it.copy(isCapturing = false, errorMessage = uploadResult.error.userMessage) }
                emitEffect(CheckinHubUiEffect.ShowMessage(uploadResult.error.userMessage))
                return@launch
            }
            val imageUrl = (uploadResult as AppResult.Success).data

            if (!campaign.hasCheckedIn) {
                // 2a. Lần đầu -> điểm danh chính thức kèm ảnh.
                val lat = state.userLatitude
                val lng = state.userLongitude
                if (lat == null || lng == null) {
                    _uiState.update { it.copy(isCapturing = false) }
                    emitEffect(CheckinHubUiEffect.ShowMessage("Chưa lấy được vị trí, vui lòng thử lại."))
                    return@launch
                }
                when (val res = performCheckinUseCase(campaign.campaignId, lat, lng, imageUrl)) {
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isCapturing = false,
                                showSuccessOverlay = true,
                                campaign = it.campaign?.copy(
                                    hasCheckedIn = true,
                                    checkedInAt = res.data.checkedInAt
                                )
                            )
                        }
                        loadMoments(campaign.campaignId)
                    }
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isCapturing = false, errorMessage = res.error.userMessage) }
                        emitEffect(CheckinHubUiEffect.ShowMessage(res.error.userMessage))
                    }
                }
            } else {
                // 2b. Đã điểm danh -> đăng ảnh khoảnh khắc.
                when (val res = addMomentUseCase(campaign.campaignId, imageUrl, null)) {
                    is AppResult.Success -> {
                        _uiState.update { it.copy(isCapturing = false) }
                        emitEffect(CheckinHubUiEffect.ShowMessage("Đã chia sẻ khoảnh khắc."))
                        loadMoments(campaign.campaignId)
                    }
                    is AppResult.Error -> {
                        _uiState.update { it.copy(isCapturing = false, errorMessage = res.error.userMessage) }
                        emitEffect(CheckinHubUiEffect.ShowMessage(res.error.userMessage))
                    }
                }
            }
        }
    }

    private fun deleteMoment(momentId: Int) {
        // Xử lý xóa ảnh (kiểm duyệt) sẽ nối ở vòng sau khi UI wall có nút xóa.
    }

    private fun distanceToCampaign(
        lat: Double?,
        lng: Double?,
        campaign: MyCampaign?
    ): Double? {
        if (lat == null || lng == null) return null
        val cLat = campaign?.latitude ?: return null
        val cLng = campaign.longitude ?: return null
        return haversine(lat, lng, cLat, cLng)
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    private fun emitEffect(effect: CheckinHubUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }
}
