package com.example.uitvolunteermap.features.campaign.presentation.team

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.core.ui.theme.VolunteerFlowPalette
import com.example.uitvolunteermap.features.post.presentation.addpost.AddPostBottomSheetCard
import com.example.uitvolunteermap.features.post.presentation.addpost.AddPostPopupUiEvent

private val TeamTopBackground = VolunteerFlowPalette.BackgroundTop
private val TeamContentBackground = VolunteerFlowPalette.Background
private val TeamBorder = VolunteerFlowPalette.Border
private val TeamSurface = VolunteerFlowPalette.Surface
private val TeamSurfaceSoft = VolunteerFlowPalette.SurfaceSoft
private val TeamSurfaceVariant = VolunteerFlowPalette.SurfaceVariant
private val TeamPrimary = VolunteerFlowPalette.TextPrimary
private val TeamSecondary = VolunteerFlowPalette.TextSecondary
private val TeamMuted = VolunteerFlowPalette.TextMuted
private val TeamAccent = VolunteerFlowPalette.BrandAccent
private val TeamAccentPressed = VolunteerFlowPalette.BrandAccentPressed
private val TeamPrimaryAction = VolunteerFlowPalette.BrandPrimary
private val TeamSecondaryAction = VolunteerFlowPalette.BrandSecondary
private val TeamHighlight = VolunteerFlowPalette.WarningSurface
private val TeamInverse = VolunteerFlowPalette.TextInverse

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
                                showEditButton = !state.isGuest,
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
                                showAddButton = !state.isGuest,
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
                state = state.addPostSheet.toAddPostPopupUiState(canManagePosts = !state.isGuest),
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
                        is AddPostPopupUiEvent.RemoveAttachmentClicked -> {
                            onEvent(
                                TeamFormationDetailUiEvent.AddPostAttachmentRemoved(event.index)
                            )
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

@Composable
private fun TeamHeader(
    isGuest: Boolean,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TeamTopBackground)
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(TeamSurface)
                .border(1.dp, TeamBorder, CircleShape)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = TeamPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "UIT TÌNH NGUYỆN • ĐỘI HÌNH",
                color = TeamMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(TeamSurface)
                .border(1.dp, TeamBorder, RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (isGuest) "KHÁCH" else "TÌNH NGUYỆN",
                color = TeamPrimaryAction,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun TeamTitleSection(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "MHX 2026",
            color = TeamMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = highlightedTeamTitle(title),
            color = TeamPrimary,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = description,
            color = TeamSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TeamHeroCollage(
    heroCards: List<TeamHeroCardUiModel>,
    showEditButton: Boolean,
    onEditClick: () -> Unit
) {
    val leftCard = heroCards.getOrNull(0)
    val centerCard = heroCards.getOrNull(1)
    val rightCard = heroCards.getOrNull(2)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .padding(horizontal = 20.dp)
    ) {
        leftCard?.let {
            PlaceholderHeroCard(
                label = it.label,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp)
                    .size(width = 110.dp, height = 112.dp),
                isPrimary = false
            )
        }

        centerCard?.let {
            PlaceholderHeroCard(
                label = it.label,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(width = 140.dp, height = 142.dp),
                isPrimary = true
            )
        }

        rightCard?.let {
            PlaceholderHeroCard(
                label = it.label,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp)
                    .size(width = 110.dp, height = 112.dp),
                isPrimary = false
            )
        }

        // Nút chỉnh sửa ảnh — chỉ hiện với Volunteer, ẩn với Guest
        if (showEditButton) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 20.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TeamSurface)
                    .border(1.dp, TeamBorder, CircleShape)
                    .clickable(onClick = onEditClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "E",
                    color = TeamPrimaryAction,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PlaceholderHeroCard(
    label: String,
    modifier: Modifier,
    isPrimary: Boolean
) {
    val cardShape = RoundedCornerShape(if (isPrimary) 24.dp else 18.dp)
    Box(
        modifier = modifier
            .clip(cardShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isPrimary) {
                        listOf(TeamSurface, TeamSurfaceVariant)
                    } else {
                        listOf(TeamSurfaceSoft, TeamSurface)
                    }
                ),
                shape = cardShape
            )
            .border(1.dp, TeamBorder, cardShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isPrimary) TeamPrimaryAction else TeamMuted,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TeamLeadersSection(
    leaders: List<TeamLeaderUiModel>,
    onLeaderClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Ban chỉ huy",
            color = TeamPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (leaders.isEmpty()) {
                Text(
                    text = "Chưa có thông tin ban chỉ huy.",
                    color = TeamSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                leaders.forEach { leader ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onLeaderClick(leader.id) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(86.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(TeamSurfaceVariant, TeamSurface)
                                    )
                                )
                                .border(1.dp, TeamBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = leader.initials,
                                color = TeamPrimaryAction,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = leader.role,
                            color = TeamPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = leader.name,
                            color = TeamSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamActivitiesSection(
    activities: List<TeamActivityUiModel>,
    showAddButton: Boolean,
    onAddClick: () -> Unit,
    onActivityClick: (Int) -> Unit
) {
    // Lọc bỏ ô "+" nếu là Guest — giữ nguyên nếu là Volunteer
    val displayActivities = if (showAddButton) activities
    else activities.filter { !it.isAddButton }

    val firstRow = displayActivities.take(3)
    val secondRow = displayActivities.drop(3).take(3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Hoạt động",
            color = TeamPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        if (displayActivities.isEmpty()) {
            Text(
                text = "Chưa có hoạt động nào được ghi nhận.",
                color = TeamSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else {
            ActivityRow(
                activities = firstRow,
                onAddClick = onAddClick,
                onActivityClick = onActivityClick
            )
            if (secondRow.isNotEmpty()) {
                ActivityRow(
                    activities = secondRow,
                    onAddClick = onAddClick,
                    onActivityClick = onActivityClick
                )
            }
        }
    }
}

@Composable
private fun ActivityRow(
    activities: List<TeamActivityUiModel>,
    onAddClick: () -> Unit,
    onActivityClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        activities.forEach { activity ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(108.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (activity.isAddButton) {
                            TeamSurfaceVariant
                        } else {
                            TeamSurface
                        },
                        RoundedCornerShape(18.dp)
                    )
                    .border(
                        width = if (activity.isAddButton) 0.dp else 1.dp,
                        color = if (activity.isAddButton) TeamSurfaceVariant else TeamBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        if (activity.isAddButton) onAddClick() else onActivityClick(activity.id)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (activity.isAddButton) {
                    Box(
                        modifier = Modifier
                            .testTag(VolunteerFlowTestTags.TeamFormationAddActivity)
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(TeamAccent, TeamAccentPressed)
                                )
                            )
                            .border(1.dp, TeamAccentPressed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activity.label,
                            color = TeamInverse,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                } else {
                    Text(
                        text = activity.label,
                        color = TeamPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamErrorState(
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
            color = TeamPrimary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(18.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = TeamAccent,
                contentColor = TeamInverse
            )
        ) {
            Text(text = "Thử lại")
        }
    }
}

private fun highlightedTeamTitle(title: String) = buildAnnotatedString {
    val words = title.split(Regex("\\s+")).filter { it.isNotBlank() }
    if (words.isEmpty()) return@buildAnnotatedString

    append(words.first())
    if (words.size > 1) {
        append(" ")
        withStyle(SpanStyle(background = TeamHighlight)) {
            append(words.drop(1).joinToString(" "))
        }
    }
    append(".")
}

private fun TeamAddPostSheetUiState.toAddPostPopupUiState(
    canManagePosts: Boolean
) = com.example.uitvolunteermap.features.post.presentation.addpost.AddPostPopupUiState(
    canManagePosts = canManagePosts,
    title = title,
    content = content,
    attachmentNames = attachmentNames,
    isSubmitting = isSubmitting,
    errorMessage = errorMessage
)
