package com.example.uitvolunteermap.features.admin.dashboard.domain.usecase

import com.example.uitvolunteermap.features.admin.dashboard.domain.repository.AdminDashboardRepository
import javax.inject.Inject

class GetAdminDashboardStatsUseCase @Inject constructor(
    private val repository: AdminDashboardRepository
) {
    suspend operator fun invoke() = repository.getDashboardStats()
}
