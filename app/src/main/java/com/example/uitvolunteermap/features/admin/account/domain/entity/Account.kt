package com.example.uitvolunteermap.features.admin.account.domain.entity

data class Account(
    val accId: Int,
    val username: String,
    val createdAt: String?,
    val roleName: String,
    val userId: Int? = null,
    val fullName: String? = null,
    val email: String? = null
)
