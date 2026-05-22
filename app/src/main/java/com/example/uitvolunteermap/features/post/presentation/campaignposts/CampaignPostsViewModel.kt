package com.example.uitvolunteermap.features.post.presentation.campaignposts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.app.navigation.AppDestination
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.campaign.domain.usecase.GetCampaignDetailUseCase
import com.example.uitvolunteermap.features.post.domain.entity.AddPostDraft
import com.example.uitvolunteermap.features.post.domain.entity.PostUiModel
import com.example.uitvolunteermap.features.post.domain.entity.UpdatePostDraft
import com.example.uitvolunteermap.features.post.domain.usecase.CreateAddPostUseCase
import com.example.uitvolunteermap.features.post.domain.usecase.DeletePostUseCase
import com.example.uitvolunteermap.features.post.domain.usecase.GetPostsUseCase
import com.example.uitvolunteermap.features.post.domain.usecase.UpdatePostUseCase
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
class CampaignPostsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCampaignDetailUseCase: GetCampaignDetailUseCase,
    private val getPostsUseCase: GetPostsUseCase,
    private val createAddPostUseCase: CreateAddPostUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val campaignId: Int = checkNotNull(
        savedStateHandle[AppDestination.CampaignPosts.campaignIdArg]
    )

    private val canManagePosts: Boolean
        get() = sessionManager.canManagePosts
    private val authorId: Int
        get() = sessionManager.currentUserId

    private val _uiState = MutableStateFlow(
        CampaignPostsUiState(
            canManagePosts = canManagePosts
        )
    )
    val uiState: StateFlow<CampaignPostsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<CampaignPostsUiEffect>()
    val uiEffect: SharedFlow<CampaignPostsUiEffect> = _uiEffect.asSharedFlow()

    private var campaignContext: CampaignPostsContext? = null
    private var allPosts: List<PostUiModel> = emptyList()

    init {
        viewModelScope.launch {
            sessionManager.userRole.collect {
                _uiState.update { current ->
                    val stillCanManage = canManagePosts
                    current.copy(
                        canManagePosts = stillCanManage,
                        editor = current.editor?.takeIf { stillCanManage },
                        deleteTarget = current.deleteTarget?.takeIf { stillCanManage },
                        isSaving = current.isSaving && stillCanManage
                    )
                }
            }
        }
        onEvent(CampaignPostsUiEvent.RefreshRequested)
    }

    fun onEvent(event: CampaignPostsUiEvent) {
        when (event) {
            CampaignPostsUiEvent.RefreshRequested -> loadCampaignPosts()
            CampaignPostsUiEvent.BackClicked -> emitEffect(CampaignPostsUiEffect.NavigateBack)
            is CampaignPostsUiEvent.TeamFilterSelected -> {
                publishUi(selectedTeamId = event.teamId)
            }

            is CampaignPostsUiEvent.PostCardClicked -> {
                _uiState.update { current ->
                    current.copy(
                        expandedPostId = if (current.expandedPostId == event.postId) null else event.postId
                    )
                }
            }

            is CampaignPostsUiEvent.CreatePostClicked -> {
                if (requirePostWritePermission()) {
                    openCreateEditor(event.preselectedTeamId)
                }
            }
            is CampaignPostsUiEvent.EditPostClicked -> {
                if (requirePostWritePermission()) {
                    openEditEditor(event.postId)
                }
            }
            is CampaignPostsUiEvent.DeletePostClicked -> {
                if (requirePostWritePermission()) {
                    promptDelete(event.postId)
                }
            }
            CampaignPostsUiEvent.DeleteDismissed -> {
                _uiState.update { it.copy(deleteTarget = null) }
            }

            CampaignPostsUiEvent.DeleteConfirmed -> {
                if (requirePostWritePermission()) {
                    deletePost()
                }
            }
            CampaignPostsUiEvent.EditorDismissed -> {
                _uiState.update { it.copy(editor = null, isSaving = false) }
            }

            is CampaignPostsUiEvent.EditorTitleChanged -> {
                _uiState.updateEditor { it.copy(title = event.value) }
            }

            is CampaignPostsUiEvent.EditorContentChanged -> {
                _uiState.updateEditor { it.copy(content = event.value) }
            }

            is CampaignPostsUiEvent.EditorTeamChanged -> {
                _uiState.updateEditor { it.copy(selectedTeamId = event.teamId) }
            }

            is CampaignPostsUiEvent.EditorAttachmentInputChanged -> {
                _uiState.updateEditor { it.copy(attachmentInput = event.value) }
            }

            CampaignPostsUiEvent.AddAttachmentClicked -> {
                if (requirePostWritePermission()) {
                    appendAttachment()
                }
            }
            is CampaignPostsUiEvent.RemoveAttachmentClicked -> {
                if (!canManagePosts) {
                    emitEffect(CampaignPostsUiEffect.ShowMessage("Chi leader moi duoc thao tac bai viet."))
                    return
                }
                _uiState.updateEditor { editor ->
                    editor.copy(
                        attachmentNames = editor.attachmentNames.removeAtOrKeep(event.index)
                    )
                }
            }

            CampaignPostsUiEvent.SaveEditorClicked -> {
                if (requirePostWritePermission()) {
                    saveEditor()
                }
            }
        }
    }

    private fun loadCampaignPosts() {
        viewModelScope.launch {
            val previousState = _uiState.value
            _uiState.update {
                it.copy(
                    isLoading = campaignContext == null,
                    isRefreshing = campaignContext != null,
                    errorMessage = null
                )
            }

            val campaignResult = getCampaignDetailUseCase(campaignId)
            if (campaignResult is AppResult.Error) {
                _uiState.update {
                    previousState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = campaignResult.error.userMessage
                    )
                }
                return@launch
            }

            val campaign = (campaignResult as AppResult.Success).data
            val teams = campaign.teams.map { team ->
                CampaignPostsTeamUiModel(
                    id = team.id,
                    label = team.name,
                    accentColors = team.accentColors
                )
            }

            val postsResult = getPostsUseCase()
            if (postsResult is AppResult.Error) {
                campaignContext = CampaignPostsContext(
                    appName = campaign.appName,
                    campaignTitle = campaign.title,
                    campaignSubtitle = campaign.schedule,
                    teams = teams
                )
                allPosts = emptyList()
                publishUi(
                    selectedTeamId = previousState.selectedTeamId,
                    errorMessage = postsResult.error.userMessage,
                    isLoading = false,
                    isRefreshing = false
                )
                return@launch
            }

            val allowedTeamIds = teams.map { it.id }.toSet()
            campaignContext = CampaignPostsContext(
                appName = campaign.appName,
                campaignTitle = campaign.title,
                campaignSubtitle = campaign.schedule,
                teams = teams
            )
            allPosts = (postsResult as AppResult.Success).data.filter { it.teamId in allowedTeamIds }

            publishUi(
                selectedTeamId = previousState.selectedTeamId,
                errorMessage = null,
                isLoading = false,
                isRefreshing = false
            )
        }
    }

    private fun openCreateEditor(preselectedTeamId: Int?) {
        if (!canManagePosts) {
            return
        }
        val context = campaignContext ?: return
        val selectedTeamId = preselectedTeamId
            ?.takeIf { teamId -> context.teams.any { it.id == teamId } }
            ?: _uiState.value.selectedTeamId
            ?: context.teams.firstOrNull()?.id

        _uiState.update {
            it.copy(
                editor = CampaignPostEditorUiModel(
                    mode = CampaignPostEditorMode.Create,
                    selectedTeamId = selectedTeamId
                )
            )
        }
    }

    private fun openEditEditor(postId: Int) {
        if (!canManagePosts) {
            return
        }
        val context = campaignContext ?: return
        val editablePost = allPosts.firstOrNull { post ->
            post.id == postId && context.teams.any { team -> team.id == post.teamId }
        }
        if (editablePost == null) {
            emitEffect(CampaignPostsUiEffect.ShowMessage("Bai viet nay khong thuoc chien dich hien tai."))
            return
        }

        _uiState.update {
            it.copy(
                editor = CampaignPostEditorUiModel(
                    mode = CampaignPostEditorMode.Edit,
                    postId = editablePost.id,
                    title = editablePost.title,
                    content = editablePost.content,
                    selectedTeamId = editablePost.teamId,
                    attachmentNames = editablePost.attachmentLabels
                )
            )
        }
    }

    private fun promptDelete(postId: Int) {
        if (!canManagePosts) {
            return
        }
        val post = allPosts.firstOrNull { it.id == postId } ?: return
        _uiState.update {
            it.copy(
                deleteTarget = CampaignPostDeleteTargetUiModel(
                    id = post.id,
                    title = post.title
                )
            )
        }
    }

    private fun appendAttachment() {
        if (!canManagePosts) {
            return
        }
        val editor = _uiState.value.editor ?: return
        if (editor.mode == CampaignPostEditorMode.Edit) {
            emitEffect(
                CampaignPostsUiEffect.ShowMessage(
                    "Che do sua hien chi cho phep cap nhat noi dung va doi phu trach."
                )
            )
            return
        }

        val candidate = editor.attachmentInput.trim()
        if (candidate.isBlank()) {
            emitEffect(CampaignPostsUiEffect.ShowMessage("Nhap ten anh mock truoc khi them."))
            return
        }

        if (editor.attachmentNames.size >= 5) {
            emitEffect(CampaignPostsUiEffect.ShowMessage("Chi ho tro toi da 5 anh mock cho moi bai viet."))
            return
        }

        _uiState.updateEditor {
            it.copy(
                attachmentInput = "",
                attachmentNames = it.attachmentNames + candidate
            )
        }
    }

    private fun saveEditor() {
        if (!canManagePosts) {
            return
        }
        val editor = _uiState.value.editor ?: return
        val selectedTeamId = editor.selectedTeamId
        if (selectedTeamId == null) {
            emitEffect(CampaignPostsUiEffect.ShowMessage("Chon doi phu trach truoc khi luu bai viet."))
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val result = when (editor.mode) {
                CampaignPostEditorMode.Create -> {
                    createAddPostUseCase(
                        AddPostDraft(
                            teamId = selectedTeamId,
                            authorId = authorId,
                            title = editor.title,
                            content = editor.content,
                            attachmentNames = editor.attachmentNames
                        )
                    )
                }

                CampaignPostEditorMode.Edit -> {
                    updatePostUseCase(
                        postId = checkNotNull(editor.postId),
                        draft = UpdatePostDraft(
                            title = editor.title,
                            content = editor.content,
                            teamId = selectedTeamId
                        )
                    )
                }
            }

            when (result) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            editor = null,
                            isSaving = false
                        )
                    }
                    emitEffect(
                        CampaignPostsUiEffect.ShowMessage(
                            if (editor.mode == CampaignPostEditorMode.Create) {
                                "Da tao bai viet moi cho chien dich."
                            } else {
                                "Da cap nhat bai viet."
                            }
                        )
                    )
                    loadCampaignPosts()
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    emitEffect(CampaignPostsUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }

    private fun deletePost() {
        if (!canManagePosts) {
            return
        }
        val target = _uiState.value.deleteTarget ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (val result = deletePostUseCase(target.id)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            deleteTarget = null,
                            isSaving = false
                        )
                    }
                    emitEffect(CampaignPostsUiEffect.ShowMessage("Bai viet da duoc xoa khoi danh sach."))
                    loadCampaignPosts()
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    emitEffect(CampaignPostsUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }

    private fun publishUi(
        selectedTeamId: Int?,
        errorMessage: String? = null,
        isLoading: Boolean = false,
        isRefreshing: Boolean = false
    ) {
        val context = campaignContext ?: return
        val selected = selectedTeamId?.takeIf { teamId ->
            context.teams.any { it.id == teamId }
        }
        val postCountByTeam = allPosts.groupingBy { it.teamId }.eachCount()
        val visiblePosts = allPosts.filter { selected == null || it.teamId == selected }
        val teamMetadata = context.teams.associateBy { it.id }

        _uiState.update { current ->
            current.copy(
                appName = context.appName,
                campaignTitle = context.campaignTitle,
                campaignSubtitle = context.campaignSubtitle,
                canManagePosts = canManagePosts,
                summary = CampaignPostsSummaryUiModel(
                    totalPosts = allPosts.size,
                    totalTeams = postCountByTeam.count { it.value > 0 },
                    latestUpdate = allPosts.firstOrNull()?.updatedAt ?: "--"
                ),
                teamFilters = context.teams.map { team ->
                    team.copy(postCount = postCountByTeam[team.id] ?: 0)
                },
                selectedTeamId = selected,
                posts = visiblePosts.map { post ->
                    val team = teamMetadata[post.teamId]
                    CampaignPostCardUiModel(
                        id = post.id,
                        teamId = post.teamId,
                        teamName = team?.label ?: post.teamName,
                        title = post.title,
                        excerpt = post.excerpt,
                        content = post.content,
                        authorName = post.authorName,
                        publishedAt = post.publishedAt,
                        updatedAt = post.updatedAt,
                        thumbnailUrl = post.thumbnailUrl,
                        accentColors = team?.accentColors ?: emptyList(),
                        attachmentLabels = post.attachmentLabels
                    )
                },
                expandedPostId = current.expandedPostId?.takeIf { expandedId ->
                    visiblePosts.any { it.id == expandedId }
                },
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                errorMessage = errorMessage
            )
        }
    }

    private fun MutableStateFlow<CampaignPostsUiState>.updateEditor(
        transform: (CampaignPostEditorUiModel) -> CampaignPostEditorUiModel
    ) {
        update { current ->
            current.copy(
                editor = current.editor?.let(transform)
            )
        }
    }

    private fun emitEffect(effect: CampaignPostsUiEffect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }

    private fun requirePostWritePermission(): Boolean {
        if (canManagePosts) {
            return true
        }
        emitEffect(CampaignPostsUiEffect.ShowMessage("Chi leader moi duoc tao, sua hoac xoa bai viet."))
        return false
    }
}

private data class CampaignPostsContext(
    val appName: String,
    val campaignTitle: String,
    val campaignSubtitle: String,
    val teams: List<CampaignPostsTeamUiModel>
)

private fun List<String>.removeAtOrKeep(index: Int): List<String> {
    if (index !in indices) return this
    return toMutableList().also { it.removeAt(index) }
}
