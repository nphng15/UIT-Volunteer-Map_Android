package com.example.uitvolunteermap.features.admin.account.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.account.domain.entity.Account

interface AccountRepository {

    suspend fun getAccounts(): AppResult<List<Account>>

    suspend fun createAccount(
        fullname: String,
        mssv: String,
        className: String,
        email: String,
        teamId: Int,
        phoneNumber: String,
        username: String,
        password: String
    ): AppResult<Unit>

    /**
     * @param roleName gửi đúng TÊN vai trò ("admin"|"leader"|"volunteer") — backend
     * map sang roleId nội bộ dù field tên là `roleId`.
     */
    suspend fun updateAccount(
        accountId: Int,
        password: String? = null,
        roleName: String? = null
    ): AppResult<Account>

    suspend fun deleteAccount(accountId: Int): AppResult<Unit>
}
