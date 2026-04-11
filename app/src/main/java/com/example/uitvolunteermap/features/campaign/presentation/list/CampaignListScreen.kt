package com.example.uitvolunteermap.features.campaign.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Palette nhất quán với VolunteerHomeScreen
private val ListTopBackground = Color(0xFFF7F1D8)
private val ListContentBackground = Color(0xFFFFFDF9)
private val ListBorder = Color(0xFFE7DED3)
private val ListPrimaryText = Color(0xFF1F1A17)
private val ListSecondaryText = Color(0xFF625750)
private val ListAccentSoft = Color(0xFFFFF4CC)
private val ListEmptyIconBackground = Color(0xFFEDE8E3)

// BOM 2024.06.00 → Material3 1.2.x: dùng PullToRefreshContainer + rememberPullToRefreshState
// (PullToRefreshBox chỉ có từ M3 1.3.0 / BOM 2024.09.00 trở lên)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignListScreen(
    state: CampaignListUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CampaignListUiEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullToRefreshState()

    // Gesture hoàn tất → forward sự kiện lên ViewModel
    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            onEvent(CampaignListUiEvent.PullToRefreshTriggered)
        }
    }

    // ViewModel load xong → reset indicator
    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = ListContentBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Danh sach chien dich",
                        color = ListPrimaryText,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lai",
                            tint = ListPrimaryText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ListTopBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                // Full-screen spinner chỉ khi load lần đầu (chưa có data)
                state.isLoading && state.campaigns.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // Error state khi chưa có data nào (lần đầu thất bại)
                state.errorMessage != null && state.campaigns.isEmpty() -> {
                    CampaignListErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(CampaignListUiEvent.RefreshRequested) }
                    )
                }

                // Bình thường: PullToRefresh bao ngoài (xử lý cả empty list và có data)
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
                                        onClick = {
                                            onEvent(CampaignListUiEvent.CampaignClicked(campaign.campaignId))
                                        }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(8.dp)) }
                            }
                        }

                        // Indicator luôn nằm trên cùng của content area
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

@Composable
private fun CampaignListItem(
    campaign: CampaignListItemUiModel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(width = 1.dp, color = ListBorder, shape = RoundedCornerShape(20.dp))
            .background(color = ListContentBackground, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = campaign.campaignName,
            color = ListPrimaryText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = campaign.dateRange,
            color = ListSecondaryText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (campaign.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = campaign.description,
                color = ListSecondaryText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CampaignListEmptyState(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color = ListEmptyIconBackground, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "—",
                color = ListSecondaryText,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chua co chien dich nao",
            color = ListPrimaryText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Keo xuong de lam moi hoac quay lai sau.",
            color = ListSecondaryText,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRefresh,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ListAccentSoft,
                contentColor = ListPrimaryText
            )
        ) {
            Text(text = "Lam moi", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CampaignListErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = ListPrimaryText,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ListAccentSoft,
                contentColor = ListPrimaryText
            )
        ) {
            Text(text = "Thu lai", fontWeight = FontWeight.Bold)
        }
    }
}
