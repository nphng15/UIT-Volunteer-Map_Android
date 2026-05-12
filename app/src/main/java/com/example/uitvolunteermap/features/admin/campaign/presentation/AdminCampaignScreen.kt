package com.example.uitvolunteermap.features.admin.campaign.presentation

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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.AdminBottomBar
import com.example.uitvolunteermap.core.ui.AdminBottomBarTab
import com.example.uitvolunteermap.core.ui.AdminTopBar
import com.example.uitvolunteermap.features.admin.campaign.presentation.components.AdminCampaignCard
import com.example.uitvolunteermap.features.admin.campaign.presentation.components.AdminCampaignDeleteDialog
import com.example.uitvolunteermap.features.admin.campaign.presentation.components.AdminCampaignEmptyState
import com.example.uitvolunteermap.features.admin.campaign.presentation.components.AdminCampaignErrorState
import com.example.uitvolunteermap.features.admin.campaign.presentation.components.AdminCampaignFormSheet
import com.example.uitvolunteermap.features.admin.campaign.presentation.components.AdminCampaignSearchBar
import com.example.uitvolunteermap.features.admin.campaign.presentation.components.AdminCampaignTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCampaignScreen(
    state: AdminCampaignUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AdminCampaignUiEvent) -> Unit,
    onTabSelected: (AdminBottomBarTab) -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullToRefreshState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            onEvent(AdminCampaignUiEvent.PullToRefreshTriggered)
        }
    }

    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    if (state.pendingDeleteId != null) {
        AdminCampaignDeleteDialog(
            onConfirm = { onEvent(AdminCampaignUiEvent.DeleteConfirmed) },
            onDismiss = { onEvent(AdminCampaignUiEvent.DeleteCancelled) }
        )
    }

    if (state.form != null) {
        AdminCampaignFormSheet(
            form = state.form,
            sheetState = sheetState,
            onEvent = onEvent
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AdminCampaignTokens.ContentBackground,
        bottomBar = {
            AdminBottomBar(
                currentTab = AdminBottomBarTab.Campaigns,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (state.canManageCampaigns) {
                FloatingActionButton(
                    onClick = { onEvent(AdminCampaignUiEvent.CreateClicked) },
                    containerColor = AdminCampaignTokens.Accent,
                    contentColor = AdminCampaignTokens.Inverse,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tạo chiến dịch"
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
                state.isLoading && state.campaigns.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.campaigns.isEmpty() -> {
                    AdminCampaignErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(AdminCampaignUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    val visible = state.visibleCampaigns
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(pullRefreshState.nestedScrollConnection)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                AdminCampaignSearchBar(
                                    query = state.searchQuery,
                                    onQueryChange = {
                                        onEvent(AdminCampaignUiEvent.SearchQueryChanged(it))
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }

                            if (visible.isEmpty()) {
                                item {
                                    AdminCampaignEmptyState(
                                        hasSearchQuery = state.searchQuery.isNotBlank(),
                                        onRefresh = { onEvent(AdminCampaignUiEvent.RefreshRequested) }
                                    )
                                }
                            } else {
                                items(visible, key = { it.campaignId }) { campaign ->
                                    AdminCampaignCard(
                                        campaign = campaign,
                                        showActions = state.canManageCampaigns,
                                        onClick = {
                                            onEvent(AdminCampaignUiEvent.EditClicked(campaign.campaignId))
                                        },
                                        onEditClick = {
                                            onEvent(AdminCampaignUiEvent.EditClicked(campaign.campaignId))
                                        },
                                        onDeleteClick = {
                                            onEvent(AdminCampaignUiEvent.DeleteClicked(campaign.campaignId))
                                        }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(72.dp)) }
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
