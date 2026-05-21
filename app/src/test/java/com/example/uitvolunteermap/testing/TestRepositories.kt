package com.example.uitvolunteermap.testing

import com.example.uitvolunteermap.core.common.error.AppError
import com.example.uitvolunteermap.core.common.result.AppResult
import com.example.uitvolunteermap.features.auth.domain.entity.AuthUser
import com.example.uitvolunteermap.features.auth.domain.repository.AuthRepository
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetailPost
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetailStat
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignDetailTeam
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignMapLocation
import com.example.uitvolunteermap.features.campaign.domain.entity.CampaignMapOverview
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamActivityItem
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamFormationDetail
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamHeroCard
import com.example.uitvolunteermap.features.campaign.domain.entity.TeamLeader
import com.example.uitvolunteermap.features.campaign.domain.repository.CampaignDetailRepository
import com.example.uitvolunteermap.features.campaign.domain.repository.TeamFormationDetailRepository
import com.example.uitvolunteermap.features.home.domain.model.VolunteerCampaignSummary
import com.example.uitvolunteermap.features.home.domain.model.VolunteerHomeContent
import com.example.uitvolunteermap.features.home.domain.model.VolunteerOverviewStat
import com.example.uitvolunteermap.features.home.domain.repository.VolunteerHomeRepository
import com.example.uitvolunteermap.features.post.domain.entity.CreatePostDraft
import com.example.uitvolunteermap.features.post.domain.entity.Post
import com.example.uitvolunteermap.features.post.domain.entity.PostPhoto
import com.example.uitvolunteermap.features.post.domain.entity.PostPhotoDraft
import com.example.uitvolunteermap.features.post.domain.entity.UpdatePostDraft
import com.example.uitvolunteermap.features.post.domain.repository.PostRepository

class FakeAuthRepository : AuthRepository {
    var result: AppResult<AuthUser> = AppResult.Success(
        AuthUser(
            id = "user_uit_volunteer",
            email = "volunteer@uit.edu.vn",
            displayName = "UIT Volunteer",
        )
    )

    var lastEmail: String? = null
        private set

    var lastPassword: String? = null
        private set

    override suspend fun login(email: String, password: String): AppResult<AuthUser> {
        lastEmail = email
        lastPassword = password
        return result
    }
}

class FakeVolunteerHomeRepository : VolunteerHomeRepository {
    var result: AppResult<VolunteerHomeContent> = AppResult.Success(defaultVolunteerHomeContent())

    override suspend fun getVolunteerHomeContent(): AppResult<VolunteerHomeContent> = result
}

class FakeCampaignDetailRepository : CampaignDetailRepository {
    var result: AppResult<CampaignDetail> = AppResult.Success(defaultCampaignDetail())

    override suspend fun getCampaignDetail(campaignId: Int): AppResult<CampaignDetail> = result
}

class FakeTeamFormationDetailRepository : TeamFormationDetailRepository {
    var result: AppResult<TeamFormationDetail> = AppResult.Success(defaultTeamFormationDetail())

    override suspend fun getTeamFormationDetail(teamId: Int): AppResult<TeamFormationDetail> = result
}

class FakePostRepository : PostRepository {
    var postsResult: AppResult<List<Post>> = AppResult.Success(listOf(defaultPost()))
    var postResult: AppResult<Post> = AppResult.Success(defaultPost())
    var createResult: AppResult<Post> = AppResult.Success(defaultPost())
    var updateResult: AppResult<Post> = AppResult.Success(defaultPost())
    var deleteResult: AppResult<Post> = AppResult.Success(defaultPost())
    var addPhotoResult: AppResult<PostPhoto> = AppResult.Success(defaultPhoto())

    val createdDrafts = mutableListOf<CreatePostDraft>()
    val updatedDrafts = mutableListOf<Pair<Int, UpdatePostDraft>>()
    val deletedPostIds = mutableListOf<Int>()

    override suspend fun getPosts(): AppResult<List<Post>> = postsResult

    override suspend fun getPost(postId: Int): AppResult<Post> = postResult

    override suspend fun createPost(draft: CreatePostDraft): AppResult<Post> {
        createdDrafts += draft
        return createResult
    }

    override suspend fun updatePost(postId: Int, draft: UpdatePostDraft): AppResult<Post> {
        updatedDrafts += postId to draft
        return updateResult
    }

    override suspend fun deletePost(postId: Int): AppResult<Post> {
        deletedPostIds += postId
        return deleteResult
    }

    override suspend fun addPhoto(postId: Int, photo: PostPhotoDraft): AppResult<PostPhoto> = addPhotoResult
}

