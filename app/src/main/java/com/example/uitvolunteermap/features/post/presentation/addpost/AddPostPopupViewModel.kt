package com.example.uitvolunteermap.features.post.presentation.addpost

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.post.domain.entity.AddPostDraft
import com.example.uitvolunteermap.features.post.domain.usecase.CreateAddPostUseCase
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
class AddPostPopupViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createAddPostUseCase: CreateAddPostUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val teamId: Int = checkNotNull(savedStateHandle[AppDestination.AddPostPopup.teamIdArg])
    private val canManagePosts: Boolean
        get() = sessionManager.canManagePosts
    private val authorId: Int
        get() = sessionManager.currentUserId

    private val _uiState = MutableStateFlow(
        AddPostPopupUiState(canManagePosts = canManagePosts)
    )
    val uiState: StateFlow<AddPostPopupUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddPostPopupUiEffect>()
    val uiEffect: SharedFlow<AddPostPopupUiEffect> = _uiEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            sessionManager.userRole.collect {
                _uiState.update { current ->
                    current.copy(
                        canManagePosts = canManagePosts,
                        isSubmitting = current.isSubmitting && canManagePosts,
                        errorMessage = if (canManagePosts) current.errorMessage else null
                    )
                }
            }
        }
    }

    fun onEvent(event: AddPostPopupUiEvent) {
        when (event) {
            AddPostPopupUiEvent.CloseClicked -> emitEffect(AddPostPopupUiEffect.NavigateBack)
            AddPostPopupUiEvent.PublishClicked -> {
                if (requirePostWritePermission()) {
                    publishPost()
                }
            }
            AddPostPopupUiEvent.UploadClicked -> {
                if (requirePostWritePermission()) {
                    appendMockAttachment()
                }
            }
            is AddPostPopupUiEvent.ContentChanged -> {
                _uiState.update { it.copy(content = event.value, errorMessage = null) }
            }
            is AddPostPopupUiEvent.RemoveAttachmentClicked -> {
                _uiState.update {
                    it.copy(
                        attachmentNames = it.attachmentNames.removeAtOrKeep(event.index)
                    )
                }
            }
            is AddPostPopupUiEvent.TitleChanged -> {
                _uiState.update { it.copy(title = event.value, errorMessage = null) }
            }
        }
    }

    private fun appendMockAttachment() {
        if (!canManagePosts) {
            return
        }
        val currentAttachments = _uiState.value.attachmentNames
        if (currentAttachments.size >= 5) {
            emitEffect(AddPostPopupUiEffect.ShowMessage("Bản mock hiện chỉ hỗ trợ tối đa 5 ảnh."))
            return
        }

        val nextIndex = currentAttachments.size + 1
        _uiState.update {
            it.copy(
                attachmentNames = currentAttachments + "activity_mock_$nextIndex.jpg",
                errorMessage = null
            )
        }
        emitEffect(
            AddPostPopupUiEffect.ShowMessage(
                "Đã thêm ảnh mock $nextIndex. Sau này sẽ nối với media picker và API thật."
            )
        )
    }

    private fun publishPost() {
        if (!canManagePosts) {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val draft = AddPostDraft(
                teamId = teamId,
                authorId = authorId,
                title = _uiState.value.title,
                content = _uiState.value.content,
                attachmentNames = _uiState.value.attachmentNames
            )

            when (val result = createAddPostUseCase(draft)) {
                is AppResult.Success -> {
                    _uiState.value = AddPostPopupUiState(
                        canManagePosts = canManagePosts
                    )
                    emitEffect(
                        AddPostPopupUiEffect.PostPublished(
                            "Bài viết đã được tạo thành công."
                        )
                    )
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    private fun emitEffect(effect: AddPostPopupUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    private fun requirePostWritePermission(): Boolean {
        if (canManagePosts) {
            return true
        }
        emitEffect(AddPostPopupUiEffect.ShowMessage("Chỉ trưởng nhóm mới được tạo bài viết."))
        return false
    }
}

private fun List<String>.removeAtOrKeep(index: Int): List<String> {
    if (index !in indices) return this
    return toMutableList().also { it.removeAt(index) }
}
