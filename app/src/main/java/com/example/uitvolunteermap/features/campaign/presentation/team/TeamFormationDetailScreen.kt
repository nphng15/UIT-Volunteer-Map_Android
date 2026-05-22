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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags

private val TeamTopBackground = Color(0xFFF7F1D8)
private val TeamContentBackground = Color(0xFFFFFDF9)
private val TeamBorder = Color(0xFFE3E6EB)
private val TeamSurface = Color(0xFFF1F3F7)
private val TeamPrimary = Color(0xFF121212)
private val TeamSecondary = Color(0xFF6B7280)
private val TeamPlaceholder = Color(0xFFB9C0CC)
private val TeamAccent = Color(0xFFCF9A9A)
private val TeamMutedButton = Color(0xFFD8D2C7)

@Composable
fun TeamFormationDetailScreen(
    state: TeamFormationDetailUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (TeamFormationDetailUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
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
                                appName = state.appName,
                                appSubtitle = state.appSubtitle,
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
}

@Composable
private fun TeamHeader(
    appName: String,
    appSubtitle: String?,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TeamTopBackground)
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(TeamAccent)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "<",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = appName,
                color = Color.Black,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            if (appSubtitle != null) {
                Text(
                    text = appSubtitle,
                    color = Color.Black.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
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
            text = title,
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
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE7DED3), CircleShape)
                    .clickable(onClick = onEditClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "E",
                    color = Color(0xFF7A7A7A),
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
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(if (isPrimary) 24.dp else 18.dp))
            .background(if (isPrimary) Color(0xFFF5F6FA) else TeamSurface)
            .border(1.dp, TeamBorder, RoundedCornerShape(if (isPrimary) 24.dp else 18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = TeamPlaceholder,
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
            text = "Ban chi huy",
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
                    text = "Chua co thong tin ban chi huy.",
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
                                .background(Color(0xFFEEF1F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = leader.initials,
                                color = TeamPlaceholder,
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
            text = "Hoat dong",
            color = TeamPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        if (displayActivities.isEmpty()) {
            Text(
                text = "Chua co hoat dong nao duoc ghi nhan.",
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
                    .background(if (activity.isAddButton) Color.Transparent else TeamSurface)
                    .border(
                        width = if (activity.isAddButton) 0.dp else 1.dp,
                        color = if (activity.isAddButton) Color.Transparent else TeamBorder,
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
                            .background(TeamMutedButton),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activity.label,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                } else {
                    Text(
                        text = activity.label,
                        color = TeamPlaceholder,
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
        Button(onClick = onRetry) {
            Text(text = "Thu lai")
        }
    }
}
