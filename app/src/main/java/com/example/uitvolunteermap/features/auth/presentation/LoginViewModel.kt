package com.example.uitvolunteermap.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import com.example.uitvolunteermap.features.auth.domain.usecase.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<LoginUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        if (sessionManager.accessToken.value != null) {
            viewModelScope.launch {
                val valid = runCatching { authRepository.isTokenValid() }.getOrDefault(false)
                if (valid) {
                    _uiEvent.send(LoginUiEvent.NavigateToHome(isAdmin = sessionManager.userRole.value == UserRole.ADMIN))
                } else {
                    sessionManager.clearSession()
                }
            }
        }
    }

    fun onEmailChanged(newValue: String) {
        _uiState.update {
            it.copy(
                email = newValue,
                emailError = null,
                authError = null,
            )
        }
    }

    fun onPasswordChanged(newValue: String) {
        _uiState.update {
            it.copy(
                password = newValue,
                passwordError = null,
                authError = null,
            )
        }
    }

    fun onLoginClick() {
        if (_uiState.value.isLoading) return

        val emailError = validateUsername(_uiState.value.email)
        val passwordError = validatePassword(_uiState.value.password)

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    authError = null,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authError = null) }

            when (val result = loginUseCase(_uiState.value.email, _uiState.value.password)) {
                is AppResult.Success -> {
                    val user = result.data
                    if (user.token == null || user.accountId == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                authError = "Đăng nhập thất bại. Vui lòng thử lại."
                            )
                        }
                        return@launch
                    }
                    sessionManager.setAuthenticatedSession(
                        token = user.token,
                        accountId = user.accountId,
                        username = user.username,
                        role = user.role
                    )
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            password = "",
                            passwordError = null,
                        )
                    }
                    _uiEvent.send(LoginUiEvent.NavigateToHome(isAdmin = user.role == UserRole.ADMIN))
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            authError = result.error.userMessage,
                        )
                    }
                }
            }
        }
    }

    fun onContinueAsGuestClick() {
        if (_uiState.value.isLoading) return

        sessionManager.setRole(UserRole.GUEST)
        viewModelScope.launch {
            _uiEvent.send(LoginUiEvent.NavigateToHome(isAdmin = false))
        }
    }

    private fun validateUsername(username: String): String? = when {
        username.isBlank() -> "Vui lòng nhập tên đăng nhập."
        username.trim().length < MIN_USERNAME_LENGTH -> "Tên đăng nhập phải có ít nhất 3 ký tự."
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Vui lòng nhập mật khẩu."
        password.length < MIN_PASSWORD_LENGTH -> "Mật khẩu phải có ít nhất 6 ký tự."
        else -> null
    }

    private companion object {
        private const val MIN_USERNAME_LENGTH = 3
        private const val MIN_PASSWORD_LENGTH = 6
    }
}
