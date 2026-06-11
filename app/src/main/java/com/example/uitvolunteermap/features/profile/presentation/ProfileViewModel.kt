package com.example.uitvolunteermap.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import com.example.uitvolunteermap.features.checkin.domain.usecase.GetMyCampaignUseCase
import com.example.uitvolunteermap.features.profile.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getMyCampaignUseCase: GetMyCampaignUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            username = sessionManager.currentUsername ?: "",
            role = sessionManager.userRole.value,
            accountId = sessionManager.currentUserId
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<ProfileUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadProfile()
        loadMyCampaign()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProfileLoading = true) }
            // Mọi role đã đăng nhập đều xem được hồ sơ của CHÍNH mình.
            // Nếu lỗi, giữ dữ liệu từ session, không hiển thị lỗi chặn.
            when (val result = getUserProfileUseCase()) {
                is AppResult.Success -> {
                    val profile = result.data
                    _uiState.update {
                        it.copy(
                            isProfileLoading = false,
                            fullName = profile.fullName,
                            mssv = profile.mssv,
                            className = profile.className,
                            email = profile.email,
                            phoneNumber = profile.phoneNumber,
                            createdAt = profile.createdAt
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isProfileLoading = false) }
                }
            }
        }
    }

    private fun loadMyCampaign() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isMyCampaignLoading = true, myCampaignErrorMessage = null)
            }
            when (val result = getMyCampaignUseCase()) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(isMyCampaignLoading = false, myCampaign = result.data)
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isMyCampaignLoading = false,
                            myCampaignErrorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    fun onLogoutClick() {
        if (_uiState.value.isLoggingOut) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            try {
                runCatching { authRepository.logout() }
                sessionManager.clearSession()
                _uiEvent.send(ProfileUiEvent.NavigateToLogin)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoggingOut = false) }
            }
        }
    }
}
