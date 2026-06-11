package com.example.uitvolunteermap.features.admin.account.data.mapper

import com.example.uitvolunteermap.features.admin.account.data.model.AccountDto
import com.example.uitvolunteermap.features.admin.account.domain.entity.Account

fun AccountDto.toDomain(): Account = Account(
    accId = accId,
    username = username,
    createdAt = createdAt,
    roleName = roleName,
    userId = userId,
    fullName = fullName,
    email = email
)
