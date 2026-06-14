package com.example.uitvolunteermap.features.admin.post.domain.usecase

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.post.domain.entity.AdminPost
import com.example.uitvolunteermap.features.admin.post.domain.repository.AdminPostRepository
import javax.inject.Inject

class GetAdminPostsUseCase @Inject constructor(
    private val repository: AdminPostRepository
) {
    suspend operator fun invoke(): AppResult<List<AdminPost>> = repository.getPosts()
}
