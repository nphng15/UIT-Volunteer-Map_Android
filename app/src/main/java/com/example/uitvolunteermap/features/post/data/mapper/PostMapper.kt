package com.example.uitvolunteermap.features.post.data.mapper

import com.example.uitvolunteermap.features.post.data.remote.PostListItemDto
import com.example.uitvolunteermap.features.post.data.remote.PostMutationResponseDto
import com.example.uitvolunteermap.features.post.data.remote.PostPhotoDto
import com.example.uitvolunteermap.features.post.data.remote.RawPostDto
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.PostPhoto

fun PostListItemDto.toDomain(): Post = Post(
    id = postId,
    title = title,
    content = content,
    teamId = team?.teamId ?: 0,
    teamName = team?.teamName ?: "Chưa rõ đội",
    authorId = author?.userId ?: 0,
    authorName = author?.fullName ?: "Chưa rõ tác giả",
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = false,
    photos = thumbnail?.let {
        listOf(
            PostPhoto(
                id = it.photoId,
                title = it.title,
                imageUrl = it.imageUrl,
                uploadedAt = createdAt,
                isFirstImage = true,
                isDeleted = false
            )
        )
    }.orEmpty()
)

fun RawPostDto.toDomain(
    teamId: Int = 0,
    teamName: String = "Chưa rõ đội",
    authorId: Int = 0,
    authorName: String = "Chưa rõ tác giả"
): Post = Post(
    id = postId,
    title = title,
    content = content,
    teamId = teamId,
    teamName = teamName,
    authorId = authorId,
    authorName = authorName,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted == 1,
    photos = emptyList()
)

fun PostMutationResponseDto.toDomain(
    teamId: Int,
    teamName: String = "Đội #$teamId",
    authorId: Int,
    authorName: String = "Tài khoản #$authorId"
): Post = Post(
    id = postId,
    title = title,
    content = content,
    teamId = teamId,
    teamName = teamName,
    authorId = authorId,
    authorName = authorName,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDeleted = isDeleted == 1,
    photos = photos.orEmpty().map { it.toDomain() }
)

fun PostPhotoDto.toDomain(): PostPhoto = PostPhoto(
    id = photoId,
    title = title,
    imageUrl = imageUrl,
    uploadedAt = uploadedAt,
    isFirstImage = isFirstImage == 1,
    isDeleted = isDeleted == 1
)
