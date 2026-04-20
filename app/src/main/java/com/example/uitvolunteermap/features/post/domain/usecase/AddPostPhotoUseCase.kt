package com.example.uitvolunteermap.features.post.domain.usecase

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.post.domain.entity.PostPhoto
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import javax.inject.Inject

class AddPostPhotoUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(postId: Int, photo: PostPhotoDraft): AppResult<PostPhoto> = when {
        postId <= 0 -> AppResult.Error(
            AppError.Validation("Post id khong hop le.")
        )

        photo.imageUrl.trim().isEmpty() -> AppResult.Error(
            AppError.Validation("Anh bai viet can co imageUrl.")
        )

        !photo.imageUrl.trim().startsWith("http://") &&
            !photo.imageUrl.trim().startsWith("https://") -> AppResult.Error(
            AppError.Validation("ImageUrl phai la mot duong dan http hoac https hop le.")
        )

        else -> repository.addPhoto(
            postId = postId,
            photo = photo.copy(
                title = photo.title?.trim()?.takeIf { it.isNotEmpty() },
                imageUrl = photo.imageUrl.trim()
            )
        )
    }
}
