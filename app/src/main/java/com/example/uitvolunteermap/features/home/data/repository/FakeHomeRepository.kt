package com.example.uitvolunteermap.features.home.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.home.data.mapper.toDomain
import com.example.uitvolunteermap.features.home.data.model.HomeInfoDto
import com.example.uitvolunteermap.features.home.domain.entity.HomeInfo
import com.example.uitvolunteermap.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class FakeHomeRepository @Inject constructor() : HomeRepository {
    override suspend fun getHomeInfo(): AppResult<HomeInfo> {
        val fakeData = HomeInfoDto(
            title = "UIT Volunteer Map",
            subtitle = "Welcome to the home screen"
        )
        return AppResult.Success(fakeData.toDomain())
    }
}
