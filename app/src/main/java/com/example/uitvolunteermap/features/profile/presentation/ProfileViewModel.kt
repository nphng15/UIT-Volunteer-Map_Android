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
        _uiState.update {
            it.copy(fullName = newValue, errorMessage = null, saveSuccess = false)
        }
    }

    fun onClassNameChanged(newValue: String) {
        _uiState.update {
            it.copy(className = newValue, errorMessage = null, saveSuccess = false)
        }
    }

    fun onEmailChanged(newValue: String) {
        _uiState.update {
            it.copy(email = newValue, errorMessage = null, saveSuccess = false)
        }
    }

    fun onPhoneNumberChanged(newValue: String) {
        _uiState.update {
            it.copy(phoneNumber = newValue, errorMessage = null, saveSuccess = false)
        }
    }

    fun onSaveClicked() {
        if (_uiState.value.isSaving) return

        val state = _uiState.value
        if (state.fullName.isBlank() || state.email.isBlank() || state.phoneNumber.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val profile = ProfileInfo(
                userId = state.userId,
                fullName = state.fullName,
                mssv = state.mssv,
                className = state.className,
                email = state.email,
                phoneNumber = state.phoneNumber,
                createdAt = state.createdAt,
            )

            profileRepository.updateProfile(profile).fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = null,
                            saveSuccess = true,
                            fullName = updated.fullName,
                            className = updated.className,
                            email = updated.email,
                            phoneNumber = updated.phoneNumber,
                            createdAt = updated.createdAt,
                            userId = updated.userId,
                            mssv = updated.mssv,
                        )
                    }
                    _uiEvent.emit(ProfileUiEvent.ProfileSaved)
                },
                onFailure = { throwable ->
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEvent.emit(ProfileUiEvent.ShowError(throwable.message ?: "Không thể lưu hồ sơ."))
                }
            )
        }
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
}
