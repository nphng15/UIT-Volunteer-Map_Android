package com.example.uitvolunteermap.features.post.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.post.data.remote.AddPostPhotoRequest
import com.example.uitvolunteermap.features.post.data.remote.CreatePostRequest
import com.example.uitvolunteermap.features.post.data.remote.PostApiService
import com.example.uitvolunteermap.features.post.data.remote.PostAuthorDto
import com.example.uitvolunteermap.features.post.data.remote.PostListItemDto
import com.example.uitvolunteermap.features.post.data.remote.PostMutationResponseDto
import com.example.uitvolunteermap.features.post.data.remote.PostPhotoDto
import com.example.uitvolunteermap.features.post.data.remote.PostTeamDto
import com.example.uitvolunteermap.features.post.data.remote.PostThumbnailDto
import com.example.uitvolunteermap.features.post.data.remote.RawPostDto
import com.example.uitvolunteermap.features.post.data.remote.UpdatePostRequest
import com.example.uitvolunteermap.features.post.domain.entity.CreatePostDraft
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RemotePostRepositoryTest {

    @Test
    fun getPosts_maps_backend_postId_thumbnail_team_and_author() = runTest {
        val repository = RemotePostRepository(FakePostApiService())

        val result = repository.getPosts()

        assertTrue(result is AppResult.Success)
        val post = (result as AppResult.Success).data.single()
        assertEquals(11, post.id)
        assertEquals(3, post.teamId)
        assertEquals("Media Team", post.teamName)
        assertEquals(20, post.authorId)
        assertEquals("Tran Thi B", post.authorName)
        assertEquals(100, post.photos.single().id)
        assertTrue(post.photos.single().isFirstImage)
    }

    @Test
    fun createPost_sends_integer_firstImage_and_maps_photoId() = runTest {
        val api = FakePostApiService()
        val repository = RemotePostRepository(api)

        val result = repository.createPost(
            CreatePostDraft(
                teamId = 3,
                authorId = 20,
                title = "Campaign Kickoff",
                content = "Opening event details",
                photos = listOf(
                    PostPhotoDraft(
                        title = "Cover",
                        imageUrl = "https://example.com/post-1.jpg",
                        isFirstImage = true
                    )
                )
            )
        )

        assertTrue(result is AppResult.Success)
        assertEquals(1, api.lastCreateRequest?.photos?.single()?.isFirstImage)
        val post = (result as AppResult.Success).data
        assertFalse(post.isDeleted)
        assertEquals(100, post.photos.single().id)
    }

    private class FakePostApiService : PostApiService {
        var lastCreateRequest: CreatePostRequest? = null

        override suspend fun getPosts(): ApiEnvelope<List<PostListItemDto>> =
            ApiEnvelope(
                success = true,
                data = listOf(
                    PostListItemDto(
                        postId = 11,
                        title = "Campaign Kickoff",
                        content = "Opening event details",
                        createdAt = "2026-03-20T08:00:00.000Z",
                        updatedAt = "2026-03-20T08:00:00.000Z",
                        thumbnail = PostThumbnailDto(
                            photoId = 100,
                            imageUrl = "https://example.com/post-thumb.jpg",
                            title = "Cover"
                        ),
                        team = PostTeamDto(teamId = 3, teamName = "Media Team"),
                        author = PostAuthorDto(userId = 20, fullName = "Tran Thi B")
                    )
                ),
                message = null,
                error = null
            )

        override suspend fun getPost(postId: Int): ApiEnvelope<RawPostDto> =
            ApiEnvelope(success = true, data = rawPost(postId), message = null, error = null)

        override suspend fun createPost(body: CreatePostRequest): ApiEnvelope<PostMutationResponseDto> {
            lastCreateRequest = body
            return ApiEnvelope(
                success = true,
                data = PostMutationResponseDto(
                    postId = 11,
                    title = body.title,
                    content = body.content,
                    isDeleted = 0,
                    createdAt = "2026-03-27T09:00:00.000Z",
                    updatedAt = "2026-03-27T09:00:00.000Z",
                    photos = listOf(
                        PostPhotoDto(
                            photoId = 100,
                            title = "Cover",
                            imageUrl = "https://example.com/post-1.jpg",
                            uploadedAt = "2026-03-27T09:00:00.000Z",
                            isFirstImage = 1,
                            isDeleted = 0
                        )
                    )
                ),
                message = null,
                error = null
            )
        }

        override suspend fun updatePost(
            postId: Int,
            body: UpdatePostRequest
        ): ApiEnvelope<RawPostDto> =
            ApiEnvelope(success = true, data = rawPost(postId), message = null, error = null)

        override suspend fun deletePost(postId: Int): ApiEnvelope<RawPostDto> =
            ApiEnvelope(success = true, data = rawPost(postId, isDeleted = 1), message = null, error = null)

        override suspend fun addPhoto(
            postId: Int,
            body: AddPostPhotoRequest
        ): ApiEnvelope<PostPhotoDto> =
            ApiEnvelope(
                success = true,
                data = PostPhotoDto(
                    photoId = 101,
                    title = body.title,
                    imageUrl = body.imageUrl,
                    uploadedAt = "2026-03-27T09:00:00.000Z",
                    isFirstImage = body.isFirstImage,
                    isDeleted = 0
                ),
                message = null,
                error = null
            )

        private fun rawPost(id: Int, isDeleted: Int = 0): RawPostDto = RawPostDto(
            postId = id,
            title = "Campaign Kickoff",
            content = "Opening event details",
            isDeleted = isDeleted,
            createdAt = "2026-03-20T08:00:00.000Z",
            updatedAt = "2026-03-20T08:00:00.000Z"
        )
    }
}
