package com.example.uitvolunteermap.features.post.data.remote

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PostApiService {
    @GET("posts")
    suspend fun getPosts(): ApiEnvelope<List<PostListItemDto>>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") postId: Int): ApiEnvelope<RawPostDto>

    @POST("posts")
    suspend fun createPost(@Body body: CreatePostRequest): ApiEnvelope<PostMutationResponseDto>

    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: Int,
        @Body body: UpdatePostRequest
    ): ApiEnvelope<RawPostDto>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") postId: Int): ApiEnvelope<RawPostDto>

    @POST("posts/{id}/photos")
    suspend fun addPhoto(
        @Path("id") postId: Int,
        @Body body: AddPostPhotoRequest
    ): ApiEnvelope<PostPhotoDto>
}

data class PostListItemDto(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("thumbnail") val thumbnail: PostThumbnailDto?,
    @SerializedName("team") val team: PostTeamDto?,
    @SerializedName("author") val author: PostAuthorDto?
)

data class RawPostDto(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("isDeleted") val isDeleted: Int?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class PostMutationResponseDto(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("isDeleted") val isDeleted: Int?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("photos") val photos: List<PostPhotoDto>?
)

data class PostThumbnailDto(
    @SerializedName("photoId") val photoId: Int,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("title") val title: String?
)

data class PostTeamDto(
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("teamName") val teamName: String
)

data class PostAuthorDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("fullName") val fullName: String
)

data class PostPhotoDto(
    @SerializedName("photoId") val photoId: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("uploadedAt") val uploadedAt: String,
    @SerializedName("isFirstImage") val isFirstImage: Int?,
    @SerializedName("isDeleted") val isDeleted: Int?
)

data class CreatePostRequest(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("teamId") val teamId: Int,
    @SerializedName("authorId") val authorId: Int,
    @SerializedName("photos") val photos: List<CreatePostPhotoRequest>
)

data class CreatePostPhotoRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("isFirstImage") val isFirstImage: Int
)

data class UpdatePostRequest(
    @SerializedName("title") val title: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("teamId") val teamId: Int? = null,
    @SerializedName("authorId") val authorId: Int? = null
)

data class AddPostPhotoRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("isFirstImage") val isFirstImage: Int
)
