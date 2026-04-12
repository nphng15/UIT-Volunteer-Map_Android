package com.example.uitvolunteermap.features.campaign.data.model

import com.google.gson.annotations.SerializedName

// GET /teams — public, trả về danh sách rút gọn
data class TeamListItemDto(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("leaders") val leaders: List<TeamLeaderDto>
)

// GET /teams/:id — public, trả về chi tiết một team kèm leaders
data class TeamDto(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("leaders") val leaders: List<TeamLeaderDto>
)

// Leader nested object dùng chung cho cả hai endpoint
data class TeamLeaderDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("role") val role: String,
    @SerializedName("avatarUrl") val avatarUrl: String?   // chỉ có ở /teams/:id
)
