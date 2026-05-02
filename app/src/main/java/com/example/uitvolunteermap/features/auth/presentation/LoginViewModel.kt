package com.example.uitvolunteermap.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.auth.domain.usecase.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>(replay = 0)
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    private val emailRegex = Regex(EMAIL_REGEX_PATTERN)

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

        val emailError = validateEmail(_uiState.value.email)
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
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            password = "",
                            passwordError = null,
                        )
                    }
                    _uiEvent.emit(LoginUiEvent.NavigateToHome)
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

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Email is required."
        !emailRegex.matches(email.trim()) -> "Please enter a valid email address."
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> "Password is required."
        password.length < MIN_PASSWORD_LENGTH -> "Password must be at least 6 characters."
        else -> null
    }

    private companion object {
        private const val MIN_PASSWORD_LENGTH = 6
        private const val EMAIL_REGEX_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    }
}
