package com.example.uitvolunteermap.features.campaign.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignBackdrop
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDescriptionBlock
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenBackground
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenBackgroundBottom
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenBackgroundTop
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignHeroSection
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignMapSection
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignPostsBlock
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignStatsGrid
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignTeamsBlock
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.DetailErrorState
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.DetailTopBar

@Composable
fun CampaignDetailScreen(
    state: CampaignDetailUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CampaignDetailUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDescriptionExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(VolunteerFlowTestTags.CampaignDetailScreen),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = ScreenBackground
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
            CampaignBackdrop()

            when {
                state.isLoading && state.title.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.title.isEmpty() -> {
                    DetailErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(CampaignDetailUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(VolunteerFlowTestTags.CampaignDetailList),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item {
                            DetailTopBar(
                                routeLabel = "UIT TÌNH NGUYỆN • CHIẾN DỊCH",
                                onBackClick = { onEvent(CampaignDetailUiEvent.BackClicked) }
                            )
                        }
                        item {
                            CampaignHeroSection(
                                routeSubtitle = state.schedule,
                                title = state.title,
                                supportingText = state.heroSupportingText
                            )
                        }
                        item {
                            CampaignStatsGrid(stats = state.stats)
                        }
                        item {
                            CampaignDescriptionBlock(
                                description = state.description,
                                isExpanded = isDescriptionExpanded,
                                onReadMore = {
                                    isDescriptionExpanded = !isDescriptionExpanded
                                    onEvent(CampaignDetailUiEvent.ReadMoreClicked)
                                }
                            )
                        }
                        item {
                            CampaignTeamsBlock(
                                title = state.teamSectionTitle,
                                teams = state.teams,
                                onTeamClick = { teamId ->
                                    onEvent(CampaignDetailUiEvent.TeamClicked(teamId))
                                }
                            )
                        }
                        item {
                            CampaignPostsBlock(
                                posts = state.posts,
                                canManageCampaigns = state.canManageCampaigns,
                                onViewAllPosts = {
                                    onEvent(CampaignDetailUiEvent.ViewAllPostsClicked)
                                },
                                onPostClick = { postId ->
                                    onEvent(CampaignDetailUiEvent.PostClicked(postId))
                                },
                                onEditClick = {
                                    onEvent(CampaignDetailUiEvent.EditClicked)
                                },
                                onDeleteClick = {
                                    onEvent(CampaignDetailUiEvent.DeleteClicked)
                                }
                            )
                        }
                        state.mapOverview?.let { mapOverview ->
                            item {
                                CampaignMapSection(
                                    mapOverview = mapOverview,
                                    onOpenMaps = {
                                        onEvent(CampaignDetailUiEvent.OpenGoogleMapsClicked)
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
