package com.example.uitvolunteermap.features.auth.presentation

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val authError: String? = null,
    val isLoading: Boolean = false,
) {
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank() && !isLoading
}
