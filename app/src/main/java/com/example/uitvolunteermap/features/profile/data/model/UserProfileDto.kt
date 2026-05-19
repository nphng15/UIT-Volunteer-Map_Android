package com.example.uitvolunteermap.features.profile.data.model

import com.google.gson.annotations.SerializedName

data class UserProfileDto(
    @SerializedName("UserId") val userId: String,
    @SerializedName("FullName") val fullName: String,
    @SerializedName("Mssv") val mssv: String,
    @SerializedName("Class") val className: String,
    @SerializedName("Email") val email: String,
    @SerializedName("PhoneNumber") val phoneNumber: String,
    @SerializedName("created_at") val createdAt: String?
)
