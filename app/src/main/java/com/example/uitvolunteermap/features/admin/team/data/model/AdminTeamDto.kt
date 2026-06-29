package com.example.uitvolunteermap.features.admin.team.data.model

import com.google.gson.annotations.SerializedName

/**
 * GET /teams — phản hồi cho người dùng đã đăng nhập (admin) có đầy đủ leaders + members.
 * imageUrl và description có thể null. members chỉ chứa userId + fullName (không có vai trò).
 */
data class AdminTeamListItemDto(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("checkInLatitude") val checkInLatitude: Double?,
    @SerializedName("checkInLongitude") val checkInLongitude: Double?,
    @SerializedName("checkInRadius") val checkInRadius: Double?,
    @SerializedName("leaders") val leaders: List<AdminTeamLeaderDto>?,
    @SerializedName("members") val members: List<AdminTeamMemberDto>?
)

/**
 * GET /teams/{id} — chi tiết một team. Không có mảng members theo hợp đồng API hiện tại;
 * leaders ở đây kèm avatarUrl (khác với danh sách GET /teams).
 */
data class AdminTeamDetailDto(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("checkInLatitude") val checkInLatitude: Double?,
    @SerializedName("checkInLongitude") val checkInLongitude: Double?,
    @SerializedName("checkInRadius") val checkInRadius: Double?,
    @SerializedName("leaders") val leaders: List<AdminTeamLeaderDto>?
)

/** Leader lồng nhau dùng chung cho cả hai endpoint. avatarUrl chỉ xuất hiện ở GET /teams/{id}. */
data class AdminTeamLeaderDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("role") val role: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?
)

/** Member lồng nhau — chỉ có ở danh sách GET /teams khi đã đăng nhập. */
data class AdminTeamMemberDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("fullName") val fullName: String
)
