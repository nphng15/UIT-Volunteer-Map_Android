package com.example.uitvolunteermap.features.post.presentation.campaignposts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.VolunteerBottomBar
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.CampaignPostCard
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.DeletePostDialog
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.EmptyPostsState
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostEditorCard
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsErrorState
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsHeader
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsInlineError
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenAccent
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenAccentSoft
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenCoral
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenBottom
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenSurface
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.PostsScreenTop
import com.example.uitvolunteermap.features.post.presentation.campaignposts.components.TeamFilterRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignPostsScreen(
    state: CampaignPostsUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CampaignPostsUiEvent) -> Unit,
    onHomeTabClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val bottomSheetState = androidx.compose.material3.rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    if (state.deleteTarget != null) {
        DeletePostDialog(
            title = state.deleteTarget.title,
            isBusy = state.isSaving,
            onDismiss = { onEvent(CampaignPostsUiEvent.DeleteDismissed) },
            onConfirm = { onEvent(CampaignPostsUiEvent.DeleteConfirmed) }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(VolunteerFlowTestTags.CampaignPostsScreen),
        containerColor = PostsScreenSurface,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            VolunteerBottomBar(
                currentTab = VolunteerBottomBarTab.Post,
                onTabSelected = { selectedTab ->
                    when (selectedTab) {
                        VolunteerBottomBarTab.Home -> onHomeTabClick()
                        VolunteerBottomBarTab.Map -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Màn bản đồ sẽ được cập nhật sớm.")
                            }
                        }
                        VolunteerBottomBarTab.Post -> Unit
                        VolunteerBottomBarTab.Me -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Màn cá nhân sẽ được cập nhật sớm.")
                            }
                        }
                    }
                }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PostsScreenAccentSoft.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PostsScreenCoral.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            radius = 860f
                        )
                    )
            )

            when {
                state.isLoading && state.campaignTitle.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PostsScreenAccent
                    )
                }

                state.errorMessage != null && state.campaignTitle.isEmpty() -> {
                    PostsErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(CampaignPostsUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing14)
                    ) {
                        item {
                            PostsHeader(
                                appName = state.appName,
                                campaignTitle = state.campaignTitle,
                                canCreatePost = state.canManagePosts,
                                onBackClick = { onEvent(CampaignPostsUiEvent.BackClicked) },
                                onCreateClick = {
                                    onEvent(CampaignPostsUiEvent.CreatePostClicked(state.selectedTeamId))
                                }
                            )
                        }
                        item {
                            TeamFilterRow(
                                teams = state.teamFilters,
                                selectedTeamId = state.selectedTeamId,
                                onAllSelected = {
                                    onEvent(CampaignPostsUiEvent.TeamFilterSelected(null))
                                },
                                onTeamSelected = { teamId ->
                                    onEvent(CampaignPostsUiEvent.TeamFilterSelected(teamId))
                                }
                            )
                        }
                        if (state.errorMessage != null && state.campaignTitle.isNotEmpty()) {
                            item {
                                PostsInlineError(
                                    message = state.errorMessage,
                                    onRetry = { onEvent(CampaignPostsUiEvent.RefreshRequested) }
                                )
                            }
                        }

                        if (state.posts.isEmpty()) {
                            item {
                                EmptyPostsState(
                                    selectedTeamId = state.selectedTeamId,
                                    teams = state.teamFilters,
                                    canCreatePost = state.canManagePosts,
                                    onCreateClick = {
                                        onEvent(
                                            CampaignPostsUiEvent.CreatePostClicked(
                                                state.selectedTeamId
                                            )
                                        )
                                    }
                                )
                            }
                        } else {
                            items(state.posts, key = { it.id }) { post ->
                                CampaignPostCard(
                                    post = post,
                                    isExpanded = state.expandedPostId == post.id,
                                    canManagePost = state.canManagePosts,
                                    onClick = {
                                        onEvent(CampaignPostsUiEvent.PostCardClicked(post.id))
                                    },
                                    onEditClick = {
                                        onEvent(CampaignPostsUiEvent.EditPostClicked(post.id))
                                    },
                                    onDeleteClick = {
                                        onEvent(CampaignPostsUiEvent.DeletePostClicked(post.id))
                                    }
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

    if (state.editor != null && state.canManagePosts) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(CampaignPostsUiEvent.EditorDismissed) },
            sheetState = bottomSheetState,
            dragHandle = null,
            containerColor = Color.Transparent,
            scrimColor = PostsScreenAccent.copy(alpha = 0.12f)
        ) {
            PostEditorCard(
                editor = state.editor,
                teams = state.teamFilters,
                isSaving = state.isSaving,
                onDismiss = { onEvent(CampaignPostsUiEvent.EditorDismissed) },
                onTitleChanged = {
                    onEvent(CampaignPostsUiEvent.EditorTitleChanged(it))
                },
                onContentChanged = {
                    onEvent(CampaignPostsUiEvent.EditorContentChanged(it))
                },
                onTeamSelected = { teamId ->
                    onEvent(CampaignPostsUiEvent.EditorTeamChanged(teamId))
                },
                onAttachmentInputChanged = {
                    onEvent(CampaignPostsUiEvent.EditorAttachmentInputChanged(it))
                },
                onAttachmentAdded = {
                    onEvent(CampaignPostsUiEvent.AddAttachmentClicked)
                },
                onAttachmentRemoved = { index ->
                    onEvent(CampaignPostsUiEvent.RemoveAttachmentClicked(index))
                },
                onSave = { onEvent(CampaignPostsUiEvent.SaveEditorClicked) },
                modifier = Modifier.padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing8)
            )
        }
    }
}
