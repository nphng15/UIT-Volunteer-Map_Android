package com.example.uitvolunteermap.features.campaign.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignsUseCase
import com.example.uitvolunteermap.features.campaign.domain.usecase.ManageCampaignUseCase
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
class CampaignListViewModel @Inject constructor(
    private val getCampaignsUseCase: GetCampaignsUseCase,
    private val manageCampaignUseCase: ManageCampaignUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        // Đọc role tại thời điểm khởi tạo ViewModel — đủ vì role không đổi trong session
        // Real: sessionManager.userRole sẽ được cập nhật sau khi login/logout
        CampaignListUiState(canManageCampaigns = sessionManager.canManageCampaigns)
    )
    val uiState: StateFlow<CampaignListUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CampaignListUiEffect>()
    val uiEffect: SharedFlow<CampaignListUiEffect> = _uiEffect.asSharedFlow()

    // Snapshot lưu item vừa bị xóa optimistically để rollback nếu API thất bại
    // Pair<index, item> — không cần expose ra UiState vì UI không cần biết
    private var deletedItemSnapshot: Pair<Int, CampaignListItemUiModel>? = null

    init {
        onEvent(CampaignListUiEvent.RefreshRequested)
    }

    fun onEvent(event: CampaignListUiEvent) {
        when (event) {
            CampaignListUiEvent.RefreshRequested -> loadCampaigns(isPullRefresh = false)
            CampaignListUiEvent.PullToRefreshTriggered -> loadCampaigns(isPullRefresh = true)
            is CampaignListUiEvent.CampaignClicked -> navigateToCampaignDetail(event.campaignId)
            is CampaignListUiEvent.DeleteClicked -> handleDeleteClicked(event.campaignId)
            CampaignListUiEvent.DeleteConfirmed -> handleDeleteConfirmed()
            CampaignListUiEvent.DeleteCancelled ->
                _uiState.update { it.copy(pendingDeleteId = null) }
        }
    }

    // ─── Load ────────────────────────────────────────────────────────────────────

    private fun loadCampaigns(isPullRefresh: Boolean) {
        viewModelScope.launch {
            // isPullRefresh = true → giữ list cũ, chỉ hiện PTR indicator
            // isPullRefresh = false → hiện full-screen spinner (load lần đầu / retry)
            if (isPullRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            when (val result = getCampaignsUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            campaigns = result.data.map { campaign ->
                                CampaignListItemUiModel(
                                    campaignId = campaign.campaignId,
                                    campaignName = campaign.campaignName,
                                    description = campaign.description ?: "",
                                    dateRange = "${campaign.startDate} – ${campaign.endDate}"
                                )
                            },
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    // ─── Navigation ──────────────────────────────────────────────────────────────

    private fun navigateToCampaignDetail(campaignId: Int) {
        viewModelScope.launch {
            _uiEffect.emit(CampaignListUiEffect.NavigateToCampaignDetail(campaignId))
        }
    }

    // ─── Delete ───────────────────────────────────────────────────────────────────

    private fun handleDeleteClicked(campaignId: Int) {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(pendingDeleteId = campaignId) }
    }

    private fun handleDeleteConfirmed() {
        if (!sessionManager.canManageCampaigns) return
        if (_uiState.value.isDeleting) return
        val campaignId = _uiState.value.pendingDeleteId ?: return
        val currentList = _uiState.value.campaigns
        val index = currentList.indexOfFirst { it.campaignId == campaignId }

        if (index == -1) {
            // Item không còn trong list (edge case), chỉ đóng dialog
            _uiState.update { it.copy(pendingDeleteId = null) }
            return
        }

        // Lưu snapshot để rollback nếu API thất bại
        deletedItemSnapshot = Pair(index, currentList[index])

        // Optimistic UI: xóa item khỏi list ngay lập tức, đóng dialog
        _uiState.update {
            it.copy(
                campaigns = currentList.toMutableList().also { list -> list.removeAt(index) },
                pendingDeleteId = null,
                isDeleting = true
            )
        }

        viewModelScope.launch {
            when (val result = manageCampaignUseCase.delete(campaignId)) {
                is AppResult.Success -> {
                    deletedItemSnapshot = null
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEffect.emit(CampaignListUiEffect.ShowMessage("Xóa chiến dịch thành công."))
                }

                is AppResult.Error -> {
                    val snapshot = deletedItemSnapshot
                    if (snapshot != null) {
                        val (restoredIndex, restoredItem) = snapshot
                        _uiState.update {
                            val mutableList = it.campaigns.toMutableList()
                            mutableList.add(restoredIndex.coerceAtMost(mutableList.size), restoredItem)
                            it.copy(campaigns = mutableList, isDeleting = false)
                        }
                        deletedItemSnapshot = null
                    } else {
                        _uiState.update { it.copy(isDeleting = false) }
                    }
                    _uiEffect.emit(CampaignListUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }
}
