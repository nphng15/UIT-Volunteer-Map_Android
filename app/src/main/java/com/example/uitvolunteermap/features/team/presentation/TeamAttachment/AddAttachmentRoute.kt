package com.example.uitvolunteermap.features.team.presentation.addattachment

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddAttachmentRoute(
    teamId: Int,
    onBack: () -> Unit,
    viewModel: AddAttachmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Xử lý các hiệu ứng một lần (Side Effects)
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AddAttachmentUiEffect.NavigateBack -> onBack()
                is AddAttachmentUiEffect.ShowError -> {
                    // Ở đây Hiền có thể gọi Snackbar hoặc Toast để hiện effect.message
                }
                // Thêm else để hết lỗi exhaustive nếu IDE chưa nhận diện đủ sealed class
                else -> {}
            }
        }
    }

    // Gọi Screen (Stateless UI)
    AddAttachmentScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
        }
    )
}

