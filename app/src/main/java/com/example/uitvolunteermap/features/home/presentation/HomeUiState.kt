package com.example.uitvolunteermap.features.home.presentation

data class HomeUiState(
    val title: String = "",
    val subtitle: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
