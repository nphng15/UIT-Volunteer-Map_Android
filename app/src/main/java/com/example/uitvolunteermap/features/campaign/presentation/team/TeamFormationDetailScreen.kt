package com.example.uitvolunteermap.features.campaign.presentation.team

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamActivitiesSection
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamContentBackground
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamPrimary
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamTopBackground
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamErrorState
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamHeader
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamHeroCollage
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamLeadersSection
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamTitleSection
import com.example.uitvolunteermap.features.post.presentation.addpost.AddPostBottomSheetCard
import com.example.uitvolunteermap.features.post.presentation.addpost.AddPostPopupUiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamFormationDetailScreen(
    state: TeamFormationDetailUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (TeamFormationDetailUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomSheetState = androidx.compose.material3.rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(VolunteerFlowTestTags.TeamFormationDetailScreen),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = TeamContentBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(TeamTopBackground, TeamContentBackground)
                    )
                )
        ) {
            when {
                state.isLoading && state.title.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null && state.title.isEmpty() -> {
                    TeamErrorState(
                        message = state.errorMessage,
                        onRetry = { onEvent(TeamFormationDetailUiEvent.RefreshRequested) }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(28.dp)
                    ) {
                        item {
                            TeamHeader(
                                isGuest = state.isGuest,
                                onBackClick = { onEvent(TeamFormationDetailUiEvent.BackClicked) }
                            )
                        }
                        item {
                            TeamTitleSection(
                                title = state.title,
                                description = state.description
                            )
                        }
                        item {
                            TeamHeroCollage(
                                heroCards = state.heroCards,
                                showEditButton = state.canManagePosts,
                                onEditClick = { onEvent(TeamFormationDetailUiEvent.HeroEditClicked) }
                            )
                        }
                        item {
                            TeamLeadersSection(
                                leaders = state.leaders,
                                onLeaderClick = { leaderId ->
                                    onEvent(TeamFormationDetailUiEvent.LeaderClicked(leaderId))
                                }
                            )
                        }
                        item {
                            TeamActivitiesSection(
                                activities = state.activities,
                                showAddButton = state.canManagePosts,
                                onAddClick = { onEvent(TeamFormationDetailUiEvent.AddActivityClicked) },
                                onActivityClick = { activityId ->
                                    onEvent(TeamFormationDetailUiEvent.ActivityClicked(activityId))
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    if (state.addPostSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(TeamFormationDetailUiEvent.AddPostDismissed) },
            sheetState = bottomSheetState,
            dragHandle = null,
            containerColor = Color.Transparent,
            scrimColor = TeamPrimary.copy(alpha = 0.18f)
        ) {
            AddPostBottomSheetCard(
                state = state.addPostSheet.toAddPostPopupUiState(canManagePosts = state.canManagePosts),
                onEvent = { event ->
                    when (event) {
                        AddPostPopupUiEvent.CloseClicked -> {
                            onEvent(TeamFormationDetailUiEvent.AddPostDismissed)
                        }
                        is AddPostPopupUiEvent.TitleChanged -> {
                            onEvent(TeamFormationDetailUiEvent.AddPostTitleChanged(event.value))
                        }
                        is AddPostPopupUiEvent.ContentChanged -> {
                            onEvent(TeamFormationDetailUiEvent.AddPostContentChanged(event.value))
                        }
                        AddPostPopupUiEvent.UploadClicked -> {
                            onEvent(TeamFormationDetailUiEvent.AddPostUploadClicked)
                        }
                        is AddPostPopupUiEvent.ImagesPicked -> {
                            onEvent(TeamFormationDetailUiEvent.AddPostImagesPicked(event.uris))
                        }
                        is AddPostPopupUiEvent.RemoveAttachmentClicked -> {
                            onEvent(
                                TeamFormationDetailUiEvent.AddPostAttachmentRemoved(event.index)
                            )
                        }
                        AddPostPopupUiEvent.RegenerateCaptionClicked -> {
                            onEvent(TeamFormationDetailUiEvent.AddPostRegenerateCaptionClicked)
                        }
                        AddPostPopupUiEvent.AcceptSuggestionClicked -> {
                            onEvent(TeamFormationDetailUiEvent.AddPostAcceptSuggestionClicked)
                        }
                        is AddPostPopupUiEvent.CaptionModeChanged -> {
                            onEvent(TeamFormationDetailUiEvent.AddPostCaptionModeChanged(event.mode))
                        }
                        AddPostPopupUiEvent.PublishClicked -> {
                            onEvent(TeamFormationDetailUiEvent.AddPostPublishClicked)
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

private fun TeamAddPostSheetUiState.toAddPostPopupUiState(
    canManagePosts: Boolean
) = com.example.uitvolunteermap.features.post.presentation.addpost.AddPostPopupUiState(
    canManagePosts = canManagePosts,
    title = title,
    content = content,
    pickedImages = pickedImages,
    isSubmitting = isSubmitting,
    errorMessage = errorMessage,
    isGeneratingCaption = isGeneratingCaption,
    captionSuggestion = captionSuggestion,
    regenerateNonce = regenerateNonce,
    captionMode = captionMode,
    gemmaModelAvailable = gemmaModelAvailable
)
