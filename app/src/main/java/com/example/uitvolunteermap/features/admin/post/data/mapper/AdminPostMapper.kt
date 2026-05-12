package com.example.uitvolunteermap.features.admin.post.data.mapper

import com.example.uitvolunteermap.features.admin.post.data.model.AdminPostListItemDto
import com.example.uitvolunteermap.features.admin.post.domain.entity.AdminPost

fun AdminPostListItemDto.toDomain(): AdminPost = AdminPost(
    id = postId,
    title = title,
    content = content,
    teamId = team?.teamId ?: 0,
    teamName = team?.teamName ?: "Chưa rõ đội",
    authorId = author?.userId ?: 0,
    authorName = author?.fullName ?: "Chưa rõ tác giả",
    thumbnailUrl = thumbnail?.imageUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
    // Endpoint danh sách không trả isDeleted → mặc định false
    isDeleted = false
)
