package com.example.uitvolunteermap.features.team.presentation.TeamEdit

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TeamEditRoute(
    teamId: Int,
    onBack: () -> Unit,
    viewModel: TeamEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Lắng nghe UiEffect để thực hiện hành động một lần
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is TeamEditUiEffect.NavigateBack -> onBack()
                is TeamEditUiEffect.ShowToast -> { /* Hiển thị thông báo nếu cần */ }
            }
        }
    }

    // Truyền dữ liệu xuống Screen thuần túy
    TeamEditScreen(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) }
    )
}