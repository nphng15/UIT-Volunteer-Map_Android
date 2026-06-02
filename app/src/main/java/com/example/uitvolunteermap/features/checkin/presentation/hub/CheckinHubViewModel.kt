package com.example.uitvolunteermap.features.checkin.presentation.hub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.checkin.domain.entity.MyCampaign
import com.example.uitvolunteermap.features.checkin.domain.usecase.AddMomentUseCase
import com.example.uitvolunteermap.features.checkin.domain.usecase.DeleteMomentUseCase
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
    private val addMomentUseCase: AddMomentUseCase,
    private val deleteMomentUseCase: DeleteMomentUseCase,
    sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CheckinHubUiState(currentAccId = sessionManager.currentUserId)
    )
    val uiState: StateFlow<CheckinHubUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CheckinHubUiEffect>()
    val uiEffect: SharedFlow<CheckinHubUiEffect> = _uiEffect.asSharedFlow()

    init {
        loadCampaign()
        // Việc xin quyền được kích hoạt từ UI (LaunchedEffect) để tránh race với
        // SharedFlow replay=0 — effect emit trong init có thể bị mất khi chưa có collector.
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

            is CheckinHubUiEvent.CameraPermissionResult -> {
                _uiState.update { it.copy(cameraPermissionGranted = event.granted) }
            }

            is CheckinHubUiEvent.LocationReceived -> {
                _uiState.update { current ->
                    current.copy(
                        userLatitude = event.latitude,
                        userLongitude = event.longitude,
                        locationError = false,
                        distanceMeters = distanceToCampaign(
                            event.latitude,
                            event.longitude,
                            current.campaign
                        )
                    )
                }
            }

            CheckinHubUiEvent.LocationUnavailable -> {
                _uiState.update { it.copy(locationError = true) }
            }

            CheckinHubUiEvent.RetryLocationRequested -> {
                _uiState.update { it.copy(locationError = false) }
                emitEffect(CheckinHubUiEffect.RequestLocation)
            }

            is CheckinHubUiEvent.PhotoCaptured -> {
                // Vào trạng thái xem trước, KHÔNG gửi server tự động.
                _uiState.update {
                    it.copy(
                        shutterMode = ShutterMode.Reviewing,
                        previewFile = event.file,
                        errorMessage = null
                    )
                }
            }

            CheckinHubUiEvent.PreviewDismissed -> {
                _uiState.value.previewFile?.let { runCatching { it.delete() } }
                _uiState.update {
                    it.copy(shutterMode = ShutterMode.Live, previewFile = null)
                }
            }

            CheckinHubUiEvent.PreviewSendRequested -> sendPreview()

            CheckinHubUiEvent.CameraFlipRequested -> {
                if (_uiState.value.shutterMode == ShutterMode.Live) {
                    _uiState.update {
                        val next = if (it.cameraFacing == CameraFacing.Back) CameraFacing.Front else CameraFacing.Back
                        it.copy(cameraFacing = next)
                    }
                }
            }

            CheckinHubUiEvent.SuccessOverlayDismissed -> {
                _uiState.update { it.copy(showSuccessOverlay = false) }
            }

            is CheckinHubUiEvent.MomentSelected -> {
                _uiState.update { it.copy(viewingMomentId = event.momentId) }
            }

            CheckinHubUiEvent.MomentViewerDismissed -> {
                _uiState.update { it.copy(viewingMomentId = null) }
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
            _uiState.update { it.copy(isLoadingMoments = true, momentsError = false) }
            when (val result = getCampaignMomentsUseCase(campaignId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(moments = result.data, isLoadingMoments = false, momentsError = false)
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoadingMoments = false, momentsError = true) }
                }
            }
        }
    }

    /**
     * Gửi ảnh đang xem trước (Reviewing) lên server.
     * - Chuyển sang Sending; "gửi liền" cho UX bằng cách quay về Live ngay sau khi server trả về,
     *   ảnh mới được optimistic-add vào đầu wall + refresh background.
     * - Lỗi: ở lại Reviewing để user chụp lại / huỷ.
     */
    private fun sendPreview() {
        val state = _uiState.value
        val campaign = state.campaign ?: return
        val file = state.previewFile ?: return
        if (state.shutterMode != ShutterMode.Reviewing) return

        viewModelScope.launch {
            _uiState.update { it.copy(shutterMode = ShutterMode.Sending, errorMessage = null) }

            val uploadResult = uploadImageUseCase(file)
            runCatching { file.delete() }
            if (uploadResult is AppResult.Error) {
                _uiState.update { it.copy(shutterMode = ShutterMode.Reviewing, errorMessage = uploadResult.error.userMessage) }
                emitEffect(CheckinHubUiEffect.ShowMessage(uploadResult.error.userMessage))
                return@launch
            }
            val imageUrl = (uploadResult as AppResult.Success).data

            if (!campaign.hasCheckedIn) {
                val lat = state.userLatitude
                val lng = state.userLongitude
                if (lat == null || lng == null) {
                    _uiState.update { it.copy(shutterMode = ShutterMode.Reviewing) }
                    emitEffect(CheckinHubUiEffect.ShowMessage("Chưa lấy được vị trí, vui lòng thử lại."))
                    return@launch
                }
                when (val res = performCheckinUseCase(campaign.campaignId, lat, lng, imageUrl)) {
                    is AppResult.Success -> {
                        _uiState.update {
                            it.copy(
                                shutterMode = ShutterMode.Live,
                                previewFile = null,
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
                        _uiState.update {
                            it.copy(shutterMode = ShutterMode.Reviewing, errorMessage = res.error.userMessage)
                        }
                        emitEffect(CheckinHubUiEffect.ShowMessage(res.error.userMessage))
                    }
                }
            } else {
                when (val res = addMomentUseCase(campaign.campaignId, imageUrl, null)) {
                    is AppResult.Success -> {
                        // Optimistic: thêm ngay vào đầu wall, sau đó refresh ngầm.
                        _uiState.update {
                            it.copy(
                                shutterMode = ShutterMode.Live,
                                previewFile = null,
                                moments = listOf(res.data) + it.moments
                            )
                        }
                        emitEffect(CheckinHubUiEffect.ShowMessage("Đã chia sẻ khoảnh khắc."))
                        loadMoments(campaign.campaignId)
                    }
                    is AppResult.Error -> {
                        _uiState.update {
                            it.copy(shutterMode = ShutterMode.Reviewing, errorMessage = res.error.userMessage)
                        }
                        emitEffect(CheckinHubUiEffect.ShowMessage(res.error.userMessage))
                    }
                }
            }
        }
    }

    private fun deleteMoment(momentId: Int) {
        val campaign = _uiState.value.campaign ?: return
        // Optimistic: bỏ khỏi wall + đóng viewer ngay; rollback nếu lỗi.
        val previous = _uiState.value.moments
        _uiState.update {
            it.copy(
                viewingMomentId = null,
                moments = it.moments.filterNot { m -> m.id == momentId }
            )
        }
        viewModelScope.launch {
            when (val res = deleteMomentUseCase(campaign.campaignId, momentId)) {
                is AppResult.Success -> {
                    emitEffect(CheckinHubUiEffect.ShowMessage("Đã xoá khoảnh khắc."))
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(moments = previous) }
                    emitEffect(CheckinHubUiEffect.ShowMessage(res.error.userMessage))
                }
            }
        }
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
