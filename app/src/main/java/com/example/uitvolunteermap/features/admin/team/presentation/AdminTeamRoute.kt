package com.example.uitvolunteermap.features.admin.team.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uitvolunteermap.core.ui.AdminBottomBarTab

@Composable
fun AdminTeamRoute(
    onTabSelected: (AdminBottomBarTab) -> Unit,
    onLogout: () -> Unit = {},
    viewModel: AdminTeamViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AdminTeamUiEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    AdminTeamScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onTabSelected = onTabSelected,
        onLogout = onLogout
    )
}
