package com.example.uitvolunteermap.features.profile.domain.entity

data class ProfileInfo(
    val userId: String,
    val fullName: String,
    val mssv: String,
    val className: String,
    val email: String,
    val phoneNumber: String,
    val createdAt: String
)