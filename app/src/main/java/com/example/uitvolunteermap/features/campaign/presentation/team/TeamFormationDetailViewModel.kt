package com.example.uitvolunteermap.features.campaign.presentation.team

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetTeamFormationDetailUseCase
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
class TeamFormationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTeamFormationDetailUseCase: GetTeamFormationDetailUseCase,
    private val createAddPostUseCase: CreateAddPostUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val teamId: Int = checkNotNull(
        savedStateHandle[AppDestination.TeamFormationDetail.teamIdArg]
    )

    private val _uiState = MutableStateFlow(
        // Đọc role tại thời điểm khởi tạo ViewModel — đủ vì role không đổi trong session
        TeamFormationDetailUiState(isGuest = sessionManager.isGuest)
    )
    val uiState: StateFlow<TeamFormationDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<TeamFormationDetailUiEffect>()
    val uiEffect: SharedFlow<TeamFormationDetailUiEffect> = _uiEffect.asSharedFlow()

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
                _uiState.update { it.copy(addPostSheet = null) }
            }
            is TeamFormationDetailUiEvent.AddPostTitleChanged -> {
                _uiState.updateAddPostSheet { it.copy(title = event.value, errorMessage = null) }
            }
            is TeamFormationDetailUiEvent.AddPostContentChanged -> {
                _uiState.updateAddPostSheet { it.copy(content = event.value, errorMessage = null) }
            }
            TeamFormationDetailUiEvent.AddPostUploadClicked -> appendMockAttachment()
            is TeamFormationDetailUiEvent.AddPostAttachmentRemoved -> {
                _uiState.updateAddPostSheet { sheet ->
                    sheet.copy(
                        attachmentNames = sheet.attachmentNames.removeAtOrKeep(event.index)
                    )
                }
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
                        // Giữ lại isGuest từ state trước — không để replace bởi default constructor
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
                            isGuest = current.isGuest
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
        _uiState.update { it.copy(addPostSheet = TeamAddPostSheetUiState()) }
    }

    private fun appendMockAttachment() {
        if (!sessionManager.canManagePosts) return

        val currentSheet = _uiState.value.addPostSheet ?: return
        if (currentSheet.attachmentNames.size >= 5) {
            showMessage("Biểu mẫu này chỉ hỗ trợ tối đa 5 ảnh đính kèm.")
            return
        }

        val nextIndex = currentSheet.attachmentNames.size + 1
        _uiState.updateAddPostSheet { sheet ->
            sheet.copy(
                attachmentNames = sheet.attachmentNames + "team_activity_$nextIndex.jpg",
                errorMessage = null
            )
        }
    }

    private fun publishAddPost() {
        if (!sessionManager.canManagePosts) {
            showMessage("Chỉ trưởng nhóm mới được tạo bài viết.")
            return
        }
        val currentSheet = _uiState.value.addPostSheet ?: return

        viewModelScope.launch {
            _uiState.updateAddPostSheet { it.copy(isSubmitting = true, errorMessage = null) }

            when (
                val result = createAddPostUseCase(
                    AddPostDraft(
                        teamId = teamId,
                        authorId = sessionManager.currentUserId,
                        title = currentSheet.title,
                        content = currentSheet.content,
                        attachmentNames = currentSheet.attachmentNames
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

    private fun emitEffect(effect: TeamFormationDetailUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}

private fun MutableStateFlow<TeamFormationDetailUiState>.updateAddPostSheet(
    transform: (TeamAddPostSheetUiState) -> TeamAddPostSheetUiState
) {
    update { current ->
        current.copy(addPostSheet = current.addPostSheet?.let(transform))
    }
}

private fun List<String>.removeAtOrKeep(index: Int): List<String> {
    if (index !in indices) return this
    return toMutableList().also { it.removeAt(index) }
}
