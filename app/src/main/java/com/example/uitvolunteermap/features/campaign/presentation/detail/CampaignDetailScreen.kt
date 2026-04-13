package com.example.uitvolunteermap.features.campaign.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val DetailTopBackground = Color(0xFFF7F1D8)
private val DetailContentBackground = Color(0xFFFFFDF9)
private val DetailSoftBackground = Color(0xFFFFF8EF)
private val DetailBorder = Color(0xFFE7DED3)
private val DetailAccent = Color(0xFF1C3977)
private val DetailPrimary = Color(0xFF20303A)
private val DetailSecondary = Color(0xFF746D66)
private val DetailHighlight = Color(0xFFFFF4CC)
private val DetailWarm = Color(0xFFE06300)
private val DetailLogoBackground = Color(0xFFCF9A9A)

@Composable
fun CampaignDetailScreen(
    state: CampaignDetailUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CampaignDetailUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDescriptionExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = DetailContentBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DetailTopBackground, DetailContentBackground)
                    )
                )
        ) {
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
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            DetailTopBar(
                                appName = state.appName,
                                onBackClick = { onEvent(CampaignDetailUiEvent.BackClicked) }
                            )
                        }
                        item {
                            CampaignHeroSection(
                                title = state.title,
                                schedule = state.schedule,
                                heroHeadline = state.heroHeadline,
                                heroSupportingText = state.heroSupportingText
                            )
                        }
                        item {
                            CampaignStatsRow(stats = state.stats)
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
                                onViewAllPosts = {
                                    onEvent(CampaignDetailUiEvent.ViewAllPostsClicked)
                                },
                                onPostClick = { postId ->
                                    onEvent(CampaignDetailUiEvent.PostClicked(postId))
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

@Composable
private fun DetailTopBar(
    appName: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DetailTopBackground)
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DetailLogoBackground)
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
        Text(
            text = appName,
            color = DetailPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun CampaignHeroSection(
    title: String,
    schedule: String,
    heroHeadline: String,
    heroSupportingText: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                color = Color.Black,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = schedule,
                color = Color.Black.copy(alpha = 0.88f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(196.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF6D839A), Color(0xFF112331))
                    )
                )
                .padding(18.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = heroHeadline,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = heroSupportingText,
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun CampaignStatsRow(stats: List<CampaignDetailStatUiModel>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, DetailBorder, RoundedCornerShape(22.dp))
                    .background(DetailContentBackground, RoundedCornerShape(22.dp))
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DetailHighlight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stat.label.take(1),
                        color = DetailPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stat.value,
                    color = DetailAccent,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stat.label,
                    color = DetailAccent,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CampaignDescriptionBlock(
    description: String,
    isExpanded: Boolean,
    onReadMore: () -> Unit
) {
    val displayedText = if (isExpanded) {
        description
    } else {
        description.take(220).trimEnd() + "..."
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .border(1.dp, DetailBorder, RoundedCornerShape(30.dp))
            .background(DetailContentBackground, RoundedCornerShape(30.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Mo ta",
            color = DetailAccent,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = displayedText,
            color = DetailSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = if (isExpanded) "Thu gon" else "Xem them",
            color = DetailAccent,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onReadMore)
        )
    }
}

@Composable
private fun CampaignTeamsBlock(
    title: String,
    teams: List<CampaignDetailTeamUiModel>,
    onTeamClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = DetailAccent,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )

        // Tất cả card có cùng kích thước 262dp; card tiếp theo tự peek từ phần overflow khi scroll
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            teams.forEach { team ->
                Box(
                    modifier = Modifier
                        .width(262.dp)
                        .height(196.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = team.accentColors.map { Color(it) }
                            )
                        )
                        .clickable { onTeamClick(team.id) }
                        .padding(18.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = team.name,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(teams.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = if (index == 0) 20.dp else 6.dp, height = 6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (index == 0) DetailPrimary else DetailBorder)
                )
            }
        }
    }
}

