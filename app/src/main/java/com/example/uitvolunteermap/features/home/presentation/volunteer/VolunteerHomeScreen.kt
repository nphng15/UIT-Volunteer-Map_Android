package com.example.uitvolunteermap.features.home.presentation.volunteer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.VolunteerBottomBar
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.CampaignSectionHeader
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.EmptyVolunteerCampaignState
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.OverviewStatsStrip
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerBackdrop
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerCampaignCard
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeErrorState
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeHero
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenBackground
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenBackgroundBottom
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenBackgroundTop

@Composable
fun VolunteerHomeScreen(
    state: VolunteerHomeUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (VolunteerHomeUiEvent) -> Unit,
    onTabSelected: (VolunteerBottomBarTab) -> Unit = {},
    onSeeAllCampaigns: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(VolunteerFlowTestTags.VolunteerHomeScreen),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = ScreenBackground,
        bottomBar = {
            VolunteerBottomBar(
                currentTab = VolunteerBottomBarTab.Home,
                onTabSelected = { selectedTab ->
                    if (selectedTab != VolunteerBottomBarTab.Home) {
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
                        colors = listOf(ScreenBackgroundTop, ScreenBackground, ScreenBackgroundBottom)
                    )
                )
        ) {
            VolunteerBackdrop()

            when {
                state.isLoading && state.campaigns.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.campaigns.isEmpty() -> {
                    VolunteerHomeErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(VolunteerHomeUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        item {
                            VolunteerHomeHero(
                                appName = state.appName,
                                isGuest = state.isGuest,
                                roleBadge = state.roleBadge,
                                stats = state.stats
                            )
                        }
                        item {
                            OverviewStatsStrip(stats = state.stats)
                        }
                        item {
                            CampaignSectionHeader(onSeeAll = onSeeAllCampaigns)
                        }
                        if (state.campaigns.isEmpty()) {
                            item {
                                EmptyVolunteerCampaignState(
                                    onRetry = {
                                        onEvent(VolunteerHomeUiEvent.RefreshRequested)
                                    }
                                )
                            }
                        } else {
                            items(state.campaigns, key = { it.id }) { campaign ->
                                VolunteerCampaignCard(
                                    campaign = campaign,
                                    onClick = {
                                        onEvent(
                                            VolunteerHomeUiEvent.CampaignPrimaryClicked(campaign.id)
                                        )
                                    }
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
