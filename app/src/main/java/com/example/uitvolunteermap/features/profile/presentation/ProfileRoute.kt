package com.example.uitvolunteermap.features.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileRoute(
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ProfileUiEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    ProfileScreen(
        state = state.value,
        onLogoutClick = viewModel::onLogoutClick,
        onBack = onBack
    )
}
