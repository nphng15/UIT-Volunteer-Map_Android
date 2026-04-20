package com.example.uitvolunteermap.features.campaign.presentation.form

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CampaignFormRoute(
    onSaved: (String) -> Unit,  // truyền success message về màn trước (CampaignList)
    onBack: () -> Unit,
    viewModel: CampaignFormViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is CampaignFormUiEffect.FormSaved -> onSaved(effect.message)
                CampaignFormUiEffect.NavigateBack -> onBack()
                is CampaignFormUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    CampaignFormScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}
