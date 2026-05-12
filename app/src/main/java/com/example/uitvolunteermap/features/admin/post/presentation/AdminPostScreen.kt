package com.example.uitvolunteermap.features.admin.post.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.AdminBottomBar
import com.example.uitvolunteermap.core.ui.AdminBottomBarTab
import com.example.uitvolunteermap.core.ui.AdminTopBar
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette
import com.example.uitvolunteermap.features.admin.post.presentation.components.AdminPostDeleteDialog
import com.example.uitvolunteermap.features.admin.post.presentation.components.AdminPostEmptyState
import com.example.uitvolunteermap.features.admin.post.presentation.components.AdminPostErrorState
import com.example.uitvolunteermap.features.admin.post.presentation.components.AdminPostFormSheet
import com.example.uitvolunteermap.features.admin.post.presentation.components.AdminPostListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPostScreen(
    state: AdminPostUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AdminPostUiEvent) -> Unit,
    onTabSelected: (AdminBottomBarTab) -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            onEvent(AdminPostUiEvent.PullToRefreshTriggered)
        }
    }

    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    if (state.pendingDeleteId != null) {
        AdminPostDeleteDialog(
            onConfirm = { onEvent(AdminPostUiEvent.DeleteConfirmed) },
            onDismiss = { onEvent(AdminPostUiEvent.DeleteCancelled) }
        )
    }

    state.form?.let { form ->
        AdminPostFormSheet(
            form = form,
            isSaving = state.isSaving,
            onTitleChanged = { onEvent(AdminPostUiEvent.TitleChanged(it)) },
            onContentChanged = { onEvent(AdminPostUiEvent.ContentChanged(it)) },
            onTeamIdChanged = { onEvent(AdminPostUiEvent.TeamIdChanged(it)) },
            onAuthorIdChanged = { onEvent(AdminPostUiEvent.AuthorIdChanged(it)) },
            onSubmit = { onEvent(AdminPostUiEvent.FormSubmitted) },
            onDismiss = { onEvent(AdminPostUiEvent.FormDismissed) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = VolunteerFlowPalette.AdminBackgroundBottom,
        bottomBar = {
            AdminBottomBar(
                currentTab = AdminBottomBarTab.Posts,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (state.canManagePosts) {
                FloatingActionButton(
                    onClick = { onEvent(AdminPostUiEvent.CreateClicked) },
                    containerColor = VolunteerFlowPalette.BrandAccent,
                    contentColor = VolunteerFlowPalette.TextInverse,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Đăng bài viết mới"
                    )
                }
            }
        },
        topBar = {
            AdminTopBar(onLogout = onLogout)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading && state.posts.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.posts.isEmpty() -> {
                    AdminPostErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(AdminPostUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(pullRefreshState.nestedScrollConnection)
                    ) {
                        if (state.posts.isEmpty()) {
                            AdminPostEmptyState(
                                onRefresh = { onEvent(AdminPostUiEvent.RefreshRequested) }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.posts, key = { it.id }) { post ->
                                    AdminPostListItem(
                                        post = post,
                                        showActions = state.canManagePosts,
                                        onClick = {
                                            onEvent(AdminPostUiEvent.EditClicked(post.id))
                                        },
                                        onEditClick = {
                                            onEvent(AdminPostUiEvent.EditClicked(post.id))
                                        },
                                        onDeleteClick = {
                                            onEvent(AdminPostUiEvent.DeleteClicked(post.id))
                                        }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(8.dp)) }
                            }
                        }

                        PullToRefreshContainer(
                            modifier = Modifier.align(Alignment.TopCenter),
                            state = pullRefreshState
                        )
                    }
                }
            }
        }
    }
}
