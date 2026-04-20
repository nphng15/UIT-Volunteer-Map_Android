package com.example.uitvolunteermap.features.campaign.presentation.team

import androidx.lifecycle.SavedStateHandle
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetTeamFormationDetailUseCase
import com.example.uitvolunteermap.features.post.domain.usecase.CreateAddPostUseCase
import com.example.uitvolunteermap.testing.FakePostRepository
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
    private val postRepository = FakePostRepository()
    private val sessionManager = SessionManager()

    private fun createViewModel(): TeamFormationDetailViewModel {
        return TeamFormationDetailViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(AppDestination.TeamFormationDetail.teamIdArg to 101)
            ),
            getTeamFormationDetailUseCase = GetTeamFormationDetailUseCase(repository),
            createAddPostUseCase = CreateAddPostUseCase(postRepository),
            sessionManager = sessionManager,
        )
    }

    @Test
    fun guest_session_keeps_guest_ui_state() = runTest {
        repository.result = AppResult.Success(defaultTeamFormationDetail())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isGuest)
        assertEquals("Đội nấu cơm", viewModel.uiState.value.title)
    }

    @Test
    fun volunteer_session_opens_add_post_sheet() = runTest {
        sessionManager.setRole(UserRole.VOLUNTEER)
        val viewModel = createViewModel()
        val effects = mutableListOf<TeamFormationDetailUiEffect>()
        collectFlow(viewModel.uiEffect, effects)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isGuest)

        viewModel.onEvent(TeamFormationDetailUiEvent.AddActivityClicked)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.addPostSheet != null)
        assertTrue(effects.isEmpty())
    }

    @Test
    fun load_error_updates_error_state() = runTest {
        repository.result = AppResult.Error(
            AppError.NotFound("Không tìm thấy đội hình phù hợp.")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(
            "Không tìm thấy đội hình phù hợp.",
            viewModel.uiState.value.errorMessage
        )
    }
}
