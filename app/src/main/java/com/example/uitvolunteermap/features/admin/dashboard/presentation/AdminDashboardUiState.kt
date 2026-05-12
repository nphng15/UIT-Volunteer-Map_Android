package com.example.uitvolunteermap.features.admin.dashboard.presentation

data class AdminDashboardUiState(
    val username: String = "Quản trị viên",
    val accountCount: Int? = null,
    val campaignCount: Int? = null,
    val teamCount: Int? = null,
    val postCount: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    /** True khi chưa có bất kỳ số liệu nào — dùng để quyết định hiển thị loading/error toàn màn. */
    val hasNoStats: Boolean
        get() = accountCount == null &&
            campaignCount == null &&
            teamCount == null &&
            postCount == null
}
