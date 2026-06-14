package com.example.uitvolunteermap.features.admin.account.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.account.domain.entity.Account
import com.example.uitvolunteermap.features.admin.account.domain.repository.AccountRepository
import javax.inject.Inject

/**
 * Cập nhật tài khoản. Backend chỉ cho phép đổi `password` và `roleName`
 * (gửi tên vai trò). Cần ít nhất một trong hai trường được cung cấp.
 */
class UpdateAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    companion object {
        val ALLOWED_ROLES = setOf("admin", "leader", "volunteer")
    }

    suspend operator fun invoke(
        accountId: Int,
        password: String? = null,
        roleName: String? = null
    ): AppResult<Account> {
        if (accountId <= 0)
            return AppResult.Error(AppError.Validation("Mã tài khoản không hợp lệ."))

        val normalizedPassword = password?.takeIf { it.isNotEmpty() }
        val normalizedRole = roleName?.trim()?.lowercase()?.takeIf { it.isNotEmpty() }

        if (normalizedPassword == null && normalizedRole == null)
            return AppResult.Error(AppError.Validation("Cần cung cấp mật khẩu mới hoặc vai trò để cập nhật."))

        if (normalizedPassword != null && normalizedPassword.length < 6)
            return AppResult.Error(AppError.Validation("Mật khẩu phải có ít nhất 6 ký tự."))

        if (normalizedRole != null && normalizedRole !in ALLOWED_ROLES)
            return AppResult.Error(AppError.Validation("Vai trò không hợp lệ."))

        return repository.updateAccount(
            accountId = accountId,
            password = normalizedPassword,
            roleName = normalizedRole
        )
    }
}
