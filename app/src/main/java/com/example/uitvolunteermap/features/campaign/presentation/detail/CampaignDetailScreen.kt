package com.example.uitvolunteermap.features.campaign.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.R
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags

private val ScreenBackground = Color(0xFFFBFCFF)
private val ScreenBackgroundTop = Color(0xFFDDF3F8)
private val ScreenBackgroundBottom = Color(0xFFF9FDFF)
private val ScreenSurface = Color(0xFFFFFFFF)
private val ScreenSurfaceVariant = Color(0xFFF4F9FF)
private val ScreenSurfaceRaised = Color(0xFFF1FDF5)
private val ScreenBorder = Color(0xFFD8E5EC)
private val ScreenDivider = Color(0xFFE4EAF5)
private val ScreenTextPrimary = Color(0xFF0B1A3B)
private val ScreenTextSecondary = Color(0xFF55648A)
private val ScreenTextMuted = Color(0xFF8A97B8)
private val ScreenTextInverse = Color(0xFFFFFFFF)
private val ScreenPrimary = Color(0xFF2563FF)
private val ScreenSecondary = Color(0xFF06B6D4)
private val ScreenAccent = Color(0xFFFF5A3C)
private val ScreenAccentPressed = Color(0xFFE84423)
private val ScreenHighlight = Color(0xFFFEF3C7)

private val CardShape = RoundedCornerShape(30.dp)
private val SmallCardShape = RoundedCornerShape(24.dp)
private val PillShape = RoundedCornerShape(999.dp)

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
                        modifier = Modifier.fillMaxSize(),
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
private fun CampaignBackdrop() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 42.dp)
                .size(188.dp)
                .background(ScreenPrimary.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-36).dp)
                .size(120.dp)
                .background(ScreenSecondary.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 102.dp, end = 18.dp)
                .size(86.dp)
                .background(ScreenAccent.copy(alpha = 0.08f), CircleShape)
        )
    }
}

