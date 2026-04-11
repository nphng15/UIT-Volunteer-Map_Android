package com.example.uitvolunteermap.features.campaign.domain.model

data class AddPostDraft(
    val teamId: Int,
    val authorId: Int,
    val title: String,
    val content: String,
    val attachmentNames: List<String>
)
