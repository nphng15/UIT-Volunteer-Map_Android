package com.example.uitvolunteermap.features.campaign.presentation.areamap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamPointSource
import com.example.uitvolunteermap.features.campaign.domain.usecase.AddTeamPointUseCase
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignDetailUseCase
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignTeamPointsUseCase
import com.example.uitvolunteermap.features.campaign.domain.usecase.RemoveTeamPointUseCase
import com.example.uitvolunteermap.features.campaign.domain.usecase.UpdateTeamCheckInLocationUseCase
import com.example.uitvolunteermap.features.checkin.domain.usecase.GetMyCampaignUseCase
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
class CampaignAreaMapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCampaignDetail: GetCampaignDetailUseCase,
    private val getTeamPoints: GetCampaignTeamPointsUseCase,
    private val addTeamPoint: AddTeamPointUseCase,
    private val removeTeamPoint: RemoveTeamPointUseCase,
    private val updateTeamCheckInLocation: UpdateTeamCheckInLocationUseCase,
    private val getMyCampaign: GetMyCampaignUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val campaignId: Int = checkNotNull(
        savedStateHandle[AppDestination.CampaignAreaMap.campaignIdArg]
    )

    private val _uiState = MutableStateFlow(
        CampaignAreaMapUiState(
            canMark = sessionManager.canManagePosts,
            canSelectAnyCheckInTeam = !sessionManager.isLeader,
            isAdmin = sessionManager.userRole.value == UserRole.ADMIN
        )
    )
    val uiState: StateFlow<CampaignAreaMapUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CampaignAreaMapUiEffect>()
    val uiEffect: SharedFlow<CampaignAreaMapUiEffect> = _uiEffect.asSharedFlow()

    init {
        load()
    }

    fun onEvent(event: CampaignAreaMapUiEvent) {
        when (event) {
            CampaignAreaMapUiEvent.RefreshRequested -> load()
            CampaignAreaMapUiEvent.BackClicked -> emitEffect(CampaignAreaMapUiEffect.NavigateBack)
            CampaignAreaMapUiEvent.MarkHereClicked -> {
                if (sessionManager.canManagePosts) {
                    emitEffect(CampaignAreaMapUiEffect.RequestLocation)
                } else {
                    emitEffect(CampaignAreaMapUiEffect.ShowMessage("Chỉ trưởng nhóm mới được chấm điểm cho đội."))
                }
            }
            is CampaignAreaMapUiEvent.LocationReceived -> {
                _uiState.update { it.copy(pendingLocation = LatLng(event.latitude, event.longitude)) }
            }
            is CampaignAreaMapUiEvent.MapTapped -> {
                if (sessionManager.canManagePosts) {
                    _uiState.update { it.copy(pendingLocation = LatLng(event.latitude, event.longitude)) }
                } else {
                    emitEffect(CampaignAreaMapUiEffect.ShowMessage("Chỉ trưởng nhóm mới được chấm điểm cho đội."))
                }
            }
            CampaignAreaMapUiEvent.LocationUnavailable -> {
                emitEffect(CampaignAreaMapUiEffect.ShowMessage("Không lấy được vị trí. Hãy bật GPS và thử lại."))
            }
            CampaignAreaMapUiEvent.DialogDismissed -> {
                _uiState.update { it.copy(pendingLocation = null) }
            }
            is CampaignAreaMapUiEvent.ConfirmLocation -> confirmLocation(event.teamId, event.teamName, event.name)
            is CampaignAreaMapUiEvent.RemovePointClicked -> remove(event.id)
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val detail = getCampaignDetail(campaignId)
            val title = (detail as? AppResult.Success)?.data?.title.orEmpty()
            val teams = (detail as? AppResult.Success)?.data?.teams
                ?.map { TeamOption(id = it.id, name = it.name) }
                .orEmpty()
            val myCampaign = (getMyCampaign() as? AppResult.Success)?.data
            val checkInTeam = if (sessionManager.isLeader) {
                myCampaign
                    ?.takeIf { it.campaignId == campaignId }
                    ?.teamId
                    ?.let { teamId -> teams.firstOrNull { it.id == teamId } }
            } else {
                null
            }

            when (val pointsResult = getTeamPoints(campaignId)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        campaignTitle = title,
                        teams = teams,
                        checkInTeam = checkInTeam,
                        points = pointsResult.data,
                        myCampaign = myCampaign,
                        canMark = sessionManager.canManagePosts,
                        isAdmin = sessionManager.userRole.value == UserRole.ADMIN
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        campaignTitle = title,
                        teams = teams,
                        checkInTeam = checkInTeam,
                        myCampaign = myCampaign,
                        errorMessage = pointsResult.error.userMessage
                    )
                }
            }
        }
    }

    private fun confirmLocation(teamId: Int, teamName: String, name: String) {
        val pending = _uiState.value.pendingLocation ?: return
        if (_uiState.value.isSaving) return
        validateMembershipForSave(teamId)?.let { message ->
            emitEffect(CampaignAreaMapUiEffect.ShowMessage(message))
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            when (val checkInResult = updateTeamCheckInLocation(
                teamId = teamId,
                latitude = pending.latitude,
                longitude = pending.longitude,
                radius = 100.0
            )) {
                is AppResult.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    emitEffect(CampaignAreaMapUiEffect.ShowMessage(checkInResult.error.checkInPermissionMessage()))
                }
                is AppResult.Success -> {
                    when (val pointResult = addTeamPoint(
                        campaignId = campaignId,
                        teamId = teamId,
                        teamName = teamName,
                        name = name.ifBlank { "Điểm check-in" },
                        latitude = pending.latitude,
                        longitude = pending.longitude
                    )) {
                        is AppResult.Success -> {
                            _uiState.update { it.copy(isSaving = false, pendingLocation = null) }
                            emitEffect(CampaignAreaMapUiEffect.ShowMessage("Đã lưu điểm đội và điểm check-in cho $teamName."))
                            load()
                        }
                        is AppResult.Error -> {
                            _uiState.update { it.copy(isSaving = false) }
                            emitEffect(CampaignAreaMapUiEffect.ShowMessage(pointResult.error.userMessage))
                        }
                    }
                }
            }
        }
    }

    private fun validateMembershipForSave(teamId: Int): String? {
        if (sessionManager.userRole.value == UserRole.ADMIN) return null
        val myCampaign = _uiState.value.myCampaign ?: return "Bạn không tham gia chiến dịch này."
        if (myCampaign.campaignId != campaignId) return "Bạn không tham gia chiến dịch này."
        if (myCampaign.teamId != teamId) return "Bạn không tham gia đội hình này."
        return null
    }

    private fun remove(id: String) {
        val point = _uiState.value.points.firstOrNull { it.id == id }
        if (point == null || point.source != TeamPointSource.MANUAL) {
            emitEffect(CampaignAreaMapUiEffect.ShowMessage("Chỉ có thể xoá điểm tự chấm."))
            return
        }
        viewModelScope.launch {
            when (removeTeamPoint(id)) {
                is AppResult.Success -> {
                    emitEffect(CampaignAreaMapUiEffect.ShowMessage("Đã xoá điểm."))
                    load()
                }
                is AppResult.Error -> Unit
            }
        }
    }

    private fun AppError.checkInPermissionMessage(): String = when (this) {
        is AppError.Forbidden,
        is AppError.Unauthorized -> "Tài khoản hiện tại chưa có quyền cập nhật điểm check-in cho đội này. Nếu bạn vừa được cấp quyền trưởng nhóm, hãy đăng xuất và đăng nhập lại."
        else -> userMessage
    }

    private fun emitEffect(effect: CampaignAreaMapUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }
}
