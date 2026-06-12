package com.example.uitvolunteermap.features.attendance.data.model

import com.google.gson.annotations.SerializedName

data class TeamAttendanceDto(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("campaignId") val campaignId: Int?,
    @SerializedName("campaignName") val campaignName: String?,
    @SerializedName("date") val date: String,
    @SerializedName("members") val members: List<MemberAttendanceDto>
)

data class MemberAttendanceDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("mssv") val mssv: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("hasCheckedIn") val hasCheckedIn: Boolean,
    @SerializedName("checkedInAt") val checkedInAt: String?,
    @SerializedName("distance") val distance: Double?,
    @SerializedName("imageUrl") val imageUrl: String?
)

data class ManagedTeamDto(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("campaignId") val campaignId: Int?,
    @SerializedName("campaignName") val campaignName: String?
)
