package com.example.uitvolunteermap.features.admin.post.domain.entity

/**
 * Mô hình bài viết dùng cho khu quản trị. Lấy từ `GET /posts` nên team/author có thể
 * thiếu (API trả null) → mapper đã điền giá trị mặc định.
 */
data class AdminPost(
    val id: Int,
    val title: String,
    val content: String,
    val teamId: Int,
    val teamName: String,
    val authorId: Int,
    val authorName: String,
    val thumbnailUrl: String?,
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean
)
