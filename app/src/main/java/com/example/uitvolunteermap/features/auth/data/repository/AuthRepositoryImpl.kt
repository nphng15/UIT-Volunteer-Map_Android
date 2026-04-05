package com.example.uitvolunteermap.features.auth.data.repository

import com.example.uitvolunteermap.features.auth.data.datasource.AuthApiService
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository {

    // Giả lập Server luôn hoạt động tốt
    override suspend fun isServerHealthy(): Boolean {
        return true
    }

    // Giả lập Token luôn hợp lệ (để nhảy vào Home)
    override suspend fun isTokenValid(): Boolean {
        return true
    }
}

/*class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository {

    override suspend fun isServerHealthy(): Boolean {
        return try {
            val response = apiService.getHealth()
            // Kiểm tra success = true và status = "healthy"
            response.isSuccessful && response.body()?.success == true &&
                    response.body()?.data?.status == "healthy"
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun isTokenValid(): Boolean {
        return try {
            val response = apiService.getVerify()
            // Backend luôn trả về 200, phải check success và isExpired
            response.isSuccessful &&
                    response.body()?.success == true &&
                    response.body()?.data?.isExpired == false
        } catch (e: Exception) {
            false
        }
    }
}*/