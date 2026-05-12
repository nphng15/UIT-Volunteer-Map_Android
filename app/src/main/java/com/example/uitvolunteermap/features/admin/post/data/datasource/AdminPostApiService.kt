package com.example.uitvolunteermap.features.admin.post.data.datasource

import com.example.uitvolunteermap.core.network.ApiEnvelope
import com.example.uitvolunteermap.features.admin.post.data.model.AdminPostDetailDto
import com.example.uitvolunteermap.features.admin.post.data.model.AdminPostListItemDto
import com.example.uitvolunteermap.features.admin.post.data.model.AdminPostMutationResponseDto
import com.example.uitvolunteermap.features.admin.post.data.model.CreateAdminPostRequest
import com.example.uitvolunteermap.features.admin.post.data.model.UpdateAdminPostRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminPostApiService {
    @GET("posts")
    suspend fun getPosts(): ApiEnvelope<List<AdminPostListItemDto>>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") postId: Int): ApiEnvelope<AdminPostDetailDto>

    @POST("posts")
    suspend fun createPost(
        @Body body: CreateAdminPostRequest
    ): ApiEnvelope<AdminPostMutationResponseDto>

    @PUT("posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: Int,
        @Body body: UpdateAdminPostRequest
    ): ApiEnvelope<AdminPostMutationResponseDto>

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Path("id") postId: Int
    ): ApiEnvelope<AdminPostMutationResponseDto>
}
