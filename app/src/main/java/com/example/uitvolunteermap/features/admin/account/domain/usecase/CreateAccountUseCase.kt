package com.example.uitvolunteermap.features.admin.account.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.account.domain.repository.AccountRepository
import javax.inject.Inject

/**
 * Tạo tài khoản mới. Validate toàn bộ ràng buộc của backend ở client trước khi gọi API
 * để báo lỗi sớm bằng tiếng Việt (giống cách [ManageCampaignUseCase] làm cho chiến dịch).
 */
class CreateAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    companion object {
        const val EMAIL_SUFFIX = "@gm.uit.edu.vn"
        const val MSSV_LENGTH = 8
    }

    suspend operator fun invoke(
        fullname: String,
        mssv: String,
        className: String,
        email: String,
        teamId: Int,
        phoneNumber: String,
        username: String,
        password: String
    ): AppResult<Unit> {
        val trimmedFullname = fullname.trim()
        val trimmedMssv = mssv.trim()
        val trimmedClass = className.trim()
        val trimmedEmail = email.trim()
        val trimmedPhone = phoneNumber.trim()
        val trimmedUsername = username.trim()

        if (trimmedFullname.length < 3)
            return AppResult.Error(AppError.Validation("Họ và tên phải có ít nhất 3 ký tự."))
        if (trimmedMssv.length != MSSV_LENGTH)
            return AppResult.Error(AppError.Validation("MSSV phải gồm đúng $MSSV_LENGTH ký tự."))
        if (trimmedClass.isBlank())
            return AppResult.Error(AppError.Validation("Lớp không được để trống."))
        if (trimmedClass.length > 15)
            return AppResult.Error(AppError.Validation("Lớp không được vượt quá 15 ký tự."))
        if (!trimmedEmail.endsWith(EMAIL_SUFFIX))
            return AppResult.Error(AppError.Validation("Email phải kết thúc bằng $EMAIL_SUFFIX."))
        if (teamId <= 0)
            return AppResult.Error(AppError.Validation("Vui lòng chọn đội hợp lệ cho tài khoản."))
        if (trimmedPhone.length < 10)
            return AppResult.Error(AppError.Validation("Số điện thoại phải có ít nhất 10 chữ số."))
        if (trimmedUsername.length < 3)
            return AppResult.Error(AppError.Validation("Tên đăng nhập phải có ít nhất 3 ký tự."))
        if (password.length < 6)
            return AppResult.Error(AppError.Validation("Mật khẩu phải có ít nhất 6 ký tự."))

        return repository.createAccount(
            fullname = trimmedFullname,
            mssv = trimmedMssv,
            className = trimmedClass,
            email = trimmedEmail,
            teamId = teamId,
            phoneNumber = trimmedPhone,
            username = trimmedUsername,
            password = password
        )
    }
}
