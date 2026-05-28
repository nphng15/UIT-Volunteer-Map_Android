package com.example.uitvolunteermap.features.campaign.data.repository

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.AddPostDraft
import com.example.uitvolunteermap.features.campaign.domain.repository.AddPostRepository
import com.example.uitvolunteermap.features.post.domain.entity.CreatePostDraft
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import javax.inject.Inject

class RemoteAddPostRepository @Inject constructor(
    private val postRepository: PostRepository
) : AddPostRepository {

    override suspend fun createPost(draft: AddPostDraft): AppResult<Unit> {
        return when (
            val result = postRepository.createPost(
                CreatePostDraft(
                    teamId = draft.teamId,
                    authorId = draft.authorId,
                    title = draft.title,
                    content = draft.content,
                    photos = draft.attachmentNames.mapIndexed { index, imageUrl ->
                        PostPhotoDraft(
                            title = imageUrl.substringAfterLast('/').ifBlank { "Ảnh ${index + 1}" },
                            imageUrl = imageUrl,
                            isFirstImage = index == 0
                        )
                    }
                )
            )
        ) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Error -> result
        }
    }
}
