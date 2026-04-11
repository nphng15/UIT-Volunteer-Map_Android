package com.example.uitvolunteermap.features.campaign.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignsUseCase
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
    private val getCampaignsUseCase: GetCampaignsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CampaignListUiState())
    val uiState: StateFlow<CampaignListUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CampaignListUiEffect>()
    val uiEffect: SharedFlow<CampaignListUiEffect> = _uiEffect.asSharedFlow()

    init {
        onEvent(CampaignListUiEvent.RefreshRequested)
    }

    fun onEvent(event: CampaignListUiEvent) {
        when (event) {
            CampaignListUiEvent.RefreshRequested -> loadCampaigns(isPullRefresh = false)
            CampaignListUiEvent.PullToRefreshTriggered -> loadCampaigns(isPullRefresh = true)
            is CampaignListUiEvent.CampaignClicked -> navigateToCampaignDetail(event.campaignId)
        }
    }

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

    private fun navigateToCampaignDetail(campaignId: Int) {
        viewModelScope.launch {
            _uiEffect.emit(CampaignListUiEffect.NavigateToCampaignDetail(campaignId))
        }
    }
}
