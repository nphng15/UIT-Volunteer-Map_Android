package com.example.uitvolunteermap.features.profile.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.features.profile.domain.entity.ProfileInfo
import com.example.uitvolunteermap.features.profile.domain.usecase.GetProfileUseCase
import com.example.uitvolunteermap.features.profile.domain.usecase.LogoutUseCase
import com.example.uitvolunteermap.features.profile.domain.usecase.UpdateProfileUseCase
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
    private val getProfileUseCase: GetProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ProfileUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadProfile()
    }

    // --- Các hàm xử lý sự kiện UI giữ nguyên logic cũ ---

    fun onFullNameChanged(newValue: String) {
        if (!_uiState.value.canEdit) return
        val error = validateFullName(newValue)
        _uiState.update { state ->
            val newState = state.copy(fullName = newValue, fullNameError = error, saveSuccess = false)
            newState.copy(isDirty = computeIsDirty(newState.fullName, newState.email, newState.phoneNumber, newState.originalFullName, newState.originalEmail, newState.originalPhone))
        }
    }

    fun onClassNameChanged(newValue: String) {
        if (!_uiState.value.canEdit) return
        _uiState.update { state ->
            val newState = state.copy(className = newValue, saveSuccess = false)
            newState.copy(isDirty = computeIsDirty(newState.fullName, newState.email, newState.phoneNumber, newState.originalFullName, newState.originalEmail, newState.originalPhone))
        }
    }

    fun onEmailChanged(newValue: String) {
        if (!_uiState.value.canEdit) return
        val error = validateEmail(newValue)
        _uiState.update { state ->
            val newState = state.copy(email = newValue, emailError = error, saveSuccess = false)
            newState.copy(isDirty = computeIsDirty(newState.fullName, newState.email, newState.phoneNumber, newState.originalFullName, newState.originalEmail, newState.originalPhone))
        }
    }

    fun onPhoneNumberChanged(newValue: String) {
        if (!_uiState.value.canEdit) return
        val error = validatePhoneNumber(newValue)
        _uiState.update { state ->
            val newState = state.copy(phoneNumber = newValue, phoneError = error, saveSuccess = false)
            newState.copy(isDirty = computeIsDirty(newState.fullName, newState.email, newValue, state.originalFullName, state.originalEmail, state.originalPhone))
        }
    }

    fun onSaveClicked() {
        if (_uiState.value.isSaving) return
        val state = _uiState.value
        if (!state.canEdit || !state.isDirty || !state.isSaveEnabled) return
        if (state.fullName.isBlank() || state.email.isBlank() || state.phoneNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin.") }
            return
        }
        _uiState.update { it.copy(showConfirmDialog = true, errorMessage = null) }
    }

    fun onConfirmSave() {
        val state = _uiState.value

        // ViewModel kiểm tra nhanh để không chạy xử lý nếu không có quyền
        if (!state.canEdit) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, showConfirmDialog = false) }

            val profileUpdate = ProfileInfo(
                userId = state.userId,
                fullName = state.fullName,
                mssv = state.mssv,
                className = state.className,
                email = state.email,
                phoneNumber = state.phoneNumber,
                createdAt = state.createdAt,
                role = state.role
            )

            // Gọi UseCase xử lý logic nghiệp vụ
            when (val result = updateProfileUseCase(profileUpdate)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(
                        isSaving = false,
                        isDirty = false,
                        saveSuccess = true,
                        originalFullName = it.fullName,
                        originalEmail = it.email,
                        originalPhone = it.phoneNumber
                    )}
                    _uiEvent.emit(ProfileUiEvent.ProfileSaved)
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(
                        isSaving = false,
                        errorMessage = result.error.userMessage // Dùng extension userMessage
                    )}
                }
            }
        }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    // --- Cập nhật logic gọi UseCase thay cho Repository ---

    fun onLogoutClick() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = logoutUseCase()) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.emit(ProfileUiEvent.NavigateToLogin)
                }
                is AppResult.Error -> {
                    // SỬA LỖI: Dùng result.error.userMessage
                    val msg = result.error.userMessage
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _uiEvent.emit(ProfileUiEvent.ShowError(msg))
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getProfileUseCase()) {
                is AppResult.Success -> {
                    val profile = result.data
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
                            isLoading = false
                        )
                    }
                }
                is AppResult.Error -> {
                    // SỬA LỖI: Dùng result.error.userMessage
                    val msg = result.error.userMessage
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _uiEvent.emit(ProfileUiEvent.ShowError(msg))
                }
            }
        }
    }

    // --- Các hàm hỗ trợ tính toán logic giữ nguyên ---

    private fun computeIsDirty(f: String, e: String, p: String, of: String, oe: String, op: String) =
        f != of || e != oe || p != op

    private fun validateFullName(n: String) = if (n.isBlank()) "Trống" else if (n.length < 2) "Ngắn" else null
    private fun validateEmail(e: String) = if (e.isBlank()) "Trống" else if (!e.contains("@")) "Sai định dạng" else null
    private fun validatePhoneNumber(p: String) = if (p.length != 10) "Phải 10 số" else null
}