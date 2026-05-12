package com.example.uitvolunteermap.features.admin.team.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.AdminBottomBar
import com.example.uitvolunteermap.core.ui.AdminBottomBarTab
import com.example.uitvolunteermap.core.ui.AdminTopBar
import com.example.uitvolunteermap.features.admin.team.presentation.components.AdminTeamEmptyState
import com.example.uitvolunteermap.features.admin.team.presentation.components.AdminTeamErrorState
import com.example.uitvolunteermap.features.admin.team.presentation.components.AdminTeamFormSheet
import com.example.uitvolunteermap.features.admin.team.presentation.components.AdminTeamListItem
import com.example.uitvolunteermap.features.admin.team.presentation.components.AdminTeamTokens
import com.example.uitvolunteermap.features.admin.team.presentation.components.ConfirmDeleteTeamDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTeamScreen(
    state: AdminTeamUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AdminTeamUiEvent) -> Unit,
    onTabSelected: (AdminBottomBarTab) -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            onEvent(AdminTeamUiEvent.PullToRefreshTriggered)
        }
    }
    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    // Dialog xác nhận xóa
    state.pendingDeleteId?.let { id ->
        val name = state.teams.firstOrNull { it.teamId == id }?.teamName.orEmpty()
        ConfirmDeleteTeamDialog(
            teamName = name,
            onConfirm = { onEvent(AdminTeamUiEvent.DeleteConfirmed) },
            onDismiss = { onEvent(AdminTeamUiEvent.DeleteCancelled) }
        )
    }

    // Sheet tạo / sửa
    state.formState?.let { form ->
        AdminTeamFormSheet(
            form = form,
            onTeamNameChange = { onEvent(AdminTeamUiEvent.FormTeamNameChanged(it)) },
            onLeaderIdChange = { onEvent(AdminTeamUiEvent.FormLeaderIdChanged(it)) },
            onCampaignIdChange = { onEvent(AdminTeamUiEvent.FormCampaignIdChanged(it)) },
            onDescriptionChange = { onEvent(AdminTeamUiEvent.FormDescriptionChanged(it)) },
            onImageUrlChange = { onEvent(AdminTeamUiEvent.FormImageUrlChanged(it)) },
            onSubmit = { onEvent(AdminTeamUiEvent.FormSubmitted) },
            onDismiss = { onEvent(AdminTeamUiEvent.FormDismissed) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AdminTeamTokens.ScreenBackground,
        topBar = {
            AdminTopBar(onLogout = onLogout)
        },
        bottomBar = {
            AdminBottomBar(
                currentTab = AdminBottomBarTab.Teams,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (state.canManageTeams) {
                FloatingActionButton(
                    onClick = { onEvent(AdminTeamUiEvent.CreateClicked) },
                    containerColor = AdminTeamTokens.Accent,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tạo đội mới")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading && state.teams.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.teams.isEmpty() -> {
                    AdminTeamErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(AdminTeamUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(pullRefreshState.nestedScrollConnection)
                    ) {
                        val visible = state.visibleTeams
                        when {
                            // Không có đội nào (chưa tạo / list rỗng) → empty state toàn màn.
                            state.teams.isEmpty() -> AdminTeamEmptyState(
                                onRefresh = { onEvent(AdminTeamUiEvent.RefreshRequested) }
                            )

                            else -> LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    SearchBar(
                                        query = state.searchQuery,
                                        onQueryChange = {
                                            onEvent(AdminTeamUiEvent.SearchQueryChanged(it))
                                        }
                                    )
                                }
                                if (visible.isEmpty()) {
                                    // Có đội nhưng bộ lọc không khớp → giữ search bar để user xóa từ khóa.
                                    item {
                                        Text(
                                            text = "Không tìm thấy đội nào phù hợp.",
                                            color = AdminTeamTokens.SecondaryText,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp, vertical = 24.dp)
                                        )
                                    }
                                } else {
                                    items(visible, key = { it.teamId }) { team ->
                                        AdminTeamListItem(
                                            team = team,
                                            showActions = state.canManageTeams,
                                            onEditClick = {
                                                onEvent(AdminTeamUiEvent.EditClicked(team.teamId))
                                            },
                                            onDeleteClick = {
                                                onEvent(AdminTeamUiEvent.DeleteClicked(team.teamId))
                                            }
                                        )
                                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(AdminTeamTokens.Surface, RoundedCornerShape(16.dp)),
        placeholder = {
            Text(text = "Tìm theo tên đội hoặc nhóm trưởng...", color = AdminTeamTokens.MutedText)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = AdminTeamTokens.MutedText
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AdminTeamTokens.Brand,
            unfocusedBorderColor = AdminTeamTokens.Border,
            focusedTextColor = AdminTeamTokens.PrimaryText,
            unfocusedTextColor = AdminTeamTokens.PrimaryText,
            cursorColor = AdminTeamTokens.Brand
        )
    )
}
