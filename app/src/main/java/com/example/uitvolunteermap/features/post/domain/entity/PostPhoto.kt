package com.example.uitvolunteermap.features.post.domain.entity

data class PostPhoto(
    val id: Int,
    val title: String?,
    val imageUrl: String,
    val uploadedAt: String,
    val isFirstImage: Boolean,
    val isDeleted: Boolean
)
