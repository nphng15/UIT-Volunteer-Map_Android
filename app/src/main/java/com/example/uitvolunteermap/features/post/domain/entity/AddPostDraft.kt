package com.example.uitvolunteermap.features.post.domain.entity

data class AddPostDraft(
    val teamId: Int,
    val authorId: Int,
    val title: String,
    val content: String,
    val attachmentNames: List<String>
)
