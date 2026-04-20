package com.example.uitvolunteermap.features.post.domain.usecase

import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.PostPhoto
import com.example.uitvolunteermap.features.post.domain.entity.PostUiModel
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import javax.inject.Inject

class GetPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(): AppResult<List<PostUiModel>> {
        return when (val result = repository.getPosts()) {
            is AppResult.Success -> AppResult.Success(result.data.map(Post::toUiModel))
            is AppResult.Error -> result
        }
    }
}

private fun Post.toUiModel(): PostUiModel {
    val thumbnail = resolveThumbnail()
    return PostUiModel(
        id = id,
        teamId = teamId,
        teamName = teamName,
        title = title,
        excerpt = content.toExcerpt(),
        content = content.trim(),
        publishedAt = createdAt.toPostDateLabel(),
        updatedAt = updatedAt.toPostDateLabel(),
        authorName = authorName,
        subtitle = listOf(teamName, authorName)
            .filter { it.isNotBlank() }
            .joinToString(separator = " - "),
        thumbnailUrl = thumbnail?.imageUrl,
        attachmentLabels = photos
            .filterNot { it.isDeleted }
            .mapIndexed { index, photo ->
                photo.title?.takeIf { it.isNotBlank() } ?: "Ảnh ${index + 1}"
            }
    )
}

private fun Post.resolveThumbnail(): PostPhoto? {
    val activePhotos = photos.filterNot { it.isDeleted }
    return activePhotos.firstOrNull { it.isFirstImage } ?: activePhotos.firstOrNull()
}

private fun String.toExcerpt(maxLength: Int = 96): String {
    val collapsed = trim().replace(Regex("\\s+"), " ")
    if (collapsed.length <= maxLength) {
        return collapsed
    }
    return collapsed.take(maxLength - 3).trimEnd() + "..."
}

private fun String.toPostDateLabel(): String {
    return if (length >= 16 && getOrNull(4) == '-' && getOrNull(7) == '-') {
        "${substring(8, 10)}/${substring(5, 7)}/${substring(0, 4)} ${substring(11, 16)}"
    } else {
        this
    }
}