@Composable
private fun CampaignPostsBlock(
    posts: List<CampaignDetailPostUiModel>,
    onViewAllPosts: () -> Unit,
    onPostClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .border(1.dp, DetailBorder, RoundedCornerShape(30.dp))
            .background(DetailContentBackground, RoundedCornerShape(30.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Bai viet",
            color = DetailAccent,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        posts.forEach { post ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, DetailBorder, RoundedCornerShape(24.dp))
                    .background(DetailSoftBackground)
                    .clickable { onPostClick(post.id) }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 92.dp, height = 84.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = post.accentColors.map { Color(it) }
                            )
                        )
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (post.isLightBadge) Color(0xD8FFFDF9) else Color(0xCC20303A)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = post.teamName,
                            color = if (post.isLightBadge) DetailAccent else Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = post.title,
                        color = DetailAccent,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = post.publishedAt,
                        color = DetailWarm,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = post.summary,
                        color = Color(0xFF6D839A),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Text(
            text = "Xem tat ca bai viet",
            color = DetailAccent,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.clickable(onClick = onViewAllPosts)
        )
    }
}

@Composable
private fun CampaignMapSection(
    mapOverview: CampaignMapOverviewUiModel,
    onOpenMaps: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .border(1.dp, DetailBorder, RoundedCornerShape(30.dp))
            .background(DetailSoftBackground, RoundedCornerShape(30.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = mapOverview.headerTitle,
            color = DetailAccent,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(228.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF7F1D8))
                .border(1.dp, DetailBorder, RoundedCornerShape(24.dp))
        ) {
            val width = maxWidth
            val height = maxHeight

            Box(
                modifier = Modifier
                    .offset(x = 12.dp, y = 12.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DetailHighlight)
                    .border(1.dp, DetailBorder, RoundedCornerShape(18.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = mapOverview.selectedArea,
                    color = DetailAccent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            MapRoad(
                modifier = Modifier
                    .offset(x = (-10).dp, y = 82.dp)
                    .width(width)
                    .height(16.dp)
            )
            MapRoad(
                modifier = Modifier
                    .offset(x = width * 0.36f, y = (-18).dp)
                    .width(18.dp)
                    .height(height + 36.dp)
            )
            MapRoad(
                modifier = Modifier
                    .offset(x = 48.dp, y = 146.dp)
                    .width(width * 0.72f)
                    .height(14.dp)
            )

            mapOverview.locations.forEach { location ->
                Box(
                    modifier = Modifier
                        .offset(
                            x = width * location.xFraction,
                            y = height * location.yFraction
                        )
                        .size(if (location.isHighlighted) 30.dp else 26.dp)
                        .clip(CircleShape)
                        .background(if (location.isHighlighted) DetailAccent else DetailPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            mapOverview.locations.forEach { location ->
                Text(
                    text = location.label,
                    modifier = Modifier.offset(
                        x = width * location.xFraction + 12.dp,
                        y = height * location.yFraction - 26.dp
                    ),
                    color = Color(0xFF6D839A),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        val highlightedLocation = mapOverview.locations.firstOrNull { it.isHighlighted }
            ?: mapOverview.locations.firstOrNull()

        highlightedLocation?.let { location ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = location.supportingText,
                    color = DetailAccent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MapMetaChip(text = "3 diem", isWarm = false)
                    MapMetaChip(text = "Moi nhat", isWarm = true)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mapOverview.footerTitle,
                    color = DetailAccent,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mapOverview.footerDescription,
                    color = DetailSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = onOpenMaps,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DetailPrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = mapOverview.ctaLabel,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MapRoad(modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.White)
    )
}

@Composable
private fun MapMetaChip(
    text: String,
    isWarm: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isWarm) DetailSoftBackground else DetailHighlight)
            .border(
                width = if (isWarm) 0.dp else 1.dp,
                color = if (isWarm) Color.Transparent else DetailBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = if (isWarm) DetailWarm else DetailAccent,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DetailErrorState(
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
            color = DetailPrimary,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text(text = "Thu lai")
        }
    }
}
