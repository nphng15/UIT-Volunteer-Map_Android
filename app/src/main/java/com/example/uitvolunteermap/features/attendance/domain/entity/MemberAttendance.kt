package com.example.uitvolunteermap.features.attendance.domain.entity

data class MemberAttendance(
    val userId: Int,
    val fullName: String,
    val mssv: String?,
    val avatarUrl: String?,
    val hasCheckedIn: Boolean,
    val checkedInAt: String?,
    val distance: Double?,
    val imageUrl: String?
)
