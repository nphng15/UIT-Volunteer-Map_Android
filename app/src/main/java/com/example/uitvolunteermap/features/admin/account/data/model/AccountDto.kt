package com.example.uitvolunteermap.features.admin.account.data.model

import com.google.gson.annotations.SerializedName

data class AccountDto(
    @SerializedName("accId") val accId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("roleName") val roleName: String,
    // Có trong response của PUT /accounts/{id}; danh sách không trả field này.
    @SerializedName("updatedAt") val updatedAt: String? = null
)
