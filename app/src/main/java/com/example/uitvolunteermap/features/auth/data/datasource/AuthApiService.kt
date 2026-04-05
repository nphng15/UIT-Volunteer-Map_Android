package com.example.uitvolunteermap.features.auth.data.datasource

import com.example.uitvolunteermap.features.auth.data.model.*
import retrofit2.Response
import retrofit2.http.GET

interface AuthApiService {
    @GET("health")
    suspend fun getHealth(): Response<BaseResponseDto<HealthDataDto>>

    @GET("verify")
    suspend fun getVerify(): Response<BaseResponseDto<VerifyDataDto>>
}