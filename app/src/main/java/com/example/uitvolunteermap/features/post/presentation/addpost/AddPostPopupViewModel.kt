package com.example.uitvolunteermap.features.post.presentation.addpost

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.ai.captioning.GenerateCaptionUseCase
import com.example.uitvolunteermap.core.ai.captioning.OnDeviceLlmEngine
import com.example.uitvolunteermap.core.ai.captioning.model.PickedImage
import com.example.uitvolunteermap.core.ai.captioning.model.UitContext
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetTeamFormationDetailUseCase
import com.example.uitvolunteermap.features.post.domain.entity.AddPostDraft
import com.example.uitvolunteermap.features.post.domain.usecase.CreateAddPostUseCase
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
    @ApplicationContext private val context: Context? = null,
    savedStateHandle: SavedStateHandle,
    private val createAddPostUseCase: CreateAddPostUseCase,
    private val sessionManager: SessionManager,
    private val generateCaptionUseCase: GenerateCaptionUseCase? = null,
    private val onDeviceLlmEngine: OnDeviceLlmEngine? = null,
    private val getTeamFormationDetailUseCase: GetTeamFormationDetailUseCase
) : ViewModel() {

    private val teamId: Int = checkNotNull(savedStateHandle[AppDestination.AddPostPopup.teamIdArg])
    private val canManagePosts: Boolean
        get() = sessionManager.canManagePosts
    private val authorId: Int
        get() = sessionManager.currentUserId

    // Real team name + description, loaded once so captions reference real context.
    private var teamName: String? = null
    private var teamDescription: String? = null

    private val _uiState = MutableStateFlow(
        AddPostPopupUiState(
            canManagePosts = canManagePosts,
            gemmaModelAvailable = onDeviceLlmEngine?.isAvailable() == true
        )
    )
    val uiState: StateFlow<AddPostPopupUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AddPostPopupUiEffect>()
    val uiEffect: SharedFlow<AddPostPopupUiEffect> = _uiEffect.asSharedFlow()

    private var captionJob: Job? = null

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
        viewModelScope.launch {
            when (val result = getTeamFormationDetailUseCase(teamId)) {
                is AppResult.Success -> {
                    teamName = result.data.title.takeIf { it.isNotBlank() }
                    teamDescription = result.data.description.takeIf { it.isNotBlank() }
                }
                is AppResult.Error -> Unit
            }
        }
    }

    fun onEvent(event: AddPostPopupUiEvent) {
        when (event) {
            AddPostPopupUiEvent.CloseClicked -> emitEffect(AddPostPopupUiEffect.NavigateBack)
            AddPostPopupUiEvent.PublishClicked -> {
                if (requirePostWritePermission()) publishPost()
            }
            AddPostPopupUiEvent.UploadClicked -> {
                requirePostWritePermission()
            }
            is AddPostPopupUiEvent.ImagesPicked -> {
                if (requirePostWritePermission()) handleImagesPicked(event.uris)
            }
            is AddPostPopupUiEvent.RemoveAttachmentClicked -> removePickedImage(event.index)
            AddPostPopupUiEvent.RegenerateCaptionClicked -> {
                if (requirePostWritePermission()) {
                    _uiState.update { it.copy(regenerateNonce = it.regenerateNonce + 1) }
                    regenerateCaption()
                }
            }
            AddPostPopupUiEvent.AcceptSuggestionClicked -> applySuggestion()
            is AddPostPopupUiEvent.CampaignNameChanged -> {
                _uiState.update { it.copy(campaignNameInput = event.value) }
            }
            is AddPostPopupUiEvent.CaptionModeChanged -> {
                val effective = if (event.mode == com.example.uitvolunteermap.core.ai.captioning.model.CaptionMode.VL_GEMMA &&
                    onDeviceLlmEngine?.isAvailable() != true
                ) {
                    emitEffect(AddPostPopupUiEffect.ShowMessage(
                        "Chưa thấy model AI (qwen.task) — đặt vào /sdcard/Android/data/.../files/llm/. Vẫn dùng được chế độ Nhanh."
                    ))
                    com.example.uitvolunteermap.core.ai.captioning.model.CaptionMode.TEMPLATE_FAST
                } else event.mode
                _uiState.update { it.copy(captionMode = effective) }
                if (_uiState.value.pickedImages.isNotEmpty()) regenerateCaption()
            }
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
            emitEffect(AddPostPopupUiEffect.ShowMessage("Bài viết chỉ hỗ trợ tối đa $MAX_IMAGES ảnh."))
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
            val ctx = UitContext(
                campaignName = _uiState.value.campaignNameInput.takeIf { it.isNotBlank() },
                teamName = teamName ?: "Đội hình #$teamId",
                teamDescription = teamDescription
            )
            val result = generateCaptionUseCase?.invoke(
                uris = images.map { it.uri },
                ctx = ctx,
                nonce = _uiState.value.regenerateNonce,
                mode = _uiState.value.captionMode
            ) ?: AppResult.Error(AppError.Unknown("Chưa cấu hình bộ gợi ý nội dung."))
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
        if (!canManagePosts) return
        if (_uiState.value.isSubmitting) return
        // Flip the guard synchronously on the calling thread so consecutive taps
        // landing in the same frame are dropped before reaching the repository.
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val state = _uiState.value
            val draft = AddPostDraft(
                teamId = teamId,
                authorId = authorId,
                title = state.title,
                content = state.content,
                attachmentNames = state.attachmentDisplayNames,
                photoCaptions = state.captionSuggestion?.perPhotoCaptions.orEmpty(),
                localImageUris = state.pickedImages.map { it.uri.toString() }
            )

            when (val result = createAddPostUseCase(draft)) {
                is AppResult.Success -> {
                    _uiState.value = AddPostPopupUiState(
                        canManagePosts = canManagePosts,
                        gemmaModelAvailable = onDeviceLlmEngine?.isAvailable() == true
                    )
                    emitEffect(
                        AddPostPopupUiEffect.PostPublished("Bai viet da duoc tao thanh cong.")
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
            context?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
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

    private fun requirePostWritePermission(): Boolean {
        if (canManagePosts) return true
        emitEffect(AddPostPopupUiEffect.ShowMessage("Chỉ trưởng nhóm mới được tạo bài viết."))
        return false
    }

    companion object {
        private const val MAX_IMAGES = 5
    }
}
