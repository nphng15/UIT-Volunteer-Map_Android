package com.example.uitvolunteermap.features.campaign.presentation.list

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.VolunteerTopBar
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListEmptyState
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListErrorState
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListItem
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListContentBackground
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListPrimaryText
import com.example.uitvolunteermap.features.campaign.presentation.list.components.CampaignListTokens.ListTopBackground
import com.example.uitvolunteermap.features.campaign.presentation.list.components.ConfirmDeleteDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignListScreen(
    state: CampaignListUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CampaignListUiEvent) -> Unit,
    onBack: () -> Unit,
    onCreateCampaign: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            onEvent(CampaignListUiEvent.PullToRefreshTriggered)
        }
    }

    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    if (state.pendingDeleteId != null) {
        ConfirmDeleteDialog(
            onConfirm = { onEvent(CampaignListUiEvent.DeleteConfirmed) },
            onDismiss = { onEvent(CampaignListUiEvent.DeleteCancelled) }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = ListContentBackground,
        floatingActionButton = {
            if (state.canManageCampaigns) {
                FloatingActionButton(
                    onClick = onCreateCampaign,
                    containerColor = VolunteerFlowPalette.BrandAccent,
                    contentColor = Color.White,
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
            VolunteerTopBar(
                onBack = onBack,
                title = "Danh sách chiến dịch",
                titleColor = ListPrimaryText,
                containerColor = ListTopBackground
            )
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
                    CampaignListErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(CampaignListUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(pullRefreshState.nestedScrollConnection)
                    ) {
                        if (state.campaigns.isEmpty()) {
                            CampaignListEmptyState(
                                onRefresh = { onEvent(CampaignListUiEvent.RefreshRequested) }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.campaigns, key = { it.campaignId }) { campaign ->
                                    CampaignListItem(
                                        campaign = campaign,
                                        showDeleteButton = state.canManageCampaigns,
                                        onClick = {
                                            onEvent(CampaignListUiEvent.CampaignClicked(campaign.campaignId))
                                        },
                                        onDeleteClick = {
                                            onEvent(CampaignListUiEvent.DeleteClicked(campaign.campaignId))
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
