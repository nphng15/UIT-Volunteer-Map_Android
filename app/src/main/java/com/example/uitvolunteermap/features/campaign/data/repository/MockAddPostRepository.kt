package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.campaign.domain.entity.AddPostDraft
import com.example.uitvolunteermap.features.campaign.domain.repository.AddPostRepository
import javax.inject.Inject
import kotlinx.coroutines.delay

class MockAddPostRepository @Inject constructor() : AddPostRepository {

    override suspend fun createPost(draft: AddPostDraft): AppResult<Unit> {
        delay(450)

        // TODO: Replace this mock flow with POST /posts when backend connection is ready.
        // TODO: When media upload is wired, map images to POST /posts photos or POST /posts/:id/photos.
        return AppResult.Success(Unit)
    }
}
