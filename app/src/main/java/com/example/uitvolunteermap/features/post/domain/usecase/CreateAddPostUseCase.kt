package com.example.uitvolunteermap.features.post.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.AddPostDraft
import com.example.uitvolunteermap.features.post.domain.entity.CreatePostDraft
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import java.util.Locale
import javax.inject.Inject

class CreateAddPostUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(draft: AddPostDraft): AppResult<Post> = when {
        draft.teamId <= 0 -> AppResult.Error(
            AppError.Validation("Team id khong hop le.")
        )

        draft.authorId <= 0 -> AppResult.Error(
            AppError.Validation("Author id khong hop le.")
        )

        draft.title.isBlank() -> AppResult.Error(
            AppError.Validation("Tieu de bai viet khong duoc de trong.")
        )

        draft.content.isBlank() -> AppResult.Error(
            AppError.Validation("Noi dung mo ta khong duoc de trong.")
        )

        draft.attachmentNames.any { it.isBlank() } -> AppResult.Error(
            AppError.Validation("Ten anh dinh kem khong hop le.")
        )

        draft.attachmentNames.size > 5 -> AppResult.Error(
            AppError.Validation("Popup nay chi ho tro toi da 5 anh dinh kem.")
        )

        else -> repository.createPost(
            CreatePostDraft(
                teamId = draft.teamId,
                authorId = draft.authorId,
                title = draft.title.trim(),
                content = draft.content.trim(),
                photos = draft.attachmentNames.mapIndexed { index, attachmentName ->
                    PostPhotoDraft(
                        title = attachmentName.toPhotoTitleOrNull(index),
                        imageUrl = attachmentName.toMockImageUrl(
                            teamId = draft.teamId,
                            index = index
                        ),
                        isFirstImage = index == 0
                    )
                }
            )
        )
    }
}

private fun String.toPhotoTitleOrNull(index: Int): String? {
    val rawValue = trim()
    val normalizedSource = if (rawValue.startsWith("http://") || rawValue.startsWith("https://")) {
        rawValue.substringAfterLast('/')
            .substringBefore('?')
            .substringBefore('#')
    } else {
        rawValue
    }

    val normalized = normalizedSource.substringBeforeLast('.')
        .replace('_', ' ')
        .replace('-', ' ')
        .trim()
        .replace(Regex("\\s+"), " ")

    if (normalized.isBlank()) {
        return if (rawValue.startsWith("http://") || rawValue.startsWith("https://")) {
            null
        } else {
            "Anh ${index + 1}"
        }
    }

    return normalized.split(" ")
        .filter { it.isNotBlank() }
        .joinToString(separator = " ") { word ->
            word.lowercase(Locale.US).replaceFirstChar { character ->
                character.titlecase(Locale.US)
            }
        }
}

private fun String.toMockImageUrl(teamId: Int, index: Int): String {
    val rawValue = trim()
    if (rawValue.startsWith("http://") || rawValue.startsWith("https://")) {
        return rawValue
    }

    val slug = buildString {
        rawValue.lowercase(Locale.US).forEach { character ->
            append(
                when {
                    character.isLetterOrDigit() -> character
                    else -> '-'
                }
            )
        }
    }.replace(Regex("-+"), "-").trim('-')

    val safeSlug = if (slug.isBlank()) "post-photo-${index + 1}" else slug
    return "https://example.com/mock/posts/team-$teamId/${index + 1}-$safeSlug.jpg"
}
