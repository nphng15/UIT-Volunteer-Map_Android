package com.example.uitvolunteermap.features.post.presentation.addpost

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddPostPopupRoute(
    onPostPublished: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: AddPostPopupViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                AddPostPopupUiEffect.NavigateBack -> onBack()
                is AddPostPopupUiEffect.PostPublished -> {
                    onPostPublished(effect.message)
                }
                is AddPostPopupUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    AddPostPopupScreen(
        state = state.value,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent
    )
}
