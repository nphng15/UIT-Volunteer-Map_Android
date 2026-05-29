package com.example.uitvolunteermap.features.post.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.features.post.data.mapper.toDomain
import com.example.uitvolunteermap.features.post.data.remote.AddPostPhotoRequest
import com.example.uitvolunteermap.features.post.data.remote.CreatePostPhotoRequest
import com.example.uitvolunteermap.features.post.data.remote.CreatePostRequest
import com.example.uitvolunteermap.features.post.data.remote.PostApiService
import com.example.uitvolunteermap.features.post.data.remote.UpdatePostRequest
import com.example.uitvolunteermap.features.post.domain.entity.CreatePostDraft
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.PostPhoto
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import com.example.uitvolunteermap.features.post.domain.entity.UpdatePostDraft
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import javax.inject.Inject

class RemotePostRepository @Inject constructor(
    private val postApiService: PostApiService
) : PostRepository {

    override suspend fun getPosts(): AppResult<List<Post>> = apiCall(
        request = { postApiService.getPosts() },
        map = { posts -> posts.map { it.toDomain() } }
    )

    override suspend fun getPost(postId: Int): AppResult<Post> = apiCall(
        request = { postApiService.getPost(postId) },
        map = { it.toDomain() }
    )

    override suspend fun createPost(draft: CreatePostDraft): AppResult<Post> = apiCall(
        request = {
            postApiService.createPost(
                CreatePostRequest(
                    title = draft.title,
                    content = draft.content,
                    teamId = draft.teamId,
                    authorId = draft.authorId,
                    photos = draft.photos.map {
                        CreatePostPhotoRequest(
                            title = it.title,
                            imageUrl = it.imageUrl,
                            isFirstImage = if (it.isFirstImage) 1 else 0
                        )
                    }
                )
            )
        },
        map = { it.toDomain(teamId = draft.teamId, authorId = draft.authorId) }
    )

    override suspend fun updatePost(postId: Int, draft: UpdatePostDraft): AppResult<Post> = apiCall(
        request = {
            postApiService.updatePost(
                postId = postId,
                body = UpdatePostRequest(
                    title = draft.title,
                    content = draft.content,
                    teamId = draft.teamId,
                    authorId = draft.authorId
                )
            )
        },
        map = {
            it.toDomain(
                teamId = draft.teamId ?: 0,
                authorId = draft.authorId ?: 0
            )
        }
    )

    override suspend fun deletePost(postId: Int): AppResult<Post> = apiCall(
        request = { postApiService.deletePost(postId) },
        map = { it.toDomain() }
    )

    override suspend fun addPhoto(postId: Int, photo: PostPhotoDraft): AppResult<PostPhoto> = apiCall(
        request = {
            postApiService.addPhoto(
                postId = postId,
                body = AddPostPhotoRequest(
                    title = photo.title,
                    imageUrl = photo.imageUrl,
                    isFirstImage = if (photo.isFirstImage) 1 else 0
                )
            )
        },
        map = { it.toDomain() }
    )
}
