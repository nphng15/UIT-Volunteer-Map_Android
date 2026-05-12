package com.example.uitvolunteermap.features.admin.post.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.admin.post.data.datasource.AdminPostApiService
import com.example.uitvolunteermap.features.admin.post.data.mapper.toDomain
import com.example.uitvolunteermap.features.admin.post.data.model.CreateAdminPostRequest
import com.example.uitvolunteermap.features.admin.post.data.model.UpdateAdminPostRequest
import com.example.uitvolunteermap.features.admin.post.domain.entity.AdminPost
import com.example.uitvolunteermap.features.admin.post.domain.repository.AdminPostRepository
import javax.inject.Inject

class RemoteAdminPostRepository @Inject constructor(
    private val api: AdminPostApiService
) : AdminPostRepository {

    override suspend fun getPosts(): AppResult<List<AdminPost>> = apiCall(
        request = { api.getPosts() },
        map = { posts -> posts.map { it.toDomain() } }
    )

    // Mutation trả về entity TypeORM raw (shape khác GET). Không map sang domain —
    // chỉ cần xác nhận thành công; ViewModel sẽ refetch danh sách sau đó.
    override suspend fun createPost(
        title: String,
        content: String,
        teamId: Int,
        authorId: Int
    ): AppResult<Unit> = apiCall(
        request = {
            api.createPost(
                CreateAdminPostRequest(
                    title = title,
                    content = content,
                    teamId = teamId,
                    authorId = authorId,
                    photos = null
                )
            )
        },
        map = { }
    )

    override suspend fun updatePost(
        postId: Int,
        title: String?,
        content: String?,
        teamId: Int?,
        authorId: Int?
    ): AppResult<Unit> = apiCall(
        request = {
            api.updatePost(
                postId = postId,
                body = UpdateAdminPostRequest(
                    title = title,
                    content = content,
                    teamId = teamId,
                    authorId = authorId
                )
            )
        },
        map = { }
    )

    override suspend fun deletePost(postId: Int): AppResult<Unit> = apiCall(
        request = { api.deletePost(postId) },
        map = { }
    )
}
