package com.example.uitvolunteermap.features.post.domain.usecase

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import javax.inject.Inject

class GetPostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Int): AppResult<Post> = repository.getPost(postId)
}
