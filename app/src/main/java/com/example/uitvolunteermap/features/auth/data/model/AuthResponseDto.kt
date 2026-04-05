package com.example.uitvolunteermap.features.auth.data.model
import com.google.gson.annotations.SerializedName

// Phong bì bao ngoài dùng chung cho toàn bộ Project
data class BaseResponseDto<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?
)

// Data cho GET /health
data class HealthDataDto(
    @SerializedName("status") val status: String,
    @SerializedName("uptime") val uptime: Double
)

// Data cho GET /verify
data class VerifyDataDto(
    @SerializedName("isExpired") val isExpired: Boolean
)