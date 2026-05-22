package com.example.uitvolunteermap.features.post.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.UpdatePostDraft
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import javax.inject.Inject

class UpdatePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Int, draft: UpdatePostDraft): AppResult<Post> = when {
        postId <= 0 -> AppResult.Error(
            AppError.Validation("Mã bài viết không hợp lệ.")
        )

        !draft.hasChanges() -> AppResult.Error(
            AppError.Validation("Cần ít nhất một trường để cập nhật bài viết.")
        )

        draft.title != null && draft.title.trim().isEmpty() -> AppResult.Error(
            AppError.Validation("Tiêu đề bài viết không được để trống.")
        )

        draft.content != null && draft.content.trim().isEmpty() -> AppResult.Error(
            AppError.Validation("Nội dung bài viết không được để trống.")
        )

        draft.teamId != null && draft.teamId <= 0 -> AppResult.Error(
            AppError.Validation("Mã đội không hợp lệ.")
        )

        draft.authorId != null && draft.authorId <= 0 -> AppResult.Error(
            AppError.Validation("Mã người đăng không hợp lệ.")
        )

        else -> repository.updatePost(
            postId = postId,
            draft = draft.copy(
                title = draft.title?.trim(),
                content = draft.content?.trim()
            )
        )
    }
}
