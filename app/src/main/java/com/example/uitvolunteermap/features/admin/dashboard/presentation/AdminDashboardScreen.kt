package com.example.uitvolunteermap.features.admin.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.AdminBottomBar
import com.example.uitvolunteermap.core.ui.AdminBottomBarTab
import com.example.uitvolunteermap.core.ui.AdminTopBar
import com.example.uitvolunteermap.features.admin.dashboard.presentation.components.AdminDashboardErrorState
import com.example.uitvolunteermap.features.admin.dashboard.presentation.components.AdminDashboardHero
import com.example.uitvolunteermap.features.admin.dashboard.presentation.components.AdminDashboardSectionHeader
import com.example.uitvolunteermap.features.admin.dashboard.presentation.components.AdminDashboardTokens
import com.example.uitvolunteermap.features.admin.dashboard.presentation.components.AdminQuickActionCard
import com.example.uitvolunteermap.features.admin.dashboard.presentation.components.AdminStatCard

@Composable
fun AdminDashboardScreen(
    state: AdminDashboardUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AdminDashboardUiEvent) -> Unit,
    onTabSelected: (AdminBottomBarTab) -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AdminDashboardTokens.ScreenBackgroundBottom,
        topBar = { AdminTopBar(onLogout = onLogout) },
        bottomBar = {
            AdminBottomBar(
                currentTab = AdminBottomBarTab.Dashboard,
                onTabSelected = { selectedTab ->
                    if (selectedTab != AdminBottomBarTab.Dashboard) {
                        onTabSelected(selectedTab)
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
                        colors = listOf(
                            AdminDashboardTokens.ScreenBackgroundTop,
                            AdminDashboardTokens.ScreenBackgroundBottom
                        )
                    )
                )
        ) {
            when {
                state.isLoading && state.hasNoStats -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.hasNoStats -> {
                    AdminDashboardErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(AdminDashboardUiEvent.Refresh) }
                    )
                }

                else -> {
                    AdminDashboardContent(
                        state = state,
                        onTabSelected = onTabSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminDashboardContent(
    state: AdminDashboardUiState,
    onTabSelected: (AdminBottomBarTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            AdminDashboardHero(username = state.username)
        }

        item {
            AdminDashboardSectionHeader(title = "Thống kê nhanh")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    label = "Tài khoản",
                    count = state.accountCount,
                    icon = Icons.Outlined.ManageAccounts,
                    accentColor = AdminDashboardTokens.BrandPrimary,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    label = "Chiến dịch",
                    count = state.campaignCount,
                    icon = Icons.Outlined.Campaign,
                    accentColor = AdminDashboardTokens.Success,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    label = "Đội nhóm",
                    count = state.teamCount,
                    icon = Icons.Outlined.Group,
                    accentColor = AdminDashboardTokens.Info,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    label = "Bài viết",
                    count = state.postCount,
                    icon = Icons.AutoMirrored.Outlined.Article,
                    accentColor = AdminDashboardTokens.BrandAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            AdminDashboardSectionHeader(title = "Quản lý")
        }

        item {
            AdminQuickActionCard(
                title = "Quản lý tài khoản",
                subtitle = "Xem và phân quyền người dùng",
                icon = Icons.Outlined.ManageAccounts,
                accentColor = AdminDashboardTokens.BrandPrimary,
                onClick = { onTabSelected(AdminBottomBarTab.Accounts) }
            )
        }

        item {
            AdminQuickActionCard(
                title = "Quản lý chiến dịch",
                subtitle = "Tạo và chỉnh sửa chiến dịch",
                icon = Icons.Outlined.Campaign,
                accentColor = AdminDashboardTokens.Success,
                onClick = { onTabSelected(AdminBottomBarTab.Campaigns) }
            )
        }

        item {
            AdminQuickActionCard(
                title = "Quản lý đội nhóm",
                subtitle = "Tổ chức và phân công đội",
                icon = Icons.Outlined.Group,
                accentColor = AdminDashboardTokens.Info,
                onClick = { onTabSelected(AdminBottomBarTab.Teams) }
            )
        }

        item {
            AdminQuickActionCard(
                title = "Quản lý bài viết",
                subtitle = "Đăng và kiểm duyệt nội dung",
                icon = Icons.AutoMirrored.Outlined.Article,
                accentColor = AdminDashboardTokens.BrandAccent,
                onClick = { onTabSelected(AdminBottomBarTab.Posts) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
