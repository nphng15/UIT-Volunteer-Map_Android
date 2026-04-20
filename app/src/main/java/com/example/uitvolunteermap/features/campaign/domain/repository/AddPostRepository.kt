package com.example.uitvolunteermap.features.campaign.domain.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.AddPostDraft

interface AddPostRepository {
    suspend fun createPost(draft: AddPostDraft): AppResult<Unit>
}
