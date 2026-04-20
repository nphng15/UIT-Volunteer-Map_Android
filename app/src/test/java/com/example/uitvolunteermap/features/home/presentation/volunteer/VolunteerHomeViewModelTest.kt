package com.example.uitvolunteermap.features.home.presentation.volunteer

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.home.domain.usecase.GetVolunteerHomeContentUseCase
import com.example.uitvolunteermap.testing.FakeVolunteerHomeRepository
import com.example.uitvolunteermap.testing.MainDispatcherRule
import com.example.uitvolunteermap.testing.collectFlow
import com.example.uitvolunteermap.testing.defaultVolunteerHomeContent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VolunteerHomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeVolunteerHomeRepository()

    @Test
    fun init_loads_home_content_successfully() = runTest {
        repository.result = AppResult.Success(defaultVolunteerHomeContent())

        val viewModel = VolunteerHomeViewModel(
            getVolunteerHomeContentUseCase = GetVolunteerHomeContentUseCase(repository)
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("UIT Volunteer Map", state.appName)
        assertEquals(1, state.campaigns.size)
        assertNull(state.errorMessage)
    }

    @Test
    fun primary_campaign_click_emits_navigation_effect() = runTest {
        val viewModel = VolunteerHomeViewModel(
            getVolunteerHomeContentUseCase = GetVolunteerHomeContentUseCase(repository)
        )
        val effects = mutableListOf<VolunteerHomeUiEffect>()
        collectFlow(viewModel.uiEffect, effects)
        advanceUntilIdle()

        viewModel.onEvent(VolunteerHomeUiEvent.CampaignPrimaryClicked(42))
        advanceUntilIdle()

        assertEquals(
            listOf(VolunteerHomeUiEffect.NavigateToCampaignDetail(42)),
            effects
        )
    }

    @Test
    fun load_error_updates_error_state() = runTest {
        repository.result = AppResult.Error(AppError.Network("Khong the tai dashboard."))

        val viewModel = VolunteerHomeViewModel(
            getVolunteerHomeContentUseCase = GetVolunteerHomeContentUseCase(repository)
        )
        advanceUntilIdle()

        assertEquals("Khong the tai dashboard.", viewModel.uiState.value.errorMessage)
        assertEquals(emptyList<VolunteerCampaignUiModel>(), viewModel.uiState.value.campaigns)
    }
}
