package com.example.uitvolunteermap.features.profile.presentation

data class ProfileUiState(
    val userId: String = "",
    val fullName: String = "",
    val mssv: String = "",
    val className: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val createdAt: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
) {
    val isSaveEnabled: Boolean
        get() = fullName.isNotBlank() && email.isNotBlank() && phoneNumber.isNotBlank() && !isSaving && !isLoading
}
