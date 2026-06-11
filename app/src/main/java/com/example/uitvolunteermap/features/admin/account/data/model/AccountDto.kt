package com.example.uitvolunteermap.features.admin.account.data.model

import com.google.gson.annotations.SerializedName

data class AccountDto(
    @SerializedName("accId") val accId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("roleName") val roleName: String,
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    // Có trong response của PUT /accounts/{id}; danh sách không trả field này.
    @SerializedName("updatedAt") val updatedAt: String? = null
)
