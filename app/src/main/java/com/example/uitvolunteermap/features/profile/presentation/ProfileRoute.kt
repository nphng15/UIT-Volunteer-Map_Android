package com.example.uitvolunteermap.features.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileRoute(
    onBackClick: () -> Unit,
    onProfileSaved: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onError: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                ProfileUiEvent.ProfileSaved -> onProfileSaved()
                ProfileUiEvent.NavigateToLogin -> onNavigateToLogin()
                is ProfileUiEvent.ShowError -> onError(event.message)
            }
        }
    }

    ProfileScreen(
        state = state.value,
        onBackClick = onBackClick,
        onFullNameChanged = viewModel::onFullNameChanged,
        onClassNameChanged = viewModel::onClassNameChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPhoneNumberChanged = viewModel::onPhoneNumberChanged,
        onSaveClick = viewModel::onSaveClicked,
        onConfirmSave = viewModel::onConfirmSave,
        onDismissDialog = viewModel::onDismissDialog,
        onLogoutClick = viewModel::onLogoutClick,
    )
}
