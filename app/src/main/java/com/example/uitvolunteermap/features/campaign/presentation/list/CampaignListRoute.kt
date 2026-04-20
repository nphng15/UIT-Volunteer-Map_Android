package com.example.uitvolunteermap.features.campaign.presentation.list

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CampaignListRoute(
    onOpenCampaignDetail: (Int) -> Unit,
    onBack: () -> Unit,
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

    CampaignListScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onBack = onBack
    )
}
