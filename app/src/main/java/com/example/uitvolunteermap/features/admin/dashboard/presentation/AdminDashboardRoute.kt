package com.example.uitvolunteermap.features.admin.dashboard.presentation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uitvolunteermap.core.ui.AdminBottomBarTab

@Composable
fun AdminDashboardRoute(
    onTabSelected: (AdminBottomBarTab) -> Unit,
    onLogout: () -> Unit = {},
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    AdminDashboardScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onTabSelected = onTabSelected,
        onLogout = onLogout
    )
}
