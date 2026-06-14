package com.example.uitvolunteermap.features.admin.account.presentation

/**
 * Vai trò hiển thị/chọn trong UI. Giá trị `apiValue` là tên gửi lên backend
 * (field `roleId` thực chất nhận tên này).
 */
enum class AccountRole(val apiValue: String, val label: String) {
    ADMIN("admin", "Quản trị viên"),
    LEADER("leader", "Nhóm trưởng"),
    VOLUNTEER("volunteer", "Tình nguyện viên");

    companion object {
        /** Map roleName tự do từ backend về enum, không phân biệt hoa thường. */
        fun fromApi(raw: String?): AccountRole? =
            entries.firstOrNull { it.apiValue.equals(raw?.trim(), ignoreCase = true) }
    }
}

data class AccountListItemUiModel(
    val accId: Int,
    val username: String,
    val roleName: String,
    val role: AccountRole?,
    val joinedAt: String
)

/** Form tạo tài khoản mới — giữ toàn bộ input + lỗi inline. */
data class CreateAccountForm(
    val fullname: String = "",
    val mssv: String = "",
    val className: String = "",
    val email: String = "",
    val teamId: String = "",
    val phoneNumber: String = "",
    val username: String = "",
    val password: String = "",
    val role: AccountRole = AccountRole.VOLUNTEER,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

/** Form chỉnh sửa — backend chỉ cho đổi mật khẩu và vai trò. */
data class EditAccountForm(
    val accId: Int,
    val username: String,
    val password: String = "",
    val role: AccountRole = AccountRole.VOLUNTEER,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

data class AdminAccountUiState(
    val accounts: List<AccountListItemUiModel> = emptyList(),
    val searchQuery: String = "",
    val roleFilter: AccountRole? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val createForm: CreateAccountForm? = null,
    val editForm: EditAccountForm? = null,
    val pendingDeleteId: Int? = null,
    val isDeleting: Boolean = false,
    val canManageAccounts: Boolean = false
) {
    /** Danh sách sau khi áp dụng tìm kiếm + lọc vai trò. */
    val filteredAccounts: List<AccountListItemUiModel>
        get() = accounts.filter { account ->
            val matchesQuery = searchQuery.isBlank() ||
                account.username.contains(searchQuery.trim(), ignoreCase = true)
            val matchesRole = roleFilter == null || account.role == roleFilter
            matchesQuery && matchesRole
        }

    val pendingDeleteUsername: String?
        get() = accounts.firstOrNull { it.accId == pendingDeleteId }?.username
}
