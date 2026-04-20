package com.example.uitvolunteermap.features.auth.presentation

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.auth.domain.entity.AuthUser
import com.example.uitvolunteermap.features.auth.domain.usecase.LoginUseCase
import com.example.uitvolunteermap.testing.FakeAuthRepository
import com.example.uitvolunteermap.testing.MainDispatcherRule
import com.example.uitvolunteermap.testing.collectFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = FakeAuthRepository()
    private val sessionManager = SessionManager()

    @Test
    fun invalid_input_shows_validation_errors_and_does_not_call_repository() = runTest {
        val viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(authRepository),
            sessionManager = sessionManager,
        )

        viewModel.onEmailChanged("invalid-email")
        viewModel.onPasswordChanged("123")
        viewModel.onLoginClick()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Email khong dung dinh dang.", state.emailError)
        assertEquals("Mat khau phai co it nhat 6 ky tu.", state.passwordError)
        assertNull(authRepository.lastEmail)
        assertEquals(UserRole.GUEST, sessionManager.userRole.value)
    }

    @Test
    fun successful_login_sets_volunteer_role_and_emits_navigation() = runTest {
        authRepository.result = AppResult.Success(
            AuthUser(
                id = "volunteer",
                email = "volunteer@uit.edu.vn",
                displayName = "UIT Volunteer",
            )
        )
        val viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(authRepository),
            sessionManager = sessionManager,
        )
        val events = mutableListOf<LoginUiEvent>()
        collectFlow(viewModel.uiEvent, events)

        viewModel.onEmailChanged(" volunteer@uit.edu.vn ")
        viewModel.onPasswordChanged("volunteer123")
        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals("volunteer@uit.edu.vn", authRepository.lastEmail)
        assertEquals("volunteer123", authRepository.lastPassword)
        assertEquals(UserRole.VOLUNTEER, sessionManager.userRole.value)
        assertEquals(listOf(LoginUiEvent.NavigateToHome), events)
        assertEquals("", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.authError)
    }

    @Test
    fun failed_login_keeps_user_on_login_and_surfaces_error() = runTest {
        authRepository.result = AppResult.Error(
            AppError.Unauthorized("Sai thong tin dang nhap.")
        )
        val viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(authRepository),
            sessionManager = sessionManager,
        )
        val events = mutableListOf<LoginUiEvent>()
        collectFlow(viewModel.uiEvent, events)

        viewModel.onEmailChanged("volunteer@uit.edu.vn")
        viewModel.onPasswordChanged("volunteer123")
        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals("Sai thong tin dang nhap.", viewModel.uiState.value.authError)
        assertTrue(events.isEmpty())
        assertEquals(UserRole.GUEST, sessionManager.userRole.value)
    }
}
