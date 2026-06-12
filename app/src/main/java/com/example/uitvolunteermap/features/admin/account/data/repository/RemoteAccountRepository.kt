package com.example.uitvolunteermap.features.admin.account.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.core.network.apiCall
import com.example.uitvolunteermap.core.network.apiCallRaw
import com.example.uitvolunteermap.core.network.apiCallUnit
import com.example.uitvolunteermap.features.admin.account.data.datasource.AccountApiService
import com.example.uitvolunteermap.features.admin.account.data.datasource.CreateAccountRequest
import com.example.uitvolunteermap.features.admin.account.data.datasource.UpdateAccountRequest
import com.example.uitvolunteermap.features.admin.account.data.mapper.toDomain
import com.example.uitvolunteermap.features.admin.account.domain.entity.Account
import com.example.uitvolunteermap.features.admin.account.domain.repository.AccountRepository
import javax.inject.Inject

class RemoteAccountRepository @Inject constructor(
    private val api: AccountApiService
) : AccountRepository {

    override suspend fun getAccounts(): AppResult<List<Account>> = apiCallRaw(
        request = { api.getAccounts() },
        map = { accounts -> accounts.map { it.toDomain() } }
    )

    override suspend fun createAccount(
        fullname: String,
        mssv: String,
        className: String,
        email: String,
        phoneNumber: String,
        username: String,
        password: String,
        role: String
    ): AppResult<Unit> = apiCallUnit(
        request = {
            api.createAccount(
                CreateAccountRequest(
                    fullname = fullname,
                    mssv = mssv,
                    className = className,
                    email = email,
                    phoneNumber = phoneNumber,
                    username = username,
                    password = password,
                    role = role
                )
            )
        }
    )

    override suspend fun updateAccount(
        accountId: Int,
        password: String?,
        roleName: String?
    ): AppResult<Account> = apiCall(
        request = {
            api.updateAccount(
                accountId = accountId,
                body = UpdateAccountRequest(
                    password = password,
                    roleId = roleName
                )
            )
        },
        map = { it.toDomain() }
    )

    override suspend fun deleteAccount(accountId: Int): AppResult<Unit> = apiCallUnit(
        request = { api.deleteAccount(accountId) }
    )
}
