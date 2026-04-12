package com.example.uitvolunteermap.features.home.presentation.guest

import com.example.uitvolunteermap.features.home.data.repository.CampaignRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuestHomeViewModel @Inject constructor(
    private val repository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuestHomeUiState())
    val uiState: StateFlow<GuestHomeUiState> = _uiState

    init {
        fetchCampaigns()
    }

    private fun fetchCampaigns() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val response = repository.getCampaigns()

                val campaigns = response.data.map {
                    CampaignUiModel(
                        id = it.campaignId,
                        name = it.campaignName,
                        description = it.description,
                        startDate = it.startDate,
                        endDate = it.endDate
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    campaigns = campaigns
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}