@Composable
private fun DetailTopBar(
    routeLabel: String,
    onBackClick: () -> Unit
) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 18.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ScreenSurface)
                    .border(1.dp, ScreenBorder, CircleShape)
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "‹",
                    color = ScreenTextPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = routeLabel,
                color = ScreenTextMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ScreenSurface)
                    .border(1.dp, ScreenBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = ScreenTextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CampaignHeroSection(
    routeSubtitle: String,
    title: String,
    supportingText: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = routeSubtitle.ifBlank { "01.06 - 30.08.26 · 90 NGÀY" },
            color = ScreenTextMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = editorialTitle(title),
            color = ScreenTextPrimary,
            style = MaterialTheme.typography.displaySmall.copy(
                fontFamily = FontFamily.Serif,
                fontSize = 36.sp,
                lineHeight = 36.sp
            ),
            fontWeight = FontWeight.ExtraBold
        )
        if (supportingText.isNotBlank()) {
            Text(
                text = supportingText,
                color = ScreenTextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CampaignStatsGrid(stats: List<CampaignDetailStatUiModel>) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stats.chunked(2).forEach { rowStats ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowStats.forEach { stat ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, ScreenBorder, RoundedCornerShape(22.dp))
                            .background(ScreenSurface, RoundedCornerShape(22.dp))
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stat.value,
                            color = ScreenTextPrimary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = stat.label,
                            color = ScreenTextSecondary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (rowStats.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
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
            .border(1.dp, ScreenBorder, CardShape)
            .background(ScreenSurface, CardShape)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "CHIẾN DỊCH",
            color = ScreenTextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = displayedText,
            color = ScreenTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = if (isExpanded) "Rút gọn ↓" else "Đọc thêm ↓",
            color = ScreenAccentPressed,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .border(1.dp, ScreenBorder, PillShape)
                .background(ScreenSurfaceVariant, PillShape)
                .clickable(onClick = onReadMore)
                .padding(horizontal = 12.dp, vertical = 8.dp)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${title.ifBlank { "ĐỘI HÌNH" }} · ${teams.size}",
                color = ScreenTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "xem tất cả →",
                color = ScreenTextMuted,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            teams.forEachIndexed { index, team ->
                val backgroundColor = teamCardColors(index).first
                val width = 244.dp
                Box(
                    modifier = Modifier
                        .padding(end = if (index == teams.lastIndex) 0.dp else 12.dp)
                        .width(width)
                        .height(154.dp)
                        .testTag(VolunteerFlowTestTags.campaignDetailTeamCard(team.id))
                        .clip(RoundedCornerShape(24.dp))
                        .background(backgroundColor)
                        .border(1.dp, ScreenBorder, RoundedCornerShape(24.dp))
                        .clickable { onTeamClick(team.id) }
                ) {
                    TeamPreviewImage(
                        team = team,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamPreviewImage(
    team: CampaignDetailTeamUiModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(ScreenSurfaceVariant)
            .border(1.dp, ScreenBorder, RoundedCornerShape(24.dp))
    ) {
        val overlayColor = team.accentColors.firstOrNull()?.let(::Color) ?: ScreenPrimary
        Image(
            painter = painterResource(
                id = team.previewImageResId.takeIf { it != 0 } ?: R.drawable.muahexanh1
            ),
            contentDescription = "Ảnh đội hình ${team.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            overlayColor.copy(alpha = 0.24f),
                            ScreenTextPrimary.copy(alpha = 0.82f)
                        )
                    )
                )
        )
        Text(
            text = team.name,
            color = ScreenTextInverse,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                lineHeight = 26.sp
            ),
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        )
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
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BÀI VIẾT MỚI",
                color = ScreenTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "→ TẤT CẢ BÀI VIẾT",
                color = ScreenTextMuted,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .testTag(VolunteerFlowTestTags.CampaignDetailViewAllPosts)
                    .clickable(onClick = onViewAllPosts)
            )
        }

        if (posts.isEmpty()) {
            Text(
                text = "Chưa có bài viết nào cho chiến dịch này.",
                color = ScreenTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            posts.forEach { post ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ScreenBorder, CardShape)
                        .background(ScreenSurface, CardShape)
                        .clickable { onPostClick(post.id) }
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(174.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.linearGradient(
                                    post.accentColors.toGradientColors(
                                        fallback = listOf(ScreenSurfaceVariant, ScreenSurfaceRaised, ScreenSurface)
                                    )
                                )
                            )
                            .border(1.dp, ScreenBorder, RoundedCornerShape(22.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ẢNH BÀI VIẾT · ${post.id}",
                            color = ScreenTextPrimary.copy(alpha = 0.42f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.teamName,
                            color = ScreenPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = post.publishedAt,
                            color = ScreenTextMuted,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = post.title,
                        color = ScreenTextPrimary,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontSize = 22.sp,
                            lineHeight = 24.sp
                        ),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = post.summary,
                        color = ScreenTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Đọc thêm ↓",
                            color = ScreenAccentPressed,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sửa",
                            color = ScreenTextMuted,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Xóa",
                            color = ScreenAccent,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
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
            .border(1.dp, ScreenBorder, CardShape)
            .background(ScreenSurface, CardShape)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mapOverview.headerTitle,
                color = ScreenTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = mapOverview.selectedArea,
                color = ScreenTextMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(228.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(ScreenSurfaceVariant, ScreenSurface)
                    )
                )
                .border(1.dp, ScreenBorder, RoundedCornerShape(24.dp))
        ) {
            val width = maxWidth
            val height = maxHeight

            Box(
                modifier = Modifier
                    .offset(x = 12.dp, y = 12.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(ScreenHighlight)
                    .border(1.dp, ScreenBorder, RoundedCornerShape(18.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = mapOverview.selectedArea,
                    color = ScreenTextPrimary,
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
                        .background(if (location.isHighlighted) ScreenAccent else ScreenSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ScreenTextInverse)
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
                    color = ScreenTextMuted,
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
                    color = ScreenTextPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MapMetaChip(text = "3 điểm", isWarm = false)
                    MapMetaChip(text = "Mới nhất", isWarm = true)
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
                    color = ScreenTextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mapOverview.footerDescription,
                    color = ScreenTextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = onOpenMaps,
                modifier = Modifier.fillMaxWidth(0.52f),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ScreenAccent,
                    contentColor = ScreenTextInverse
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
            .background(ScreenDivider)
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
            .background(if (isWarm) ScreenHighlight else ScreenSurfaceVariant)
            .border(
                width = 1.dp,
                color = ScreenBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = if (isWarm) ScreenAccentPressed else ScreenPrimary,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ScreenBorder, CardShape)
                .background(ScreenSurface, CardShape)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = ScreenTextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ScreenAccent,
                    contentColor = ScreenTextInverse
                )
            ) {
                Text(text = "Thử lại")
            }
        }
    }
}

private fun editorialTitle(text: String): AnnotatedString {
    val yearMatch = Regex("""\b\d{4}\b""").find(text)
    if (yearMatch == null) {
        return AnnotatedString(text)
    }

    return buildAnnotatedString {
        append(text.substring(0, yearMatch.range.first))
        pushStyle(
            SpanStyle(
                background = ScreenHighlight,
                color = ScreenTextPrimary
            )
        )
        append(yearMatch.value)
        pop()
        append(text.substring(yearMatch.range.last + 1))
    }
}

private fun List<Long>.toGradientColors(fallback: List<Color>): List<Color> {
    return if (isNotEmpty()) {
        map { Color(it) }
    } else {
        fallback
    }
}

private fun teamCardColors(index: Int): Triple<Color, Color, Color> {
    return when (index % 3) {
        0 -> Triple(ScreenTextPrimary, ScreenTextInverse, ScreenTextInverse)
        1 -> Triple(ScreenHighlight, ScreenTextPrimary, ScreenTextPrimary)
        else -> Triple(ScreenSurface, ScreenTextPrimary, ScreenPrimary)
    }
}
