package com.example.uitvolunteermap.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.features.auth.domain.usecase.AuthStatus
import com.example.uitvolunteermap.features.auth.domain.usecase.CheckInitialAuthStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkAuthStatusUseCase: CheckInitialAuthStatusUseCase
) : ViewModel() {

    private val _effect = MutableSharedFlow<SplashUiEffect>(replay = 1)
    val effect = _effect.asSharedFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            when (checkAuthStatusUseCase()) {
                AuthStatus.AUTHENTICATED -> _effect.emit(SplashUiEffect.NavigateToHome)
                AuthStatus.GUEST -> _effect.emit(SplashUiEffect.NavigateToLogin)
                AuthStatus.SERVER_ERROR -> _effect.emit(SplashUiEffect.ShowError("Hệ thống đang bảo trì"))
            }
        }
    }
}