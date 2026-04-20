package com.example.uitvolunteermap.features.post.domain.entity

data class PostUiModel(
    val id: Int,
    val teamId: Int,
    val teamName: String,
    val title: String,
    val excerpt: String,
    val content: String,
    val publishedAt: String,
    val updatedAt: String,
    val authorName: String,
    val subtitle: String,
    val thumbnailUrl: String?,
    val attachmentLabels: List<String>
)
