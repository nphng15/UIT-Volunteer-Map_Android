package com.example.uitvolunteermap.features.home.presentation.volunteer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.home.domain.usecase.GetVolunteerHomeContentUseCase
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
class VolunteerHomeViewModel @Inject constructor(
    private val getVolunteerHomeContentUseCase: GetVolunteerHomeContentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VolunteerHomeUiState())
    val uiState: StateFlow<VolunteerHomeUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<VolunteerHomeUiEffect>()
    val uiEffect: SharedFlow<VolunteerHomeUiEffect> = _uiEffect.asSharedFlow()

    init {
        onEvent(VolunteerHomeUiEvent.RefreshRequested)
    }

    fun onEvent(event: VolunteerHomeUiEvent) {
        when (event) {
            VolunteerHomeUiEvent.RefreshRequested -> loadVolunteerHomeContent()
            is VolunteerHomeUiEvent.CampaignPrimaryClicked -> navigateToCampaignDetail(
                campaignId = event.campaignId
            )
            is VolunteerHomeUiEvent.CampaignSecondaryClicked -> showActionMessage(
                campaignId = event.campaignId,
                destination = "ban do"
            )
        }
    }

    private fun loadVolunteerHomeContent() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isLoading = true, errorMessage = null)
            }

            when (val result = getVolunteerHomeContentUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        VolunteerHomeUiState(
                            appName = result.data.appName,
                            stats = result.data.stats.map { stat ->
                                VolunteerStatUiModel(
                                    value = stat.value,
                                    label = stat.label
                                )
                            },
                            campaigns = result.data.campaigns.map { campaign ->
                                VolunteerCampaignUiModel(
                                    id = campaign.id,
                                    title = campaign.title,
                                    dateRange = campaign.dateRange,
                                    description = campaign.description,
                                    meta = campaign.meta,
                                    primaryActionLabel = campaign.primaryActionLabel,
                                    secondaryActionLabel = campaign.secondaryActionLabel,
                                    accentColors = campaign.accentColors
                                )
                            },
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    private fun navigateToCampaignDetail(campaignId: Int) {
        viewModelScope.launch {
            _uiEffect.emit(
                VolunteerHomeUiEffect.NavigateToCampaignDetail(campaignId = campaignId)
            )
        }
    }

    private fun showActionMessage(campaignId: Int, destination: String) {
        val campaignTitle = _uiState.value.campaigns
            .firstOrNull { it.id == campaignId }
            ?.title
            ?: "chien dich"

        viewModelScope.launch {
            _uiEffect.emit(
                VolunteerHomeUiEffect.ShowMessage(
                    message = "Da chon $destination cho $campaignTitle. API dieu huong se duoc noi sau."
                )
            )
        }
    }
}
