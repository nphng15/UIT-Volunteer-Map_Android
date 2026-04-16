package com.example.uitvolunteermap.features.profile.presentation

import com.example.uitvolunteermap.core.UserRole

data class ProfileUiState(
    val userId: String = "",
    val fullName: String = "",
    val mssv: String = "",
    val className: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val createdAt: String = "",
    val role: UserRole = UserRole.GUEST,
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

    // SỬA: Theo Permission Matrix, cả ADMIN và LEADER đều có quyền cập nhật profile
    val canEdit: Boolean
        get() = role == UserRole.ADMIN || role == UserRole.LEADER

    // SỬA: isReadOnly nên là nghịch đảo của canEdit
    val isReadOnly: Boolean
        get() = !canEdit

    val isGuest: Boolean
        get() = role == UserRole.GUEST

    // SỬA: Theo tài liệu, logout không yêu cầu auth middleware nhưng thực tế app
    // nên cho phép mọi user đã đăng nhập (Admin/Leader) được logout
    val canLogout: Boolean
        get() = role != UserRole.GUEST
}