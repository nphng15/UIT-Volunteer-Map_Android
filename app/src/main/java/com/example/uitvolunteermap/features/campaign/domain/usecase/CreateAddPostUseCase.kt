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
                AppError.Validation("Tieu de bai viet khong duoc de trong.")
            )
        }

        if (draft.content.isBlank()) {
            return AppResult.Error(
                AppError.Validation("Noi dung mo ta khong duoc de trong.")
            )
        }

        if (draft.attachmentNames.size > 5) {
            return AppResult.Error(
                AppError.Validation("Popup nay chi ho tro toi da 5 anh dinh kem.")
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
