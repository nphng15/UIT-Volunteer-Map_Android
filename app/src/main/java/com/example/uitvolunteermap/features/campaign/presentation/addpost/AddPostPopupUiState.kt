package com.example.uitvolunteermap.features.campaign.presentation.addpost

data class AddPostPopupUiState(
    val appName: String = "VolunteerMap",
    val title: String = "",
    val content: String = "",
    val attachmentNames: List<String> = emptyList(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)
