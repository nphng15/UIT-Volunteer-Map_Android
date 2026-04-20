package com.example.uitvolunteermap.features.post.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.CreatePostDraft
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.PostPhoto
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import com.example.uitvolunteermap.features.post.domain.entity.UpdatePostDraft

interface PostRepository {
    suspend fun getPosts(): AppResult<List<Post>>

    suspend fun getPost(postId: Int): AppResult<Post>

    suspend fun createPost(draft: CreatePostDraft): AppResult<Post>

    suspend fun updatePost(postId: Int, draft: UpdatePostDraft): AppResult<Post>

    suspend fun deletePost(postId: Int): AppResult<Post>

    suspend fun addPhoto(postId: Int, photo: PostPhotoDraft): AppResult<PostPhoto>
}
