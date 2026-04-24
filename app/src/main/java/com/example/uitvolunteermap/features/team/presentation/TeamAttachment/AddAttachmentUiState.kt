package com.example.uitvolunteermap.features.team.presentation.addattachment

import android.net.Uri

data class LocalAttachment(
    val uri: Uri,
    val remoteUrl: String? = null,
    val isLoading: Boolean = false
)

data class AddAttachmentUiState(
    val isLoading: Boolean = false,
    val temporaryAttachments: List<LocalAttachment> = emptyList(),
    val isUpdateSuccess: Boolean = false,
    val errorMessage: String? = null
)