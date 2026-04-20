package com.example.uitvolunteermap.features.home.data.mapper

import com.example.uitvolunteermap.features.home.data.model.HomeInfoDto
import com.example.uitvolunteermap.features.home.domain.entity.HomeInfo

fun HomeInfoDto.toDomain(): HomeInfo {
    return HomeInfo(
        title = title,
        subtitle = subtitle
    )
}