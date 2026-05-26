package com.example.uitvolunteermap.features.home.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.home.domain.model.VolunteerHomeContent

interface VolunteerHomeRepository {
    suspend fun getVolunteerHomeContent(): AppResult<VolunteerHomeContent>
}
