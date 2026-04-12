package com.example.uitvolunteermap.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.features.profile.domain.entity.ProfileInfo
import com.example.uitvolunteermap.features.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ProfileUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadProfile()
    }

    fun onFullNameChanged(newValue: String) {
        val error = validateFullName(newValue)

        _uiState.update { state ->
            val newState = state.copy(
                fullName = newValue,
                fullNameError = error,
                saveSuccess = false
            )

            newState.copy(
                isDirty = computeIsDirty(
                    newState.fullName,
                    newState.email,
                    newState.phoneNumber,
                    newState.originalFullName,
                    newState.originalEmail,
                    newState.originalPhone
                )
            )
        }
    }

    fun onClassNameChanged(newValue: String) {
        _uiState.update { state ->
            val newState = state.copy(
                className = newValue,
                saveSuccess = false
            )

            newState.copy(
                isDirty = computeIsDirty(
                    newState.fullName,
                    newState.email,
                    newState.phoneNumber,
                    newState.originalFullName,
                    newState.originalEmail,
                    newState.originalPhone
                )
            )
        }
    }

    fun onEmailChanged(newValue: String) {
        val error = validateEmail(newValue)

        _uiState.update { state ->
            val newState = state.copy(
                email = newValue,
                emailError = error,
                saveSuccess = false
            )

            newState.copy(
                isDirty = computeIsDirty(
                    newState.fullName,
                    newState.email,
                    newState.phoneNumber,
                    newState.originalFullName,
                    newState.originalEmail,
                    newState.originalPhone
                )
            )
        }
    }

    fun onPhoneNumberChanged(newValue: String) {
        val error = validatePhoneNumber(newValue)
        _uiState.update {
            it.copy(
                phoneNumber = newValue,
                phoneError = error,
                isDirty = computeIsDirty(
                    newValue,
                    it.email,
                    it.phoneNumber,
                    it.originalFullName,
                    it.originalEmail,
                    it.originalPhone,
                ),
                emailConflictError = null,
                saveSuccess = false,
            )
        }
    }

    fun onSaveClicked() {
        if (_uiState.value.isSaving) return

        val state = _uiState.value
        if (!state.isDirty || !state.isSaveEnabled) return
        if (state.fullName.isBlank() || state.email.isBlank() || state.phoneNumber.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin.")
            }
            return
        }

        _uiState.update {
            it.copy(
                showConfirmDialog = true,
                errorMessage = null,
                emailConflictError = null,
            )
        }
    }

    fun onConfirmSave() {
        val state = _uiState.value
        if (state.email.trim().equals("test@gmail.com", ignoreCase = true)) {
            _uiState.update {
                it.copy(
                    showConfirmDialog = false,
                    emailConflictError = "Email đã được sử dụng. Vui lòng chọn email khác.",
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isSaving = false,
                saveSuccess = true,
                showConfirmDialog = false,
                emailConflictError = null,
                errorMessage = null,
                originalFullName = it.fullName,
                originalEmail = it.email,
                originalPhone = it.phoneNumber,
                isDirty = false,
            )
        }

        viewModelScope.launch {
            _uiEvent.emit(ProfileUiEvent.ProfileSaved)
        }
    }

    fun onLogoutClick() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            profileRepository.logout().fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.emit(ProfileUiEvent.NavigateToLogin)
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Không thể đăng xuất.",
                        )
                    }
                    _uiEvent.emit(ProfileUiEvent.ShowError(throwable.message ?: "Không thể đăng xuất."))
                }
            )
        }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            profileRepository.getProfile().fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(
                            userId = profile.userId,
                            fullName = profile.fullName,
                            mssv = profile.mssv,
                            className = profile.className,
                            email = profile.email,
                            phoneNumber = profile.phoneNumber,
                            createdAt = profile.createdAt,
                            role = profile.role,
                            originalFullName = profile.fullName,
                            originalEmail = profile.email,
                            originalPhone = profile.phoneNumber,
                            isDirty = false,
                            showConfirmDialog = false,
                            emailConflictError = null,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Không thể tải thông tin hồ sơ.",
                        )
                    }
                    _uiEvent.emit(ProfileUiEvent.ShowError(throwable.message ?: "Không thể tải thông tin hồ sơ."))
                }
            )
        }
    }

    private fun computeIsDirty(
        fullName: String,
        email: String,
        phoneNumber: String,
        originalFullName: String,
        originalEmail: String,
        originalPhone: String,
    ): Boolean = fullName != originalFullName || email != originalEmail || phoneNumber != originalPhone

    private fun validateFullName(fullName: String): String? = when {
        fullName.isBlank() -> "Họ và tên không được để trống."
        fullName.length < 2 -> "Họ và tên phải có ít nhất 2 ký tự."
        else -> null
    }

    private fun validateEmail(email: String): String? {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return when {
            email.isBlank() -> "Email không được để trống."
            !emailRegex.matches(email.trim()) -> "Email không hợp lệ."
            else -> null
        }
    }

    private fun validatePhoneNumber(phone: String): String? = when {
        phone.isBlank() -> "Số điện thoại không được để trống."
        !phone.all { it.isDigit() } -> "Số điện thoại chỉ được chứa chữ số."
        phone.length != 10 -> "Số điện thoại phải có 10 chữ số."
        else -> null
    }
}
