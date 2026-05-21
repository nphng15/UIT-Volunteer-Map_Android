package com.example.uitvolunteermap.features.post.data.repository

import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.CreatePostDraft
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.PostPhoto
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import com.example.uitvolunteermap.features.post.domain.entity.UpdatePostDraft
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class InMemoryPostRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PostRepository {

    private val teamNames = mapOf(
        101 to "Đội nấu cơm",
        102 to "Đội giáo dục",
        103 to "Đội truyền thông"
    )

    private val authorNames = mutableMapOf(
        20 to "Trần Thị B",
        21 to "Minh Ngoc",
        22 to "Quốc Việt"
    )

    private val posts = mutableListOf(
        Post(
            id = 201,
            title = "Đội truyền thông cập nhật nhanh loạt điểm đến trong ngày đầu",
            content = "Tổng hợp nhanh tình hình hiện trường, ảnh hoạt động và các mốc cần truyền thông tiếp theo cho từng cụm.",
            teamId = 103,
            teamName = teamNameFor(103),
            authorId = 20,
            authorName = authorNameFor(20),
            createdAt = "2026-03-27T09:00:00.000Z",
            updatedAt = "2026-03-27T09:00:00.000Z",
            isDeleted = false,
            photos = listOf(
                PostPhoto(
                    id = 301,
                    title = "Cover",
                    imageUrl = "https://example.com/mock/posts/media-cover.jpg",
                    uploadedAt = "2026-03-27T09:00:00.000Z",
                    isFirstImage = true,
                    isDeleted = false
                ),
                PostPhoto(
                    id = 302,
                    title = "Hoạt động",
                    imageUrl = "https://example.com/mock/posts/media-activity.jpg",
                    uploadedAt = "2026-03-27T09:02:00.000Z",
                    isFirstImage = false,
                    isDeleted = false
                )
            )
        ),
        Post(
            id = 202,
            title = "Đội nấu cơm mở thêm điểm tiếp nước và điều phối nhân sự",
            content = "Bài viết tổng hợp lịch trực mới, các điểm tiếp nước vừa bổ sung và danh sách tình nguyện viên phụ trách.",
            teamId = 101,
            teamName = teamNameFor(101),
            authorId = 21,
            authorName = authorNameFor(21),
            createdAt = "2026-03-26T16:15:00.000Z",
            updatedAt = "2026-03-26T16:15:00.000Z",
            isDeleted = false,
            photos = listOf(
                PostPhoto(
                    id = 303,
                    title = "Tổng hợp",
                    imageUrl = "https://example.com/mock/posts/kitchen-summary.jpg",
                    uploadedAt = "2026-03-26T16:15:00.000Z",
                    isFirstImage = false,
                    isDeleted = false
                )
            )
        )
    )

    private var nextPostId = (posts.maxOfOrNull { it.id } ?: 0) + 1
    private var nextPhotoId = (posts.flatMap { it.photos }.maxOfOrNull { it.id } ?: 0) + 1

    override suspend fun getPosts(): AppResult<List<Post>> = withContext(ioDispatcher) {
        AppResult.Success(
            posts.filterNot { it.isDeleted }
                .sortedByDescending { it.createdAt }
        )
    }

    override suspend fun getPost(postId: Int): AppResult<Post> = withContext(ioDispatcher) {
        val post = posts.firstOrNull { it.id == postId && !it.isDeleted }
            ?: return@withContext postNotFound()
        AppResult.Success(post)
    }

    override suspend fun createPost(draft: CreatePostDraft): AppResult<Post> = withContext(ioDispatcher) {
        val timestamp = nowIsoString()
        val post = Post(
            id = nextPostId++,
            title = draft.title,
            content = draft.content,
            teamId = draft.teamId,
            teamName = teamNameFor(draft.teamId),
            authorId = draft.authorId,
            authorName = authorNameFor(draft.authorId),
            createdAt = timestamp,
            updatedAt = timestamp,
            isDeleted = false,
            photos = normalizePhotos(draft.photos, timestamp)
        )
        posts.add(post)
        AppResult.Success(post)
    }

    override suspend fun updatePost(postId: Int, draft: UpdatePostDraft): AppResult<Post> =
        withContext(ioDispatcher) {
            val index = posts.indexOfFirst { it.id == postId && !it.isDeleted }
            if (index == -1) {
                return@withContext postNotFound()
            }

            val existing = posts[index]
            val updatedTeamId = draft.teamId ?: existing.teamId
            val updatedAuthorId = draft.authorId ?: existing.authorId
            val updated = existing.copy(
                title = draft.title ?: existing.title,
                content = draft.content ?: existing.content,
                teamId = updatedTeamId,
                teamName = teamNameFor(updatedTeamId),
                authorId = updatedAuthorId,
                authorName = authorNameFor(updatedAuthorId),
                updatedAt = nowIsoString()
            )
            posts[index] = updated
            AppResult.Success(updated)
        }

    override suspend fun deletePost(postId: Int): AppResult<Post> = withContext(ioDispatcher) {
        val index = posts.indexOfFirst { it.id == postId && !it.isDeleted }
        if (index == -1) {
            return@withContext postNotFound()
        }

        val deleted = posts[index].copy(
            isDeleted = true,
            updatedAt = nowIsoString()
        )
        posts[index] = deleted
        AppResult.Success(deleted)
    }

    override suspend fun addPhoto(postId: Int, photo: PostPhotoDraft): AppResult<PostPhoto> =
        withContext(ioDispatcher) {
            val index = posts.indexOfFirst { it.id == postId && !it.isDeleted }
            if (index == -1) {
                return@withContext postNotFound()
            }

            val existing = posts[index]
            val hasActivePhotos = existing.photos.any { !it.isDeleted }
            val shouldPromoteToCover = photo.isFirstImage || !hasActivePhotos
            val timestamp = nowIsoString()
            val currentPhotos = if (shouldPromoteToCover) {
                // Keep a single cover image in sync with the API's isFirstImage contract.
                existing.photos.map { it.copy(isFirstImage = false) }
            } else {
                existing.photos
            }

            val newPhoto = PostPhoto(
                id = nextPhotoId++,
                title = photo.title,
                imageUrl = photo.imageUrl,
                uploadedAt = timestamp,
                isFirstImage = shouldPromoteToCover,
                isDeleted = false
            )

            posts[index] = existing.copy(
                photos = currentPhotos + newPhoto,
                updatedAt = timestamp
            )

            AppResult.Success(newPhoto)
        }

    private fun normalizePhotos(
        photos: List<PostPhotoDraft>,
        uploadedAt: String
    ): List<PostPhoto> {
        val hasExplicitCover = photos.any { it.isFirstImage }
        return photos.mapIndexed { index, photo ->
            PostPhoto(
                id = nextPhotoId++,
                title = photo.title,
                imageUrl = photo.imageUrl,
                uploadedAt = uploadedAt,
                isFirstImage = if (hasExplicitCover) photo.isFirstImage else index == 0,
                isDeleted = false
            )
        }
    }

    private fun teamNameFor(teamId: Int): String = teamNames[teamId] ?: "Đội #$teamId"

    private fun authorNameFor(authorId: Int): String {
        return authorNames.getOrPut(authorId) { "Thành viên #$authorId" }
    }

    private fun postNotFound(): AppResult.Error {
        return AppResult.Error(
            AppError.NotFound(message = "Không tìm thấy bài viết phù hợp.")
        )
    }

    private fun nowIsoString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return formatter.format(Date())
    }
}
