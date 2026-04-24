package com.example.uitvolunteermap.features.team.presentation.TeamList

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun TeamListRoute(
    onTeamClick: (Int) -> Unit,
    viewModel: TeamListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Lắng nghe hiệu ứng chuyển màn hình
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is TeamListUiEffect.NavigateToDetail -> onTeamClick(effect.teamId)
            }
        }
    }

    TeamListScreen(
        uiState = uiState,
        onEvent = { viewModel.onEvent(it) }
    )
}