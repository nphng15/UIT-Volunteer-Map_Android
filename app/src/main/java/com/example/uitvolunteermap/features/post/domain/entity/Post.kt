package com.example.uitvolunteermap.features.post.domain.entity

data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val teamId: Int,
    val teamName: String,
    val authorId: Int,
    val authorName: String,
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean,
    val photos: List<PostPhoto> = emptyList()
)
