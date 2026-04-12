package com.example.uitvolunteermap.features.campaign.presentation.team

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TeamFormationDetailRoute(
    onOpenAddPostPopup: (Int) -> Unit,
    resultMessage: String?,
    onResultMessageConsumed: () -> Unit,
    onBack: () -> Unit,
    viewModel: TeamFormationDetailViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(resultMessage) {
        if (resultMessage != null) {
            snackbarHostState.showSnackbar(resultMessage)
            onResultMessageConsumed()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                TeamFormationDetailUiEffect.NavigateBack -> onBack()
                is TeamFormationDetailUiEffect.NavigateToAddPostPopup -> {
                    onOpenAddPostPopup(effect.teamId)
                }
                is TeamFormationDetailUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    TeamFormationDetailScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}
