package com.example.uitvolunteermap.features.admin.account.domain.usecase

import com.example.uitvolunteermap.features.admin.account.domain.repository.AccountRepository
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke() = repository.getAccounts()
}
