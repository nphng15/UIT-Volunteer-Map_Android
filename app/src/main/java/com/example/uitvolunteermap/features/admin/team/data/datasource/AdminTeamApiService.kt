package com.example.uitvolunteermap.features.admin.team.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.admin.team.data.model.AdminTeamDetailDto
import com.example.uitvolunteermap.features.admin.team.data.model.AdminTeamListItemDto
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Phiên bản dành cho khu quản trị (admin) của các endpoint /teams.
 *
 * Lưu ý hợp đồng API: POST/PUT trả về raw TypeORM entity với shape khác hẳn GET
 * (không khớp [AdminTeamDetailDto]). Vì vậy phản hồi mutate được parse thành [JsonElement]
 * "loose" — ta KHÔNG dựa vào shape đó mà refetch lại danh sách sau khi mutate thành công.
 */
interface AdminTeamApiService {

    @GET("teams")
    suspend fun getTeams(): ApiEnvelope<List<AdminTeamListItemDto>>

    @GET("teams/{id}")
    suspend fun getTeam(
        @Path("id") teamId: Int
    ): ApiEnvelope<AdminTeamDetailDto>

    @POST("teams")
    suspend fun createTeam(
        @Body body: CreateAdminTeamRequest
    ): ApiEnvelope<JsonElement>

    @PUT("teams/{id}")
    suspend fun updateTeam(
        @Path("id") teamId: Int,
        @Body body: UpdateAdminTeamRequest
    ): ApiEnvelope<JsonElement>

    @PATCH("teams/{id}/check-in-location")
    suspend fun updateTeamCheckInLocation(
        @Path("id") teamId: Int,
        @Body body: UpdateTeamCheckInLocationRequest
    ): ApiEnvelope<JsonElement>

    @POST("teams/{id}/attachments")
    suspend fun addTeamAttachments(
        @Path("id") teamId: Int,
        @Body body: AddAdminTeamAttachmentsRequest
    ): ApiEnvelope<JsonElement>

    @DELETE("teams/{id}")
    suspend fun deleteTeam(
        @Path("id") teamId: Int
    ): ApiEnvelope<JsonElement>
}

/**
 * POST /teams (ADMIN). teamName/leaderId/campaignId bắt buộc; description/imageUrl/attachments tùy chọn.
 * Không gửi key cho field null (để backend áp default) — Gson bỏ qua field null mặc định.
 */
data class CreateAdminTeamRequest(
    @SerializedName("teamName") val teamName: String,
    @SerializedName("leaderId") val leaderId: Int,
    @SerializedName("campaignId") val campaignId: Int,
    @SerializedName("description") val description: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("attachments") val attachments: List<AdminTeamAttachmentPayloadDto>? = null
)

/**
 * PUT /teams/{id} (ADMIN, hoặc LEADER của chính team đó).
 * CHỈ cho phép sửa teamName/description/imageUrl — KHÔNG đổi được leaderId hay campaignId.
 */
data class UpdateAdminTeamRequest(
    @SerializedName("teamName") val teamName: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null
)

data class UpdateTeamCheckInLocationRequest(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("radius") val radius: Double
)

/** POST /teams/{id}/attachments — thêm ảnh đính kèm cho team đã tồn tại. */
data class AddAdminTeamAttachmentsRequest(
    @SerializedName("attachments") val attachments: List<AdminTeamAttachmentPayloadDto>
)

data class AdminTeamAttachmentPayloadDto(
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("position") val position: Int? = null
)
