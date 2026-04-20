package com.example.uitvolunteermap.features.campaign.presentation.addpost

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.AddPostDraft
import com.example.uitvolunteermap.features.campaign.domain.usecase.CreateAddPostUseCase
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
    private val createAddPostUseCase: CreateAddPostUseCase
) : ViewModel() {

    private val teamId: Int = checkNotNull(savedStateHandle[AppDestination.AddPostPopup.teamIdArg])

    private val _uiState = MutableStateFlow(AddPostPopupUiState())
    val uiState: StateFlow<AddPostPopupUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddPostPopupUiEffect>()
    val uiEffect: SharedFlow<AddPostPopupUiEffect> = _uiEffect.asSharedFlow()

    fun onEvent(event: AddPostPopupUiEvent) {
        when (event) {
            AddPostPopupUiEvent.CloseClicked -> emitEffect(AddPostPopupUiEffect.NavigateBack)
            AddPostPopupUiEvent.PublishClicked -> publishPost()
            AddPostPopupUiEvent.UploadClicked -> appendMockAttachment()
            is AddPostPopupUiEvent.ContentChanged -> {
                _uiState.update { it.copy(content = event.value, errorMessage = null) }
            }
            is AddPostPopupUiEvent.RemoveAttachmentClicked -> {
                _uiState.update {
                    it.copy(
                        attachmentNames = it.attachmentNames.filterNot { name ->
                            name == event.attachmentName
                        }
                    )
                }
            }
            is AddPostPopupUiEvent.TitleChanged -> {
                _uiState.update { it.copy(title = event.value, errorMessage = null) }
            }
        }
    }

    private fun appendMockAttachment() {
        val currentAttachments = _uiState.value.attachmentNames
        if (currentAttachments.size >= 5) {
            emitEffect(AddPostPopupUiEffect.ShowMessage("Mock upload hien chi cho toi da 5 anh."))
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
                "Da them anh mock $nextIndex. Sau nay se noi voi media picker va API that."
            )
        )
    }

    private fun publishPost() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val draft = AddPostDraft(
                teamId = teamId,
                authorId = 20,
                title = _uiState.value.title,
                content = _uiState.value.content,
                attachmentNames = _uiState.value.attachmentNames
            )

            when (val result = createAddPostUseCase(draft)) {
                is AppResult.Success -> {
                    _uiState.value = AddPostPopupUiState()
                    emitEffect(
                        AddPostPopupUiEffect.PostPublished(
                            "Bai viet da duoc tao bang mock data. API POST /posts se duoc noi sau."
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
}
