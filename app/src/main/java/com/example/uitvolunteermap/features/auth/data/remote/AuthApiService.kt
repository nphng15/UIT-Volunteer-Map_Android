package com.example.uitvolunteermap.features.auth.data.remote

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): ApiEnvelope<LoginResponseDto>

    @POST("auth/logout")
    suspend fun logout(): ApiEnvelope<Unit>

    @GET("verify")
    suspend fun verify(): ApiEnvelope<VerifyTokenDto>
}

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: LoginUserDto
)

data class LoginUserDto(
    @SerializedName("accId") val accId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String
)

data class VerifyTokenDto(
    @SerializedName("isExpired") val isExpired: Boolean
)
