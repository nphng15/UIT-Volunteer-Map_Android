package com.example.uitvolunteermap.features.post.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.VolunteerBottomBar
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.CampaignPostCard
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsErrorState
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenAccent
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenBottom
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenPrimary
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenSecondary
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenSurface
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenTop

@Composable
fun FeedScreen(
    state: FeedUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (FeedUiEvent) -> Unit,
    onTabSelected: (VolunteerBottomBarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(VolunteerFlowTestTags.FeedScreen),
        containerColor = PostsScreenSurface,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            VolunteerBottomBar(
                currentTab = VolunteerBottomBarTab.Feed,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PostsScreenTop, PostsScreenBottom)
                    )
                )
        ) {
            when {
                state.isLoading && state.posts.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PostsScreenAccent
                    )
                }

                state.errorMessage != null && state.posts.isEmpty() -> {
                    PostsErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(FeedUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing14)
                    ) {
                        item {
                            FeedHeader()
                        }

                        if (state.posts.isEmpty()) {
                            item {
                                EmptyFeedState()
                            }
                        } else {
                            items(state.posts, key = { it.id }) { post ->
                                CampaignPostCard(
                                    post = post,
                                    isExpanded = state.expandedPostId == post.id,
                                    canManagePost = false,
                                    onClick = {
                                        onEvent(FeedUiEvent.PostCardClicked(post.id))
                                    },
                                    onEditClick = {},
                                    onDeleteClick = {}
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(Dimens.Spacing8))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Spacing20, vertical = Dimens.Spacing14),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6)
    ) {
        Text(
            text = "BẢNG TIN TÌNH NGUYỆN",
            color = PostsScreenSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Hoạt động mới nhất",
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun EmptyFeedState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Spacing24, vertical = Dimens.Spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
    ) {
        Text(
            text = "Chưa có bài viết nào",
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Các hoạt động của chiến dịch sẽ xuất hiện ở đây khi được đăng tải.",
            color = PostsScreenSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
