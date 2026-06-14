package com.example.uitvolunteermap.features.admin.dashboard.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.dashboard.domain.entity.AdminDashboardStats

interface AdminDashboardRepository {
    /**
     * Suy ra số liệu dashboard từ 4 endpoint danh sách. Luôn trả [AppResult.Success]
     * với phần đếm được; các count thất bại để null. Chỉ trả [AppResult.Error] khi
     * toàn bộ 4 lời gọi đều thất bại (không có gì để hiển thị).
     */
    suspend fun getDashboardStats(): AppResult<AdminDashboardStats>
}