fun defaultVolunteerHomeContent(): VolunteerHomeContent = VolunteerHomeContent(
    appName = "UIT · Tình nguyện",
    stats = listOf(
        VolunteerOverviewStat(value = "06", label = "Đang diễn ra"),
        VolunteerOverviewStat(value = "03", label = "Sắp mở"),
        VolunteerOverviewStat(value = "12", label = "Địa điểm"),
    ),
    campaigns = listOf(
        VolunteerCampaignSummary(
            id = 1,
            title = "Mùa Hè Xanh 2026",
            dateRange = "10/06 - 28/07",
            description = "Chiến dịch hè quy mô lớn.",
            meta = "12 đội - 48 bài viết",
            primaryActionLabel = "Xem chiến dịch",
            secondaryActionLabel = "Bản đồ",
            accentColors = listOf(0xFFF7F1D8, 0xFFFFF4CC),
        )
    )
)

fun defaultCampaignDetail(): CampaignDetail = CampaignDetail(
    id = 1,
    appName = "UIT · Tình nguyện",
    title = "Mùa Hè Xanh 2026",
    schedule = "10/06/2026 - 28/07/2026",
    heroHeadline = "Đồng bộ đội hình, bài viết và địa điểm",
    heroSupportingText = "Theo dõi các đội hình giáo dục và hậu cần.",
    stats = listOf(
        CampaignDetailStat(value = "12", label = "Đội hình"),
        CampaignDetailStat(value = "48", label = "Bài viết"),
    ),
    description = "Mô tả chiến dịch.",
    teamSectionTitle = "Đội hình",
    teams = listOf(
        CampaignDetailTeam(
            id = 101,
            name = "Đội nấu cơm",
            shortName = "NC",
            accentColors = listOf(0xFF20303A, 0xFF6D839A),
        )
    ),
    posts = listOf(
        CampaignDetailPost(
            id = 201,
            teamName = "Đội nấu cơm",
            title = "Cập nhật ngày đầu tiên",
            publishedAt = "Hôm nay 08:30",
            summary = "Tóm tắt hoạt động.",
            accentColors = listOf(0xFF20303A, 0xFF1C3977),
            isLightBadge = true,
        )
    ),
    mapOverview = CampaignMapOverview(
        selectedArea = "Thủ Đức",
        headerTitle = "Khu vực hoạt động",
        footerTitle = "3 điểm gần nhau",
        footerDescription = "Chạm marker để mở Maps",
        ctaLabel = "Mở Google Maps",
        locations = listOf(
            CampaignMapLocation(
                id = 1,
                label = "Linh Trung",
                supportingText = "12 bài viết mới",
                xFraction = 0.22f,
                yFraction = 0.42f,
                isHighlighted = true,
            )
        )
    )
)

fun defaultTeamFormationDetail(): TeamFormationDetail = TeamFormationDetail(
    id = 101,
    appName = "UIT · Tình nguyện",
    appSubtitle = "Trang tổng hợp chiến dịch và điểm đến tình nguyện",
    title = "Đội nấu cơm",
    description = "Mô tả đội hình.",
    heroCards = listOf(
        TeamHeroCard(label = "Ảnh", isPrimary = false),
        TeamHeroCard(label = "Ảnh chính", isPrimary = true),
    ),
    leaders = listOf(
        TeamLeader(id = 1, initials = "LT", role = "Chỉ huy", name = "Lê Thanh"),
    ),
    activities = listOf(
        TeamActivityItem(id = 0, label = "+", isAddButton = true),
        TeamActivityItem(id = 1, label = "Ảnh 1", isAddButton = false),
    )
)

fun defaultPost(): Post = Post(
    id = 201,
    title = "Cập nhật chiến dịch",
    content = "Tổng hợp nhanh tình hình hiện trường.",
    teamId = 101,
    teamName = "Đội nấu cơm",
    authorId = 20,
    authorName = "Trần Thị B",
    createdAt = "2026-03-27T09:00:00.000Z",
    updatedAt = "2026-03-27T09:00:00.000Z",
    isDeleted = false,
    photos = listOf(defaultPhoto())
)

fun defaultPhoto(): PostPhoto = PostPhoto(
    id = 301,
    title = "Ảnh bìa",
    imageUrl = "https://example.com/mock/posts/media-cover.jpg",
    uploadedAt = "2026-03-27T09:00:00.000Z",
    isFirstImage = true,
    isDeleted = false
)

fun errorResult(message: String): AppResult.Error = AppResult.Error(AppError.Unknown(message))
