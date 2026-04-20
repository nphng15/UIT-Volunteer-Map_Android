package com.example.uitvolunteermap.features.campaign.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignDetailUseCase
import com.example.uitvolunteermap.testing.FakeCampaignDetailRepository
import com.example.uitvolunteermap.testing.MainDispatcherRule
import com.example.uitvolunteermap.testing.collectFlow
import com.example.uitvolunteermap.testing.defaultCampaignDetail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeCampaignDetailRepository()

    private fun createViewModel(): CampaignDetailViewModel {
        return CampaignDetailViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(AppDestination.CampaignDetail.campaignIdArg to 1)
            ),
            getCampaignDetailUseCase = GetCampaignDetailUseCase(repository),
        )
    }

    @Test
    fun refresh_populates_campaign_detail_state() = runTest {
        repository.result = AppResult.Success(defaultCampaignDetail())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals("Mua He Xanh 2026", viewModel.uiState.value.title)
        assertEquals(1, viewModel.uiState.value.teams.size)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun navigation_events_emit_expected_effects() = runTest {
        val viewModel = createViewModel()
        val effects = mutableListOf<CampaignDetailUiEffect>()
        collectFlow(viewModel.uiEffect, effects)
        advanceUntilIdle()

        viewModel.onEvent(CampaignDetailUiEvent.ViewAllPostsClicked)
        viewModel.onEvent(CampaignDetailUiEvent.TeamClicked(101))
        viewModel.onEvent(CampaignDetailUiEvent.BackClicked)
        advanceUntilIdle()

        assertEquals(
            listOf(
                CampaignDetailUiEffect.NavigateToCampaignPosts(1),
                CampaignDetailUiEffect.NavigateToTeamDetail(101),
                CampaignDetailUiEffect.NavigateBack,
            ),
            effects
        )
    }

    @Test
    fun load_error_updates_screen_error_state() = runTest {
        repository.result = AppResult.Error(
            AppError.NotFound("Khong tim thay chien dich phu hop.")
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(
            "Khong tim thay chien dich phu hop.",
            viewModel.uiState.value.errorMessage
        )
    }
}
