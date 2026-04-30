package com.example.uitvolunteermap.core.network

import com.google.gson.annotations.SerializedName

data class ApiEnvelope<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?,
    @SerializedName("error") val error: String?,
)
