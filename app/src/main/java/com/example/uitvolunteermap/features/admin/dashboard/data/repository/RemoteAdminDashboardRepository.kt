package com.example.uitvolunteermap.features.admin.dashboard.data.repository

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.admin.dashboard.data.datasource.AdminDashboardApiService
import com.example.uitvolunteermap.features.admin.dashboard.domain.entity.AdminDashboardStats
import com.example.uitvolunteermap.features.admin.dashboard.domain.repository.AdminDashboardRepository
import com.example.uitvolunteermap.core.network.ApiEnvelope
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Suy ra số liệu dashboard từ 4 endpoint danh sách (không có endpoint thống kê riêng).
 *
 * - Chạy 4 lời gọi song song để giảm độ trễ.
 * - Mỗi count được lấy độc lập: lời gọi nào lỗi (vd: 403 với non-admin, lỗi mạng tạm thời)
 *   thì count đó để null, các count còn lại vẫn hiển thị.
 * - Chỉ trả [AppResult.Error] khi CẢ 4 đều thất bại — khi đó không có gì để hiển thị.
 */
class RemoteAdminDashboardRepository @Inject constructor(
    private val api: AdminDashboardApiService
) : AdminDashboardRepository {

    override suspend fun getDashboardStats(): AppResult<AdminDashboardStats> = coroutineScope {
        val accountsDeferred = async { safeCountRaw { api.getAccounts() } }
        val campaignsDeferred = async { safeCount { api.getCampaigns() } }
        val teamsDeferred = async { safeCount { api.getTeams() } }
        val postsDeferred = async { safeCount { api.getPosts() } }

        val accountCount = accountsDeferred.await()
        val campaignCount = campaignsDeferred.await()
        val teamCount = teamsDeferred.await()
        val postCount = postsDeferred.await()

        val allFailed = accountCount == null &&
            campaignCount == null &&
            teamCount == null &&
            postCount == null

        if (allFailed) {
            AppResult.Error(AppError.Network())
        } else {
            AppResult.Success(
                AdminDashboardStats(
                    accountCount = accountCount,
                    campaignCount = campaignCount,
                    teamCount = teamCount,
                    postCount = postCount
                )
            )
        }
    }

    /**
     * Trả số phần tử nếu envelope thành công, ngược lại null. Nuốt mọi exception để
     * một endpoint hỏng không làm sập toàn bộ dashboard.
     */
    private inline fun <T> safeCount(request: () -> ApiEnvelope<List<T>>): Int? = try {
        val response = request()
        if (response.success) response.data?.size else null
    } catch (throwable: Throwable) {
        null
    }

    /** Như [safeCount] nhưng cho endpoint trả MẢNG trần (vd GET /accounts). */
    private inline fun <T> safeCountRaw(request: () -> List<T>): Int? = try {
        request().size
    } catch (throwable: Throwable) {
        null
    }
}
