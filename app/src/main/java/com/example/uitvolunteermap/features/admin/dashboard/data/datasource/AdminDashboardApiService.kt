package com.example.uitvolunteermap.features.admin.dashboard.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

/**
 * Backend KHÔNG có endpoint thống kê tổng hợp cho dashboard. Vì vậy ta tự suy ra
 * các con số bằng cách gọi 4 endpoint danh sách hiện có rồi đếm phần tử.
 *
 * Mỗi response chỉ cần biết số lượng, nên dùng DTO tối giản chỉ chứa id để Gson
 * parse gọn, tránh phụ thuộc shape đầy đủ của từng entity (giúp bền với thay đổi API).
 */
interface AdminDashboardApiService {

    // GET /accounts trả MẢNG trần (không bọc ApiEnvelope) → khai báo trực tiếp.
    @GET("accounts")
    suspend fun getAccounts(): List<AccountIdDto>

    @GET("campaigns")
    suspend fun getCampaigns(): ApiEnvelope<List<CampaignIdDto>>

    @GET("teams")
    suspend fun getTeams(): ApiEnvelope<List<TeamIdDto>>

    @GET("posts")
    suspend fun getPosts(): ApiEnvelope<List<PostIdDto>>
}

data class AccountIdDto(
    @SerializedName("userId") val userId: Int? = null
)

data class CampaignIdDto(
    @SerializedName("campaignId") val campaignId: Int? = null
)

data class TeamIdDto(
    @SerializedName("teamId") val teamId: Int? = null
)

data class PostIdDto(
    @SerializedName("postId") val postId: Int? = null
)
