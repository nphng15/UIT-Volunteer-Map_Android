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
            AppError.Validation("Post id khong hop le.")
        )

        !draft.hasChanges() -> AppResult.Error(
            AppError.Validation("Can it nhat mot truong de cap nhat bai viet.")
        )

        draft.title != null && draft.title.trim().isEmpty() -> AppResult.Error(
            AppError.Validation("Tieu de bai viet khong duoc de trong.")
        )

        draft.content != null && draft.content.trim().isEmpty() -> AppResult.Error(
            AppError.Validation("Noi dung bai viet khong duoc de trong.")
        )

        draft.teamId != null && draft.teamId <= 0 -> AppResult.Error(
            AppError.Validation("Team id khong hop le.")
        )

        draft.authorId != null && draft.authorId <= 0 -> AppResult.Error(
            AppError.Validation("Author id khong hop le.")
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
