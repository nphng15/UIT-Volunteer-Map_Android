package com.example.uitvolunteermap.features.post.domain.entity

data class CreatePostDraft(
    val teamId: Int,
    val authorId: Int,
    val title: String,
    val content: String,
    val photos: List<PostPhotoDraft> = emptyList()
)

data class UpdatePostDraft(
    val title: String? = null,
    val content: String? = null,
    val teamId: Int? = null,
    val authorId: Int? = null
) {
    fun hasChanges(): Boolean {
        return title != null || content != null || teamId != null || authorId != null
    }
}

data class PostPhotoDraft(
    val title: String? = null,
    val imageUrl: String,
    val isFirstImage: Boolean = false
)
