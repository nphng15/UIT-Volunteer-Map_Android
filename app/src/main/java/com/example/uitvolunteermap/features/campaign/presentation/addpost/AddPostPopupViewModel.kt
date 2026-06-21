package com.example.uitvolunteermap.features.campaign.presentation.addpost

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.ai.captioning.GenerateCaptionUseCase
import com.example.uitvolunteermap.core.ai.captioning.model.PickedImage
import com.example.uitvolunteermap.core.ai.captioning.model.UitContext
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.campaign.domain.usecase.CreateAddPostUseCase
import com.example.uitvolunteermap.features.post.domain.entity.AddPostDraft
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Job
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
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val createAddPostUseCase: CreateAddPostUseCase,
    private val sessionManager: SessionManager,
    private val generateCaptionUseCase: GenerateCaptionUseCase
) : ViewModel() {

    private val teamId: Int = checkNotNull(savedStateHandle[AppDestination.AddPostPopup.teamIdArg])

    private val _uiState = MutableStateFlow(AddPostPopupUiState())
    val uiState: StateFlow<AddPostPopupUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddPostPopupUiEffect>()
    val uiEffect: SharedFlow<AddPostPopupUiEffect> = _uiEffect.asSharedFlow()

    private var captionJob: Job? = null

    fun onEvent(event: AddPostPopupUiEvent) {
        when (event) {
            AddPostPopupUiEvent.CloseClicked -> emitEffect(AddPostPopupUiEffect.NavigateBack)
            AddPostPopupUiEvent.PublishClicked -> publishPost()
            AddPostPopupUiEvent.UploadClicked -> Unit
            is AddPostPopupUiEvent.ImagesPicked -> handleImagesPicked(event.uris)
            is AddPostPopupUiEvent.RemovePickedImageClicked -> removePickedImage(event.index)
            AddPostPopupUiEvent.RegenerateCaptionClicked -> {
                _uiState.update { it.copy(regenerateNonce = it.regenerateNonce + 1) }
                regenerateCaption()
            }
            AddPostPopupUiEvent.AcceptSuggestionClicked -> applySuggestion()
            is AddPostPopupUiEvent.ContentChanged -> {
                _uiState.update { it.copy(content = event.value, errorMessage = null) }
            }
            is AddPostPopupUiEvent.TitleChanged -> {
                _uiState.update { it.copy(title = event.value, errorMessage = null) }
            }
        }
    }

    private fun handleImagesPicked(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val current = _uiState.value.pickedImages
        val remainingSlots = (MAX_IMAGES - current.size).coerceAtLeast(0)
        if (remainingSlots == 0) {
            emitEffect(AddPostPopupUiEffect.ShowMessage("Mỗi bài chỉ cho phép tối đa $MAX_IMAGES ảnh."))
            return
        }
        val accepted = uris.take(remainingSlots).map { uri ->
            PickedImage(uri = uri, fileName = resolveFileName(uri))
        }
        if (accepted.isEmpty()) return
        _uiState.update {
            it.copy(
                pickedImages = it.pickedImages + accepted,
                errorMessage = null
            )
        }
        regenerateCaption()
    }

    private fun removePickedImage(index: Int) {
        _uiState.update {
            val updated = it.pickedImages.toMutableList().apply {
                if (index in indices) removeAt(index)
            }
            it.copy(pickedImages = updated)
        }
        if (_uiState.value.pickedImages.isEmpty()) {
            _uiState.update { it.copy(captionSuggestion = null) }
        } else {
            regenerateCaption()
        }
    }

    private fun regenerateCaption() {
        val images = _uiState.value.pickedImages
        if (images.isEmpty()) return
        captionJob?.cancel()
        captionJob = viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingCaption = true) }
            val ctx = UitContext(teamName = "Đội hình #$teamId")
            val result = generateCaptionUseCase(
                uris = images.map { it.uri },
                ctx = ctx,
                nonce = _uiState.value.regenerateNonce
            )
            when (result) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isGeneratingCaption = false,
                        captionSuggestion = result.data
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(
                        isGeneratingCaption = false,
                        errorMessage = result.error.userMessage
                    )
                }
            }
        }
    }

    private fun applySuggestion() {
        val suggestion = _uiState.value.captionSuggestion ?: return
        _uiState.update {
            it.copy(
                title = suggestion.title,
                content = suggestion.contentWithHashtags,
                errorMessage = null
            )
        }
        emitEffect(AddPostPopupUiEffect.ShowMessage("Đã áp dụng gợi ý AI vào bài viết."))
    }

    private fun publishPost() {
        if (_uiState.value.isSubmitting) return
        if (!sessionManager.canManagePosts) {
            emitEffect(AddPostPopupUiEffect.ShowMessage("Chỉ trưởng nhóm mới được tạo bài viết."))
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            val state = _uiState.value
            val draft = AddPostDraft(
                teamId = teamId,
                authorId = sessionManager.currentUserId,
                title = state.title,
                content = state.content,
                attachmentNames = state.attachmentDisplayNames,
                photoCaptions = state.captionSuggestion?.perPhotoCaptions.orEmpty()
            )

            when (val result = createAddPostUseCase(draft)) {
                is AppResult.Success -> {
                    _uiState.value = AddPostPopupUiState()
                    emitEffect(
                        AddPostPopupUiEffect.PostPublished("Bài viết đã được tạo thành công.")
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

    private fun resolveFileName(uri: Uri): String {
        runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && cursor.moveToFirst()) {
                    cursor.getString(idx)?.takeIf { it.isNotBlank() }?.let { return it }
                }
            }
        }
        return uri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
            ?: "image_${System.identityHashCode(uri)}.jpg"
    }

    private fun emitEffect(effect: AddPostPopupUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    companion object {
        private const val MAX_IMAGES = 5
    }
}
