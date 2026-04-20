package com.example.uitvolunteermap.features.post.presentation.addpost

data class AddPostPopupUiState(
    val appName: String = "VolunteerMap",
    val canManagePosts: Boolean = false,
    val title: String = "",
    val content: String = "",
    val attachmentNames: List<String> = emptyList(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)
