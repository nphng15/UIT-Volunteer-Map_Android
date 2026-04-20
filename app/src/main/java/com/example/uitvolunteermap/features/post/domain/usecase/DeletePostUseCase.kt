package com.example.uitvolunteermap.features.post.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Int): AppResult<Post> = if (postId <= 0) {
        AppResult.Error(AppError.Validation("Post id khong hop le."))
    } else {
        repository.deletePost(postId)
    }
}
