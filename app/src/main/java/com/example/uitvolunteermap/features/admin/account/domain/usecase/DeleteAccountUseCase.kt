package com.example.uitvolunteermap.features.admin.account.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.account.domain.repository.AccountRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(accountId: Int): AppResult<Unit> {
        if (accountId <= 0)
            return AppResult.Error(AppError.Validation("Mã tài khoản không hợp lệ."))
        return repository.deleteAccount(accountId)
    }
}
