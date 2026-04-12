package com.example.uitvolunteermap.features.profile.presentation

import com.example.uitvolunteermap.features.profile.domain.entity.UserRole

data class ProfileUiState(
    val userId: String = "",
    val fullName: String = "",
    val mssv: String = "",
    val className: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val createdAt: String = "",
    val role: UserRole = UserRole.STUDENT,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isDirty: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val emailConflictError: String? = null,
    val originalFullName: String = "",
    val originalEmail: String = "",
    val originalPhone: String = "",
    val errorMessage: String? = null,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
) {
    val isSaveEnabled: Boolean
        get() = fullName.isNotBlank() && 
                email.isNotBlank() && 
                phoneNumber.isNotBlank() &&
                fullNameError == null &&
                emailError == null &&
                phoneError == null &&
                !isSaving && 
                !isLoading
}
