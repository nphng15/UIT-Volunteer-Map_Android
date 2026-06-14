package com.example.uitvolunteermap.features.admin.post.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitvolunteermap.core.common.error.userMessage
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.session.SessionManager
import com.example.uitvolunteermap.features.admin.post.domain.entity.AdminPost
import com.example.uitvolunteermap.features.admin.post.domain.usecase.GetAdminPostsUseCase
import com.example.uitvolunteermap.features.admin.post.domain.usecase.ManageAdminPostUseCase
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
class AdminPostViewModel @Inject constructor(
    private val getAdminPostsUseCase: GetAdminPostsUseCase,
    private val manageAdminPostUseCase: ManageAdminPostUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdminPostUiState(canManagePosts = sessionManager.canManagePosts)
    )
    val uiState: StateFlow<AdminPostUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AdminPostUiEffect>()
    val uiEffect: SharedFlow<AdminPostUiEffect> = _uiEffect.asSharedFlow()

    // Giữ bản domain gốc để fill form Edit (UI model đã rút gọn, thiếu content/teamId/authorId)
    private var loadedPosts: List<AdminPost> = emptyList()

    init {
        onEvent(AdminPostUiEvent.RefreshRequested)
    }

    fun onEvent(event: AdminPostUiEvent) {
        when (event) {
            AdminPostUiEvent.RefreshRequested -> loadPosts(isPullRefresh = false)
            AdminPostUiEvent.PullToRefreshTriggered -> loadPosts(isPullRefresh = true)

            AdminPostUiEvent.CreateClicked -> openCreateForm()
            is AdminPostUiEvent.EditClicked -> openEditForm(event.postId)
            is AdminPostUiEvent.TitleChanged ->
                updateForm { it.copy(title = event.value) }
            is AdminPostUiEvent.ContentChanged ->
                updateForm { it.copy(content = event.value) }
            is AdminPostUiEvent.TeamIdChanged ->
                updateForm { it.copy(teamIdInput = event.value.filter(Char::isDigit)) }
            is AdminPostUiEvent.AuthorIdChanged ->
                updateForm { it.copy(authorIdInput = event.value.filter(Char::isDigit)) }
            AdminPostUiEvent.FormSubmitted -> submitForm()
            AdminPostUiEvent.FormDismissed ->
                _uiState.update { it.copy(form = null) }

            is AdminPostUiEvent.DeleteClicked -> handleDeleteClicked(event.postId)
            AdminPostUiEvent.DeleteConfirmed -> handleDeleteConfirmed()
            AdminPostUiEvent.DeleteCancelled ->
                _uiState.update { it.copy(pendingDeleteId = null) }
        }
    }

    // ─── Load ──────────────────────────────────────────────────────────────────

    private fun loadPosts(isPullRefresh: Boolean) {
        viewModelScope.launch {
            if (isPullRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            when (val result = getAdminPostsUseCase()) {
                is AppResult.Success -> {
                    loadedPosts = result.data
                    _uiState.update {
                        it.copy(
                            posts = result.data.map(::toUiModel),
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.error.userMessage
                        )
                    }
                }
            }
        }
    }

    // ─── Form ──────────────────────────────────────────────────────────────────

    private fun openCreateForm() {
        if (!sessionManager.canManagePosts) return
        _uiState.update {
            it.copy(form = AdminPostFormState(mode = AdminPostFormMode.Create))
        }
    }

    private fun openEditForm(postId: Int) {
        if (!sessionManager.canManagePosts) return
        val post = loadedPosts.firstOrNull { it.id == postId } ?: return
        _uiState.update {
            it.copy(
                form = AdminPostFormState(
                    mode = AdminPostFormMode.Edit,
                    postId = post.id,
                    title = post.title,
                    content = post.content,
                    teamIdInput = post.teamId.takeIf { id -> id > 0 }?.toString().orEmpty(),
                    authorIdInput = post.authorId.takeIf { id -> id > 0 }?.toString().orEmpty()
                )
            )
        }
    }

    private inline fun updateForm(transform: (AdminPostFormState) -> AdminPostFormState) {
        _uiState.update { state ->
            state.form?.let { state.copy(form = transform(it)) } ?: state
        }
    }

    private fun submitForm() {
        if (_uiState.value.isSaving) return
        val form = _uiState.value.form ?: return
        val teamId = form.teamIdInput.toIntOrNull() ?: 0
        val authorId = form.authorIdInput.toIntOrNull() ?: 0

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val result = when (form.mode) {
                AdminPostFormMode.Create -> manageAdminPostUseCase.create(
                    title = form.title,
                    content = form.content,
                    teamId = teamId,
                    authorId = authorId
                )

                AdminPostFormMode.Edit -> manageAdminPostUseCase.update(
                    postId = form.postId ?: 0,
                    title = form.title,
                    content = form.content,
                    teamId = teamId,
                    authorId = authorId
                )
            }

            when (result) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, form = null) }
                    val message = if (form.mode == AdminPostFormMode.Create) {
                        "Đăng bài viết thành công."
                    } else {
                        "Cập nhật bài viết thành công."
                    }
                    _uiEffect.emit(AdminPostUiEffect.ShowMessage(message))
                    // Mutation trả entity raw → refetch để lấy team/author/thumbnail đầy đủ
                    loadPosts(isPullRefresh = false)
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEffect.emit(AdminPostUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }

    // ─── Delete ────────────────────────────────────────────────────────────────

    private fun handleDeleteClicked(postId: Int) {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(pendingDeleteId = postId) }
    }

    private fun handleDeleteConfirmed() {
        if (!sessionManager.canManagePosts) return
        if (_uiState.value.isDeleting) return
        val postId = _uiState.value.pendingDeleteId ?: return

        _uiState.update { it.copy(pendingDeleteId = null, isDeleting = true) }
        viewModelScope.launch {
            when (val result = manageAdminPostUseCase.delete(postId)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEffect.emit(AdminPostUiEffect.ShowMessage("Xóa bài viết thành công."))
                    loadPosts(isPullRefresh = false)
                }

                is AppResult.Error -> {
                    _uiState.update { it.copy(isDeleting = false) }
                    _uiEffect.emit(AdminPostUiEffect.ShowMessage(result.error.userMessage))
                }
            }
        }
    }

    private fun toUiModel(post: AdminPost): AdminPostListItemUiModel = AdminPostListItemUiModel(
        id = post.id,
        title = post.title,
        teamName = post.teamName,
        authorName = post.authorName,
        thumbnailUrl = post.thumbnailUrl,
        createdAt = post.createdAt
    )
}
