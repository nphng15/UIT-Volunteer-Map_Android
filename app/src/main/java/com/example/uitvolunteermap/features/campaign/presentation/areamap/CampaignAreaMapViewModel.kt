package com.example.uitvolunteermap.features.campaign.presentation.areamap

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamPointSource
import com.example.uitvolunteermap.features.campaign.domain.usecase.AddTeamPointUseCase
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignDetailUseCase
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignTeamPointsUseCase
import com.example.uitvolunteermap.features.campaign.domain.usecase.RemoveTeamPointUseCase
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
    private val sessionManager: SessionManager
) : ViewModel() {

    private val campaignId: Int = checkNotNull(
        savedStateHandle[AppDestination.CampaignAreaMap.campaignIdArg]
    )

    private val _uiState = MutableStateFlow(
        CampaignAreaMapUiState(canMark = sessionManager.canManagePosts)
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
            CampaignAreaMapUiEvent.LocationUnavailable -> {
                emitEffect(CampaignAreaMapUiEffect.ShowMessage("Không lấy được vị trí. Hãy bật GPS và thử lại."))
            }
            CampaignAreaMapUiEvent.DialogDismissed -> {
                _uiState.update { it.copy(pendingLocation = null) }
            }
            is CampaignAreaMapUiEvent.ConfirmPoint -> confirmPoint(event.teamId, event.teamName, event.name)
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

            when (val pointsResult = getTeamPoints(campaignId)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        campaignTitle = title,
                        teams = teams,
                        points = pointsResult.data,
                        canMark = sessionManager.canManagePosts
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        campaignTitle = title,
                        teams = teams,
                        errorMessage = pointsResult.error.userMessage
                    )
                }
            }
        }
    }

    private fun confirmPoint(teamId: Int, teamName: String, name: String) {
        val pending = _uiState.value.pendingLocation ?: return
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val result = addTeamPoint(
                campaignId = campaignId,
                teamId = teamId,
                teamName = teamName,
                name = name,
                latitude = pending.latitude,
                longitude = pending.longitude
            )
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, pendingLocation = null) }
                    emitEffect(CampaignAreaMapUiEffect.ShowMessage("Đã chấm điểm cho $teamName."))
                    load()
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    emitEffect(CampaignAreaMapUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
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

    private fun emitEffect(effect: CampaignAreaMapUiEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }
}
