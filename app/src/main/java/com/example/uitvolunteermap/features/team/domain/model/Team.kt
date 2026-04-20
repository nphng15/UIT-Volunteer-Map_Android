package com.example.uitvolunteermap.features.team.domain.model

data class Team(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val leaderName: String,
    val attachments: List<String> = emptyList()
)