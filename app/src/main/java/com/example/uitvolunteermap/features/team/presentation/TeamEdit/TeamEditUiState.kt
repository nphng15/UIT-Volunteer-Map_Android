package com.example.uitvolunteermap.features.team.presentation.TeamEdit

data class TeamEditUiState(
    val isLoading: Boolean = false,
    val teamName: String = "",
    val description: String = "", // Mockup thêm cho UI
    val imageUrl: String? = null,
    val isUpdateSuccess: Boolean = false,
    val errorMessage: String? = null
)