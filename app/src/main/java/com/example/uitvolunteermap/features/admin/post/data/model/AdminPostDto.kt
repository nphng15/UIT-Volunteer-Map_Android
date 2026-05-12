package com.example.uitvolunteermap.features.admin.post.data.model

import com.google.gson.annotations.SerializedName

/**
 * Item trả về từ `GET /posts` (public). Mỗi item là dạng rút gọn: chỉ có thumbnail
 * (1 ảnh đại diện) thay vì danh sách ảnh đầy đủ.
 */
data class AdminPostListItemDto(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("thumbnail") val thumbnail: AdminPostThumbnailDto?,
    @SerializedName("team") val team: AdminPostTeamDto?,
    @SerializedName("author") val author: AdminPostAuthorDto?
)

/**
 * Trả về từ `GET /posts/{id}`: bổ sung `isDeleted` và danh sách ảnh `photos` đầy đủ.
 * Hiện màn admin chủ yếu dùng danh sách, giữ DTO này để hoàn chỉnh hợp đồng API.
 */
data class AdminPostDetailDto(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("isDeleted") val isDeleted: Int?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("thumbnail") val thumbnail: AdminPostThumbnailDto?,
    @SerializedName("team") val team: AdminPostTeamDto?,
    @SerializedName("author") val author: AdminPostAuthorDto?,
    @SerializedName("photos") val photos: List<AdminPostPhotoDto>?
)

/**
 * Shape thô trả về từ POST/PUT/DELETE — đây là entity TypeORM raw, KHÁC với shape của GET
 * (không có team/author/thumbnail lồng nhau). Vì vậy sau khi mutate phải refetch danh sách,
 * không tin tưởng vào shape của response này ngoài việc xác nhận thao tác thành công.
 */
data class AdminPostMutationResponseDto(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("isDeleted") val isDeleted: Int?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class AdminPostThumbnailDto(
    @SerializedName("photoId") val photoId: Int,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("title") val title: String?
)

data class AdminPostTeamDto(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("teamName") val teamName: String
)

data class AdminPostAuthorDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("fullName") val fullName: String
)

data class AdminPostPhotoDto(
    @SerializedName("photoId") val photoId: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("uploadedAt") val uploadedAt: String,
    @SerializedName("isFirstImage") val isFirstImage: Int?,
    @SerializedName("isDeleted") val isDeleted: Int?
)

// ─── Request bodies ──────────────────────────────────────────────────────────

data class CreateAdminPostRequest(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("teamId") val teamId: Int,
    // authorId là userId của tác giả (KHÔNG phải accId)
    @SerializedName("authorId") val authorId: Int,
    @SerializedName("photos") val photos: List<CreateAdminPostPhotoRequest>?
)

data class CreateAdminPostPhotoRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("isFirstImage") val isFirstImage: Int?
)

/**
 * `PUT /posts/{id}` nhận partial: chỉ gửi field nào cần đổi. null = không cập nhật.
 * Không hỗ trợ cập nhật ảnh qua endpoint này.
 */
data class UpdateAdminPostRequest(
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("teamId") val teamId: Int? = null,
    @SerializedName("authorId") val authorId: Int? = null
)
