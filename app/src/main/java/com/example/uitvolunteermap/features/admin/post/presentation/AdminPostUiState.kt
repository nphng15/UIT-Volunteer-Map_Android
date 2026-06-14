package com.example.uitvolunteermap.features.admin.post.presentation

/**
 * Chế độ của form bottom-sheet: tạo mới hoặc chỉnh sửa bài viết hiện có.
 */
enum class AdminPostFormMode { Create, Edit }

data class AdminPostFormState(
    val mode: AdminPostFormMode = AdminPostFormMode.Create,
    val postId: Int? = null,
    val title: String = "",
    val content: String = "",
    // teamId / authorId nhập dạng text để cho phép rỗng & validate tại form
    val teamIdInput: String = "",
    val authorIdInput: String = ""
)

data class AdminPostListItemUiModel(
    val id: Int,
    val title: String,
    val teamName: String,
    val authorName: String,
    val thumbnailUrl: String?,
    val createdAt: String
)

data class AdminPostUiState(
    val posts: List<AdminPostListItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val canManagePosts: Boolean = false,
    // Form (create/edit) — null = đóng
    val form: AdminPostFormState? = null,
    val isSaving: Boolean = false,
    // Xoá
    val pendingDeleteId: Int? = null,
    val isDeleting: Boolean = false
)
