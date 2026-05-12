package com.example.uitvolunteermap.features.admin.post.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.post.domain.entity.AdminPost

interface AdminPostRepository {
    suspend fun getPosts(): AppResult<List<AdminPost>>

    suspend fun createPost(
        title: String,
        content: String,
        teamId: Int,
        authorId: Int
    ): AppResult<Unit>

    suspend fun updatePost(
        postId: Int,
        title: String?,
        content: String?,
        teamId: Int?,
        authorId: Int?
    ): AppResult<Unit>

    suspend fun deletePost(postId: Int): AppResult<Unit>
}
