package com.example.uitvolunteermap.features.campaign.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.AddPostDraft
import com.example.uitvolunteermap.features.campaign.domain.repository.AddPostRepository
import javax.inject.Inject

class CreateAddPostUseCase @Inject constructor(
    private val repository: AddPostRepository
) {
    suspend operator fun invoke(draft: AddPostDraft): AppResult<Unit> {
        if (draft.title.isBlank()) {
            return AppResult.Error(
                AppError.Validation("Tiêu đề bài viết không được để trống.")
            )
        }

        if (draft.content.isBlank()) {
            return AppResult.Error(
                AppError.Validation("Nội dung mô tả không được để trống.")
            )
        }

        if (draft.attachmentNames.size > 5) {
            return AppResult.Error(
                AppError.Validation("Biểu mẫu này chỉ hỗ trợ tối đa 5 ảnh đính kèm.")
            )
        }

        return repository.createPost(
            draft.copy(
                title = draft.title.trim(),
                content = draft.content.trim()
            )
        )
    }
}
