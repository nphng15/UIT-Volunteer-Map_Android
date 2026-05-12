package com.example.uitvolunteermap.features.home.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.home.domain.entity.HomeInfo

interface HomeRepository {
    suspend fun getHomeInfo(): AppResult<HomeInfo>
}
