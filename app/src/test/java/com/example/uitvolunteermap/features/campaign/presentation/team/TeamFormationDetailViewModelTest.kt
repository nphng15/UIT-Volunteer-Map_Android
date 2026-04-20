package com.example.uitvolunteermap.features.campaign.presentation.team

import androidx.lifecycle.SavedStateHandle
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetTeamFormationDetailUseCase
import com.example.uitvolunteermap.testing.FakeTeamFormationDetailRepository
import com.example.uitvolunteermap.testing.MainDispatcherRule
import com.example.uitvolunteermap.testing.collectFlow
import com.example.uitvolunteermap.testing.defaultTeamFormationDetail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamFormationDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeTeamFormationDetailRepository()
    private val sessionManager = SessionManager()

    private fun createViewModel(): TeamFormationDetailViewModel {
        return TeamFormationDetailViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(AppDestination.TeamFormationDetail.teamIdArg to 101)
            ),
            getTeamFormationDetailUseCase = GetTeamFormationDetailUseCase(repository),
            sessionManager = sessionManager,
        )
    }

    @Test
    fun guest_session_keeps_guest_ui_state() = runTest {
        repository.result = AppResult.Success(defaultTeamFormationDetail())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isGuest)
        assertEquals("Doi nau com", viewModel.uiState.value.title)
    }

    @Test
    fun volunteer_session_allows_add_post_navigation() = runTest {
        sessionManager.setRole(UserRole.VOLUNTEER)
        val viewModel = createViewModel()
        val effects = mutableListOf<TeamFormationDetailUiEffect>()
        collectFlow(viewModel.uiEffect, effects)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isGuest)

        viewModel.onEvent(TeamFormationDetailUiEvent.AddActivityClicked)
        advanceUntilIdle()

        assertEquals(
            listOf(TeamFormationDetailUiEffect.NavigateToAddPostPopup(101)),
            effects
        )
    }

    @Test
    fun load_error_updates_error_state() = runTest {
        repository.result = AppResult.Error(
            AppError.NotFound("Khong tim thay doi hinh phu hop.")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(
            "Khong tim thay doi hinh phu hop.",
            viewModel.uiState.value.errorMessage
        )
    }
}
