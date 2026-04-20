package com.example.uitvolunteermap.features.home.presentation.guest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
@Composable
fun GuestHomeRoute(
    viewModel: GuestHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GuestHomeScreen(
        uiState = uiState
    )
}