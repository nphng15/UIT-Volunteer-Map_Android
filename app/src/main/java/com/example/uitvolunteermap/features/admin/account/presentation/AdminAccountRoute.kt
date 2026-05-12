package com.example.uitvolunteermap.features.admin.account.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uitvolunteermap.core.ui.AdminBottomBarTab

@Composable
fun AdminAccountRoute(
    onTabSelected: (AdminBottomBarTab) -> Unit,
    onLogout: () -> Unit = {},
    viewModel: AdminAccountViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AdminAccountUiEffect.ShowMessage ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    AdminAccountScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onTabSelected = onTabSelected,
        onLogout = onLogout
    )
}
