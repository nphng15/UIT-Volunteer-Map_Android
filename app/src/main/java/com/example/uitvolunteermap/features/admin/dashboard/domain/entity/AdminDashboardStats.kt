package com.example.uitvolunteermap.features.admin.dashboard.domain.entity

/**
 * Số liệu tổng hợp hiển thị trên dashboard quản trị.
 *
 * Mỗi count là nullable vì backend không có endpoint thống kê: ta suy ra bằng cách
 * đếm danh sách từ 4 endpoint riêng. Nếu một lời gọi thất bại, count tương ứng giữ
 * null để UI hiển thị "—" thay vì 0 (phân biệt "không tải được" với "thật sự bằng 0").
 */
data class AdminDashboardStats(
    val accountCount: Int? = null,
    val campaignCount: Int? = null,
    val teamCount: Int? = null,
    val postCount: Int? = null
)
