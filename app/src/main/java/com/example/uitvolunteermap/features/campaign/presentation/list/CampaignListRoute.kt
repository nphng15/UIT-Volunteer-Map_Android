package com.example.uitvolunteermap.features.campaign.presentation.list

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val CampaignFormResultKey = "campaign_form_result"

@Composable
fun CampaignListRoute(
    onOpenCampaignDetail: (Int) -> Unit,
    onCreateCampaign: () -> Unit,
    onBack: () -> Unit,
    savedStateHandle: SavedStateHandle? = null,
    viewModel: CampaignListViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is CampaignListUiEffect.NavigateToCampaignDetail -> {
                    onOpenCampaignDetail(effect.campaignId)
                }
                is CampaignListUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<String?>(CampaignFormResultKey, null)
            ?.collect { message ->
                if (message != null) {
                    snackbarHostState.showSnackbar(message)
                    savedStateHandle.remove<String>(CampaignFormResultKey)
                    viewModel.onEvent(CampaignListUiEvent.RefreshRequested)
                }
            }
    }

    CampaignListScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        onCreateCampaign = onCreateCampaign
    )
}
