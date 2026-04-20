package com.example.uitvolunteermap.features.post.presentation.addpost

import androidx.lifecycle.SavedStateHandle
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.core.session.UserRole
import com.example.uitvolunteermap.features.post.domain.usecase.CreateAddPostUseCase
import com.example.uitvolunteermap.testing.FakePostRepository
import com.example.uitvolunteermap.testing.MainDispatcherRule
import com.example.uitvolunteermap.testing.collectFlow
import com.example.uitvolunteermap.testing.defaultPost
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddPostPopupViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val postRepository = FakePostRepository()
    private val sessionManager = SessionManager()

    private fun createViewModel(): AddPostPopupViewModel {
        return AddPostPopupViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(AppDestination.AddPostPopup.teamIdArg to 101)
            ),
            createAddPostUseCase = CreateAddPostUseCase(postRepository),
            sessionManager = sessionManager,
        )
    }

    @Test
    fun publish_success_resets_form_and_emits_success() = runTest {
        sessionManager.setRole(UserRole.VOLUNTEER)
        postRepository.createResult = AppResult.Success(defaultPost())
        val viewModel = createViewModel()
        val effects = mutableListOf<AddPostPopupUiEffect>()
        collectFlow(viewModel.uiEffect, effects)
        advanceUntilIdle()

        viewModel.onEvent(AddPostPopupUiEvent.TitleChanged("Tieu de moi"))
        viewModel.onEvent(AddPostPopupUiEvent.ContentChanged("Noi dung moi"))
        viewModel.onEvent(AddPostPopupUiEvent.PublishClicked)
        advanceUntilIdle()

        assertEquals(1, postRepository.createdDrafts.size)
        assertEquals(listOf(AddPostPopupUiEffect.PostPublished("Bai viet da duoc tao thanh cong.")), effects)
        assertEquals("", viewModel.uiState.value.title)
        assertEquals("", viewModel.uiState.value.content)
    }

    @Test
    fun publish_error_keeps_popup_open_and_surfaces_message() = runTest {
        sessionManager.setRole(UserRole.VOLUNTEER)
        postRepository.createResult = AppResult.Error(
            AppError.Validation("Tieu de bai viet khong duoc de trong.")
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(AddPostPopupUiEvent.PublishClicked)
        advanceUntilIdle()

        assertEquals(
            "Tieu de bai viet khong duoc de trong.",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun guest_user_cannot_publish_post() = runTest {
        val viewModel = createViewModel()
        val effects = mutableListOf<AddPostPopupUiEffect>()
        collectFlow(viewModel.uiEffect, effects)
        advanceUntilIdle()

        viewModel.onEvent(AddPostPopupUiEvent.PublishClicked)
        advanceUntilIdle()

        assertTrue(postRepository.createdDrafts.isEmpty())
        assertEquals(
            listOf(AddPostPopupUiEffect.ShowMessage("Chi leader moi duoc tao bai viet.")),
            effects
        )
    }
}
