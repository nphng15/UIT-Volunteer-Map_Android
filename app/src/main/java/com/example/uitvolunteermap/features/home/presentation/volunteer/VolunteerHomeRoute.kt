package com.example.uitvolunteermap.features.home.presentation.volunteer

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun VolunteerHomeRoute(
    onOpenCampaignDetail: (Int) -> Unit = {},
    viewModel: VolunteerHomeViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is VolunteerHomeUiEffect.NavigateToCampaignDetail -> {
                    onOpenCampaignDetail(effect.campaignId)
                }
                is VolunteerHomeUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    VolunteerHomeScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}
