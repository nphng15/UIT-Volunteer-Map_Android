package com.example.uitvolunteermap.testing

import com.example.uitvolunteermap.core.common.di.IoDispatcher
import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.auth.domain.entity.AuthUser
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import com.example.uitvolunteermap.features.campaign.data.repository.MockAddPostRepository
import com.example.uitvolunteermap.features.campaign.data.repository.MockCampaignDetailRepository
import com.example.uitvolunteermap.features.campaign.data.repository.MockTeamFormationDetailRepository
import com.example.uitvolunteermap.features.campaign.domain.entity.AddPostDraft
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.repository.AddPostRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignDetailRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.TeamFormationDetailRepository
import com.example.uitvolunteermap.features.home.data.repository.MockVolunteerHomeRepository
import com.example.uitvolunteermap.features.home.domain.model.VolunteerHomeContent
import com.example.uitvolunteermap.features.home.domain.repository.VolunteerHomeRepository
import com.example.uitvolunteermap.features.post.data.repository.InMemoryPostRepository
import com.example.uitvolunteermap.features.post.domain.entity.CreatePostDraft
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.PostPhoto
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import com.example.uitvolunteermap.features.post.domain.entity.UpdatePostDraft
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

object VolunteerFlowTestDefaults {
    const val VolunteerEmail = "volunteer@uit.edu.vn"
    const val VolunteerPassword = "volunteer123"
}

class TestAuthRepository @Inject constructor() : AuthRepository {

    data class LoginRequest(val email: String, val password: String)

    private var forcedResult: AppResult<AuthUser>? = null

    var lastRequest: LoginRequest? = null
        private set

    override suspend fun login(email: String, password: String): AppResult<AuthUser> {
        lastRequest = LoginRequest(email = email, password = password)
        return forcedResult ?: if (
            email.equals(VolunteerFlowTestDefaults.VolunteerEmail, ignoreCase = true) &&
            password == VolunteerFlowTestDefaults.VolunteerPassword
        ) {
            AppResult.Success(
                AuthUser(
                    id = "test-volunteer",
                    email = VolunteerFlowTestDefaults.VolunteerEmail,
                    displayName = "UIT Volunteer",
                )
            )
        } else {
            AppResult.Error(
                AppError.Unauthorized(message = "Email hoac mat khau khong dung.")
            )
        }
    }

    fun succeed(user: AuthUser = defaultUser()): TestAuthRepository = apply {
        forcedResult = AppResult.Success(user)
    }

    fun fail(message: String = "Email hoac mat khau khong dung."): TestAuthRepository = apply {
        forcedResult = AppResult.Error(
            AppError.Unauthorized(message = message)
        )
    }

    fun reset() {
        forcedResult = null
        lastRequest = null
    }

    private fun defaultUser(): AuthUser = AuthUser(
        id = "test-volunteer",
        email = VolunteerFlowTestDefaults.VolunteerEmail,
        displayName = "UIT Volunteer",
    )
}

class TestVolunteerHomeRepository @Inject constructor() : VolunteerHomeRepository {
    private val delegate = MockVolunteerHomeRepository()
    private var forcedResult: AppResult<VolunteerHomeContent>? = null

    override suspend fun getVolunteerHomeContent(): AppResult<VolunteerHomeContent> {
        return forcedResult ?: delegate.getVolunteerHomeContent()
    }

    fun enqueue(result: AppResult<VolunteerHomeContent>): TestVolunteerHomeRepository = apply {
        forcedResult = result
    }

    fun reset() {
        forcedResult = null
    }
}

class TestCampaignDetailRepository @Inject constructor() : CampaignDetailRepository {
    private val delegate = MockCampaignDetailRepository()
    private var forcedResult: AppResult<CampaignDetail>? = null

    override suspend fun getCampaignDetail(campaignId: Int): AppResult<CampaignDetail> {
        return forcedResult ?: delegate.getCampaignDetail(campaignId)
    }

    fun enqueue(result: AppResult<CampaignDetail>): TestCampaignDetailRepository = apply {
        forcedResult = result
    }

    fun reset() {
        forcedResult = null
    }
}

class TestTeamFormationDetailRepository @Inject constructor() : TeamFormationDetailRepository {
    private val delegate = MockTeamFormationDetailRepository()
    private var forcedResult: AppResult<TeamFormationDetail>? = null

    override suspend fun getTeamFormationDetail(teamId: Int): AppResult<TeamFormationDetail> {
        return forcedResult ?: delegate.getTeamFormationDetail(teamId)
    }

    fun enqueue(result: AppResult<TeamFormationDetail>): TestTeamFormationDetailRepository = apply {
        forcedResult = result
    }

    fun reset() {
        forcedResult = null
    }
}

class TestAddPostRepository @Inject constructor() : AddPostRepository {
    private val delegate = MockAddPostRepository()
    private var forcedResult: AppResult<Unit>? = null

    override suspend fun createPost(draft: AddPostDraft): AppResult<Unit> {
        return forcedResult ?: delegate.createPost(draft)
    }

    fun enqueue(result: AppResult<Unit>): TestAddPostRepository = apply {
        forcedResult = result
    }

    fun reset() {
        forcedResult = null
    }
}

class TestPostRepository @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PostRepository {
    private var delegate = InMemoryPostRepository(ioDispatcher)
    private var forcedPostsResult: AppResult<List<Post>>? = null
    private var forcedPostResult: AppResult<Post>? = null

    override suspend fun getPosts(): AppResult<List<Post>> {
        return forcedPostsResult ?: delegate.getPosts()
    }

    override suspend fun getPost(postId: Int): AppResult<Post> {
        return forcedPostResult ?: delegate.getPost(postId)
    }

    override suspend fun createPost(draft: CreatePostDraft): AppResult<Post> {
        return delegate.createPost(draft)
    }

    override suspend fun updatePost(postId: Int, draft: UpdatePostDraft): AppResult<Post> {
        return delegate.updatePost(postId, draft)
    }

    override suspend fun deletePost(postId: Int): AppResult<Post> {
        return delegate.deletePost(postId)
    }

    override suspend fun addPhoto(postId: Int, photo: PostPhotoDraft): AppResult<PostPhoto> {
        return delegate.addPhoto(postId, photo)
    }

    fun enqueuePosts(result: AppResult<List<Post>>): TestPostRepository = apply {
        forcedPostsResult = result
    }

    fun enqueuePost(result: AppResult<Post>): TestPostRepository = apply {
        forcedPostResult = result
    }

    fun reset() {
        delegate = InMemoryPostRepository(ioDispatcher)
        forcedPostsResult = null
        forcedPostResult = null
    }
}
