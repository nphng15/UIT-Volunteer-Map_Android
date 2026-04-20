package com.example.uitvolunteermap.features.home.presentation.volunteer

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.uitvolunteermap.core.ui.VolunteerBottomBar
import com.example.uitvolunteermap.core.ui.VolunteerBottomBarTab
import kotlinx.coroutines.launch

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
fun VolunteerHomeScreen(
    state: VolunteerHomeUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (VolunteerHomeUiEvent) -> Unit,
    onPostTabClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

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
                    when (selectedTab) {
                        VolunteerBottomBarTab.Home -> Unit
                        VolunteerBottomBarTab.Map -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Màn bản đồ sẽ được cập nhật sớm.")
                            }
                        }
                        VolunteerBottomBarTab.Post -> {
                            val firstCampaignId = state.campaigns.firstOrNull()?.id
                            if (firstCampaignId != null) {
                                onPostTabClick(firstCampaignId)
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Chưa có chiến dịch để mở bài viết.")
                                }
                            }
                        }
                        VolunteerBottomBarTab.Me -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Màn cá nhân sẽ được cập nhật sớm.")
                            }
                        }
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
                    ErrorState(
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
                                isGuest = state.isGuest
                            )
                        }
                        item {
                            OverviewStatsStrip(stats = state.stats)
                        }
                        item {
                            CampaignSectionHeader()
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

@Composable
private fun VolunteerBackdrop() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 44.dp)
                .size(184.dp)
                .background(ScreenPrimary.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-34).dp)
                .size(112.dp)
                .background(ScreenSecondary.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 22.dp)
                .size(88.dp)
                .background(ScreenAccent.copy(alpha = 0.08f), CircleShape)
        )
    }
}

@Composable
private fun VolunteerHomeHero(
    appName: String,
    isGuest: Boolean
) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 18.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = appName.ifBlank { "UIT · Tình nguyện" },
                    color = ScreenTextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Chào bạn.",
                    color = ScreenTextPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isGuest) "KHÁCH" else "TÌNH NGUYỆN",
                    color = ScreenPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .border(1.dp, ScreenBorder, PillShape)
                        .background(ScreenSurface, PillShape)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, ScreenBorder, CircleShape)
                        .background(ScreenSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⋯",
                        color = ScreenTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = editorialHeadline(
                fullText = "Tháng sáu.\nMùa của\nnhững chiến dịch.",
                highlight = "Mùa"
            ),
            color = ScreenTextPrimary,
            style = MaterialTheme.typography.displaySmall.copy(
                fontFamily = FontFamily.Serif,
                fontSize = 36.sp,
                lineHeight = 36.sp
            ),
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Bạn có 2 chiến dịch đang diễn ra và 4 bài viết chờ duyệt.",
            color = ScreenTextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun OverviewStatsStrip(stats: List<VolunteerStatUiModel>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, ScreenBorder, RoundedCornerShape(22.dp))
                    .background(ScreenSurface, RoundedCornerShape(22.dp))
                    .padding(horizontal = 12.dp, vertical = 14.dp),
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
    }
}

@Composable
private fun CampaignSectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "CHIẾN DỊCH ↑",
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
}

@Composable
private fun VolunteerCampaignCard(
    campaign: VolunteerCampaignUiModel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(CardShape)
            .clickable(onClick = onClick)
            .testTag(
                VolunteerFlowTestTags.volunteerHomeCampaignPrimaryAction(campaign.id)
            )
            .border(width = 1.dp, color = ScreenBorder, shape = CardShape)
            .background(ScreenSurface, CardShape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(178.dp)
                .clip(SmallCardShape)
                .background(
                    brush = Brush.linearGradient(
                        campaign.accentColors.toGradientColors(
                            fallback = listOf(ScreenSurfaceVariant, ScreenSurfaceRaised, ScreenSurface)
                        )
                    ),
                    shape = SmallCardShape
                )
                .border(1.dp, ScreenBorder, SmallCardShape)
        ) {
            Image(
                painter = painterResource(
                    id = campaign.coverImageResId.takeIf { it != 0 } ?: R.drawable.banner_mxh
                ),
                contentDescription = "Ảnh bìa ${campaign.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.10f),
                                Color.Transparent,
                                ScreenTextPrimary.copy(alpha = 0.14f)
                            )
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
                    .border(1.dp, ScreenBorder, PillShape)
                    .background(ScreenSurface, PillShape)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = campaign.dateRange,
                    color = ScreenTextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = campaign.title,
                color = ScreenTextPrimary,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    lineHeight = 26.sp
                ),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = campaign.description,
                color = ScreenTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = campaign.meta,
                color = ScreenTextMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EmptyVolunteerCampaignState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(1.dp, ScreenBorder, CardShape)
            .background(ScreenSurface, CardShape)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Chưa có chiến dịch để hiển thị.",
            color = ScreenTextPrimary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Thử tải lại để đồng bộ dữ liệu mới nhất.",
            color = ScreenTextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ScreenPrimary,
                contentColor = ScreenTextInverse
            )
        ) {
            Text(text = "Thử lại")
        }
    }
}

@Composable
private fun ErrorState(
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

private fun editorialHeadline(
    fullText: String,
    highlight: String
): AnnotatedString {
    return buildAnnotatedString {
        val index = fullText.indexOf(highlight)
        if (index >= 0) {
            append(fullText.substring(0, index))
            pushStyle(
                SpanStyle(
                    background = ScreenHighlight,
                    color = ScreenTextPrimary
                )
            )
            append(highlight)
            pop()
            append(fullText.substring(index + highlight.length))
        } else {
            append(fullText)
        }
    }
}

private fun List<Long>.toGradientColors(fallback: List<Color>): List<Color> {
    return if (isNotEmpty()) {
        map { Color(it) }
    } else {
        fallback
    }
}
