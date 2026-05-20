package com.example.uitvolunteermap.features.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab

@Composable
fun ProfileRoute(
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    onTabSelected: (VolunteerBottomBarTab) -> Unit = {},
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
        onBack = onBack,
        onTabSelected = onTabSelected
    )
}
