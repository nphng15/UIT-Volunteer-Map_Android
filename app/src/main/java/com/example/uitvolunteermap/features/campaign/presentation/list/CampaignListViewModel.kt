package com.example.uitvolunteermap.features.campaign.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
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
    private val manageCampaignUseCase: ManageCampaignUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CampaignListUiState())
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
        // Mở confirm dialog bằng cách set pendingDeleteId
        _uiState.update { it.copy(pendingDeleteId = campaignId) }
    }

    private fun handleDeleteConfirmed() {
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
                pendingDeleteId = null
            )
        }

        viewModelScope.launch {
            // Real: DELETE /campaigns/:id
            // campaignApiService.deleteCampaign(token = authToken, campaignId = campaignId)
            when (val result = manageCampaignUseCase.delete(campaignId)) {
                is AppResult.Success -> {
                    // Commit: xóa snapshot, thông báo thành công
                    deletedItemSnapshot = null
                    _uiEffect.emit(CampaignListUiEffect.ShowMessage("Xoa chien dich thanh cong."))
                }

                is AppResult.Error -> {
                    // Rollback: khôi phục item về đúng vị trí cũ trong list
                    val snapshot = deletedItemSnapshot
                    if (snapshot != null) {
                        val (restoredIndex, restoredItem) = snapshot
                        _uiState.update {
                            val mutableList = it.campaigns.toMutableList()
                            // coerceAtMost phòng trường hợp list đã thay đổi trong lúc chờ API
                            mutableList.add(restoredIndex.coerceAtMost(mutableList.size), restoredItem)
                            it.copy(campaigns = mutableList)
                        }
                        deletedItemSnapshot = null
                    }
                    _uiEffect.emit(CampaignListUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }
}
