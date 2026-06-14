package com.example.uitvolunteermap.features.admin.account.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.admin.account.data.model.AccountDto
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AccountApiService {

    /**
     * GET /accounts (ADMIN). Backend trả về MẢNG tài khoản trần (không bọc trong
     * ApiEnvelope như các endpoint khác), nên khai báo trực tiếp List<AccountDto>.
     */
    @GET("accounts")
    suspend fun getAccounts(): List<AccountDto>

    /** POST /accounts (ADMIN) — backend chỉ trả `{ success, message }`, không có `data`. */
    @POST("accounts")
    suspend fun createAccount(
        @Body body: CreateAccountRequest
    ): ApiEnvelope<Unit>

    /** PUT /accounts/{id} (ADMIN) — trả về tài khoản đã cập nhật trong `data`. */
    @PUT("accounts/{id}")
    suspend fun updateAccount(
        @Path("id") accountId: Int,
        @Body body: UpdateAccountRequest
    ): ApiEnvelope<AccountDto>

    /** DELETE /accounts/{id} (ADMIN) — soft delete, chỉ trả `{ success, message }`. */
    @DELETE("accounts/{id}")
    suspend fun deleteAccount(
        @Path("id") accountId: Int
    ): ApiEnvelope<Unit>
}

data class CreateAccountRequest(
    @SerializedName("fullname") val fullname: String,
    @SerializedName("mssv") val mssv: String,
    // `class` là từ khoá Kotlin → đặt tên thuộc tính khác nhưng giữ nguyên field JSON.
    @SerializedName("class") val className: String,
    @SerializedName("email") val email: String,
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

/**
 * PUT body chỉ cho phép `password` và `roleId`. Lưu ý: backend đặt tên field là
 * `roleId` nhưng thực chất nhận TÊN vai trò ("admin" | "leader" | "volunteer").
 */
data class UpdateAccountRequest(
    @SerializedName("password") val password: String? = null,
    @SerializedName("roleId") val roleId: String? = null
)
