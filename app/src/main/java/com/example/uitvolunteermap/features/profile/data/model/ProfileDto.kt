package com.example.uitvolunteermap.features.profile.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object cho Profile.
 * Khớp chính xác với PascalCase từ Backend API Docs.
 */
data class ProfileDto(
    @SerializedName("UserId") val userId: String?,
    @SerializedName("FullName") val fullName: String?,
    @SerializedName("Mssv") val mssv: String?,
    @SerializedName("Class") val className: String?,
    @SerializedName("Email") val email: String?,
    @SerializedName("PhoneNumber") val phoneNumber: String?,
    @SerializedName("created_at") val createdAt: String?
)