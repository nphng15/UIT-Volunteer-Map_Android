package com.example.uitvolunteermap.features.campaign.presentation.team

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.ai.captioning.GenerateCaptionUseCase
import com.example.uitvolunteermap.core.ai.captioning.OnDeviceLlmEngine
import com.example.uitvolunteermap.core.ai.captioning.model.CaptionMode
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
class TeamFormationDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context? = null,
    savedStateHandle: SavedStateHandle,
    private val getTeamFormationDetailUseCase: GetTeamFormationDetailUseCase,
    private val createAddPostUseCase: CreateAddPostUseCase,
    private val sessionManager: SessionManager,
    private val generateCaptionUseCase: GenerateCaptionUseCase? = null,
    private val onDeviceLlmEngine: OnDeviceLlmEngine? = null
) : ViewModel() {

    private val teamId: Int = checkNotNull(
        savedStateHandle[AppDestination.TeamFormationDetail.teamIdArg]
    )

    private val _uiState = MutableStateFlow(
        TeamFormationDetailUiState(
            isGuest = sessionManager.isGuest,
            canManagePosts = sessionManager.canManagePosts,
            canCheckin = sessionManager.canCheckin
        )
    )
    val uiState: StateFlow<TeamFormationDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<TeamFormationDetailUiEffect>()
    val uiEffect: SharedFlow<TeamFormationDetailUiEffect> = _uiEffect.asSharedFlow()

    private var captionJob: Job? = null

    init {
        onEvent(TeamFormationDetailUiEvent.RefreshRequested)
    }

    fun onEvent(event: TeamFormationDetailUiEvent) {
        when (event) {
            TeamFormationDetailUiEvent.RefreshRequested -> loadTeamDetail()
            TeamFormationDetailUiEvent.BackClicked -> emitEffect(TeamFormationDetailUiEffect.NavigateBack)
            TeamFormationDetailUiEvent.HeroEditClicked -> showMessage("Chức năng sửa ảnh sẽ được nối với API sau.")
            TeamFormationDetailUiEvent.AddActivityClicked -> openAddPostSheet()
            TeamFormationDetailUiEvent.AddPostDismissed -> {
                captionJob?.cancel()
                _uiState.update { it.copy(addPostSheet = null) }
            }
            is TeamFormationDetailUiEvent.AddPostTitleChanged -> {
                _uiState.updateAddPostSheet { it.copy(title = event.value, errorMessage = null) }
            }
            is TeamFormationDetailUiEvent.AddPostContentChanged -> {
                _uiState.updateAddPostSheet { it.copy(content = event.value, errorMessage = null) }
            }
            TeamFormationDetailUiEvent.AddPostUploadClicked -> Unit
            is TeamFormationDetailUiEvent.AddPostImagesPicked -> handleImagesPicked(event.uris)
            is TeamFormationDetailUiEvent.AddPostAttachmentRemoved -> removePickedImage(event.index)
            TeamFormationDetailUiEvent.AddPostRegenerateCaptionClicked -> {
                _uiState.updateAddPostSheet {
                    it.copy(regenerateNonce = it.regenerateNonce + 1)
                }
                regenerateCaption()
            }
            TeamFormationDetailUiEvent.AddPostAcceptSuggestionClicked -> applySuggestion()
            is TeamFormationDetailUiEvent.AddPostCampaignNameChanged -> {
                _uiState.updateAddPostSheet { it.copy(campaignNameInput = event.value) }
            }
            is TeamFormationDetailUiEvent.AddPostCaptionModeChanged -> {
                val effective = if (event.mode == CaptionMode.VL_GEMMA && onDeviceLlmEngine?.isAvailable() != true) {
                    showMessage(
                        "Chưa thấy model AI (qwen.task) — đặt vào /sdcard/Android/data/.../files/llm/. Vẫn dùng được chế độ Nhanh."
                    )
                    CaptionMode.TEMPLATE_FAST
                } else event.mode
                _uiState.updateAddPostSheet { it.copy(captionMode = effective) }
                val sheet = _uiState.value.addPostSheet ?: return
                if (sheet.pickedImages.isNotEmpty()) regenerateCaption()
            }
            TeamFormationDetailUiEvent.AddPostPublishClicked -> publishAddPost()
            is TeamFormationDetailUiEvent.LeaderClicked -> showMessage("Thông tin chỉ huy ${event.leaderId} sẽ được bổ sung sau.")
            is TeamFormationDetailUiEvent.ActivityClicked -> showMessage("Chi tiết hoạt động ${event.activityId} sẽ được nối sau.")
        }
    }

    private fun loadTeamDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = getTeamFormationDetailUseCase(teamId)) {
                is AppResult.Success -> {
                    _uiState.update { current ->
                        TeamFormationDetailUiState(
                            appName = result.data.appName,
                            appSubtitle = result.data.appSubtitle,
                            title = result.data.title,
                            description = result.data.description,
                            heroCards = result.data.heroCards.map { card ->
                                TeamHeroCardUiModel(
                                    label = card.label,
                                    isPrimary = card.isPrimary
                                )
                            },
                            leaders = result.data.leaders.map { leader ->
                                TeamLeaderUiModel(
                                    id = leader.id,
                                    initials = leader.initials,
                                    role = leader.role,
                                    name = leader.name
                                )
                            },
                            activities = result.data.activities.map { activity ->
                                TeamActivityUiModel(
                                    id = activity.id,
                                    label = activity.label,
                                    isAddButton = activity.isAddButton
                                )
                            },
                            isLoading = false,
                            errorMessage = null,
                            isGuest = current.isGuest,
                            canManagePosts = current.canManagePosts,
                            canCheckin = current.canCheckin
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    private fun showMessage(message: String) {
        emitEffect(TeamFormationDetailUiEffect.ShowMessage(message))
    }

    private fun openAddPostSheet() {
        if (!sessionManager.canManagePosts) {
            showMessage("Chỉ trưởng nhóm mới được tạo bài viết.")
            return
        }
        _uiState.update {
            it.copy(
                addPostSheet = TeamAddPostSheetUiState(
                    gemmaModelAvailable = onDeviceLlmEngine?.isAvailable() == true
                )
            )
        }
    }

    private fun handleImagesPicked(uris: List<Uri>) {
        if (uris.isEmpty()) return
        if (!sessionManager.canManagePosts) return
        val sheet = _uiState.value.addPostSheet ?: return
        val remainingSlots = (MAX_IMAGES - sheet.pickedImages.size).coerceAtLeast(0)
        if (remainingSlots == 0) {
            showMessage("Mỗi bài viết chỉ hỗ trợ tối đa $MAX_IMAGES ảnh.")
            return
        }
        val accepted = uris.take(remainingSlots).map { uri ->
            PickedImage(uri = uri, fileName = resolveFileName(uri))
        }
        if (accepted.isEmpty()) return
        _uiState.updateAddPostSheet {
            it.copy(pickedImages = it.pickedImages + accepted, errorMessage = null)
        }
        regenerateCaption()
    }

    private fun removePickedImage(index: Int) {
        _uiState.updateAddPostSheet { sheet ->
            val updated = sheet.pickedImages.toMutableList().apply {
                if (index in indices) removeAt(index)
            }
            sheet.copy(pickedImages = updated)
        }
        val sheet = _uiState.value.addPostSheet ?: return
        if (sheet.pickedImages.isEmpty()) {
            _uiState.updateAddPostSheet { it.copy(captionSuggestion = null) }
        } else {
            regenerateCaption()
        }
    }

    private fun regenerateCaption() {
        val sheet = _uiState.value.addPostSheet ?: return
        if (sheet.pickedImages.isEmpty()) return
        captionJob?.cancel()
        captionJob = viewModelScope.launch {
            _uiState.updateAddPostSheet { it.copy(isGeneratingCaption = true) }
            // Real team name + description are already loaded in state.
            val current = _uiState.value
            val ctx = UitContext(
                campaignName = sheet.campaignNameInput.takeIf { it.isNotBlank() },
                teamName = current.title.takeIf { it.isNotBlank() } ?: "Đội hình #$teamId",
                teamDescription = current.description.takeIf { it.isNotBlank() }
            )
            val result = generateCaptionUseCase?.invoke(
                uris = sheet.pickedImages.map { it.uri },
                ctx = ctx,
                nonce = sheet.regenerateNonce,
                mode = sheet.captionMode
            ) ?: AppResult.Error(AppError.Unknown("Chưa cấu hình bộ gợi ý nội dung."))
            when (result) {
                is AppResult.Success -> _uiState.updateAddPostSheet {
                    it.copy(isGeneratingCaption = false, captionSuggestion = result.data)
                }
                is AppResult.Error -> _uiState.updateAddPostSheet {
                    it.copy(
                        isGeneratingCaption = false,
                        errorMessage = result.error.userMessage
                    )
                }
            }
        }
    }

    private fun applySuggestion() {
        val sheet = _uiState.value.addPostSheet ?: return
        val suggestion = sheet.captionSuggestion ?: return
        _uiState.updateAddPostSheet {
            it.copy(
                title = suggestion.title,
                content = suggestion.contentWithHashtags,
                errorMessage = null
            )
        }
        showMessage("Đã áp dụng gợi ý AI vào bài viết.")
    }

    private fun publishAddPost() {
        if (!sessionManager.canManagePosts) {
            showMessage("Chỉ trưởng nhóm mới được tạo bài viết.")
            return
        }
        val currentSheet = _uiState.value.addPostSheet ?: return
        if (currentSheet.isSubmitting) return
        _uiState.updateAddPostSheet { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            when (
                val result = createAddPostUseCase(
                    AddPostDraft(
                        teamId = teamId,
                        authorId = sessionManager.currentUserId,
                        title = currentSheet.title,
                        content = currentSheet.content,
                        attachmentNames = currentSheet.attachmentDisplayNames,
                        photoCaptions = currentSheet.captionSuggestion?.perPhotoCaptions.orEmpty(),
                        localImageUris = currentSheet.pickedImages.map { it.uri.toString() }
                    )
                )
            ) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(addPostSheet = null) }
                    showMessage("Đã tạo bài viết mới cho đội hình.")
                }

                is AppResult.Error -> {
                    _uiState.updateAddPostSheet {
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

    private fun emitEffect(effect: TeamFormationDetailUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    companion object {
        private const val MAX_IMAGES = 5
    }
}

private fun MutableStateFlow<TeamFormationDetailUiState>.updateAddPostSheet(
    transform: (TeamAddPostSheetUiState) -> TeamAddPostSheetUiState
) {
    update { current ->
        current.copy(addPostSheet = current.addPostSheet?.let(transform))
    }
}
