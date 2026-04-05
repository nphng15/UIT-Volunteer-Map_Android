package com.example.uitvolunteermap.features.auth.domain.repository

interface AuthRepository {
    suspend fun isServerHealthy(): Boolean
    suspend fun isTokenValid(): Boolean
}