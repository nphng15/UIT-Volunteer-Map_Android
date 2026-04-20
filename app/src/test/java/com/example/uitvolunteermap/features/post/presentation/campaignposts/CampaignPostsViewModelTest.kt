package com.example.uitvolunteermap.features.post.presentation.campaignposts

import androidx.lifecycle.SavedStateHandle
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignDetailUseCase
import com.example.uitvolunteermap.features.post.domain.usecase.CreateAddPostUseCase
import com.example.uitvolunteermap.features.post.domain.usecase.DeletePostUseCase
import com.example.uitvolunteermap.features.post.domain.usecase.GetPostsUseCase
import com.example.uitvolunteermap.features.post.domain.usecase.UpdatePostUseCase
import com.example.uitvolunteermap.testing.FakeCampaignDetailRepository
import com.example.uitvolunteermap.testing.FakePostRepository
import com.example.uitvolunteermap.testing.MainDispatcherRule
import com.example.uitvolunteermap.testing.collectFlow
import com.example.uitvolunteermap.testing.defaultCampaignDetail
import com.example.uitvolunteermap.testing.defaultPost
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignPostsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val campaignRepository = FakeCampaignDetailRepository()
    private val postRepository = FakePostRepository()
    private val sessionManager = SessionManager()

    private fun createViewModel(): CampaignPostsViewModel {
        return CampaignPostsViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(AppDestination.CampaignPosts.campaignIdArg to 1)
            ),
            getCampaignDetailUseCase = GetCampaignDetailUseCase(campaignRepository),
            getPostsUseCase = GetPostsUseCase(postRepository),
            createAddPostUseCase = CreateAddPostUseCase(postRepository),
            updatePostUseCase = UpdatePostUseCase(postRepository),
            deletePostUseCase = DeletePostUseCase(postRepository),
            sessionManager = sessionManager,
        )
    }

    @Test
    fun refresh_loads_campaign_posts_for_matching_team_ids() = runTest {
        campaignRepository.result = AppResult.Success(defaultCampaignDetail())
        postRepository.postsResult = AppResult.Success(
            listOf(
                defaultPost(),
                defaultPost().copy(id = 202, teamId = 999, teamName = "Khac"),
            )
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals("Mùa Hè Xanh 2026", viewModel.uiState.value.campaignTitle)
        assertEquals(1, viewModel.uiState.value.posts.size)
        assertEquals(101, viewModel.uiState.value.posts.first().teamId)
    }

    @Test
    fun back_click_emits_navigation_effect() = runTest {
        val viewModel = createViewModel()
        val effects = mutableListOf<CampaignPostsUiEffect>()
        collectFlow(viewModel.uiEffect, effects)
        advanceUntilIdle()

        viewModel.onEvent(CampaignPostsUiEvent.BackClicked)
        advanceUntilIdle()

        assertEquals(listOf(CampaignPostsUiEffect.NavigateBack), effects)
    }

    @Test
    fun guest_cannot_open_post_editor() = runTest {
        val viewModel = createViewModel()
        val effects = mutableListOf<CampaignPostsUiEffect>()
        collectFlow(viewModel.uiEffect, effects)
        advanceUntilIdle()

        viewModel.onEvent(CampaignPostsUiEvent.CreatePostClicked(preselectedTeamId = 101))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.editor == null)
        assertEquals(
            listOf(CampaignPostsUiEffect.ShowMessage("Chỉ trưởng nhóm mới được tạo, sửa hoặc xóa bài viết.")),
            effects
        )
    }

    @Test
    fun refresh_error_keeps_inline_error() = runTest {
        sessionManager.setRole(UserRole.VOLUNTEER)
        campaignRepository.result = AppResult.Success(defaultCampaignDetail())
        postRepository.postsResult = AppResult.Error(AppError.Network("Không thể tải bài viết."))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals("Không thể tải bài viết.", viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.posts.isEmpty())
        assertEquals("Mùa Hè Xanh 2026", viewModel.uiState.value.campaignTitle)
    }
}
