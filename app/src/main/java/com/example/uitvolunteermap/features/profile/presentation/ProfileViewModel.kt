package com.example.uitvolunteermap.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
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
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            username = sessionManager.currentUsername ?: "",
            role = sessionManager.userRole.value,
            accountId = sessionManager.currentUserId
        )
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>(replay = 0)
    val uiEvent: SharedFlow<ProfileUiEvent> = _uiEvent.asSharedFlow()

    fun onLogoutClick() {
        if (_uiState.value.isLoggingOut) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            authRepository.logout()
            sessionManager.clearSession()
            _uiEvent.emit(ProfileUiEvent.NavigateToLogin)
        }
    }
}
