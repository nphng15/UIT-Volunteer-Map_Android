package com.example.uitvolunteermap.features.home.presentation.volunteer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.uitvolunteermap.R
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.ColorTokens

private val HomeBorder = Color(0xFFE7DED3)
private val HomeAccentSoft = Color(0xFFFFF4CC)
private val HomeLogoBackground = Color(0xFFCF9A9A)
private val HomePrimaryText = Color(0xFF1F1A17)
private val HomeSecondaryText = Color(0xFF625750)
private val HomeActionText = Color(0xFF342222)

@Composable
fun VolunteerHomeScreen(
    state: VolunteerHomeUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (VolunteerHomeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        // Transparent so the full-screen gradient below covers behind status bar + nav bar
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(ColorTokens.ScreenBackgroundTop, ColorTokens.ScreenBackgroundBottom)
                    )
                )
        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            VolunteerHomeHeader(appName = state.appName)
                        }
                        item {
                            OverviewStatsRow(stats = state.stats)
                        }
                        item {
                            SectionHeader(title = "Tong hop chien dich")
                        }
                        items(state.campaigns, key = { it.id }) { campaign ->
                            VolunteerCampaignCard(
                                campaign = campaign,
                                onPrimaryClick = {
                                    onEvent(
                                        VolunteerHomeUiEvent.CampaignPrimaryClicked(campaign.id)
                                    )
                                },
                                onSecondaryClick = {
                                    onEvent(
                                        VolunteerHomeUiEvent.CampaignSecondaryClicked(campaign.id)
                                    )
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
        } // end gradient Box
    }
}

@Composable
private fun VolunteerHomeHeader(appName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorTokens.ScreenBackgroundTop)
            .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .background(HomeLogoBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = appName,
                color = HomePrimaryText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun OverviewStatsRow(stats: List<VolunteerStatUiModel>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            OverviewStatCard(
                stat = stat,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OverviewStatCard(
    stat: VolunteerStatUiModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(width = 1.dp, color = HomeBorder, shape = RoundedCornerShape(28.dp))
            .background(ColorTokens.ScreenBackgroundBottom, RoundedCornerShape(28.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(HomeAccentSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconForStatLabel(stat.label),
                contentDescription = stat.label,
                tint = HomePrimaryText,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stat.value,
            color = HomePrimaryText,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stat.label,
            color = HomeSecondaryText,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 20.dp),
        color = HomePrimaryText,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun VolunteerCampaignCard(
    campaign: VolunteerCampaignUiModel,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(
                color = ColorTokens.ScreenBackgroundBottom,
                shape = RoundedCornerShape(30.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0x14000000),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(116.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.volunteer_demo_image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // gradient overlay giữ lại màu accent của từng campaign
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = campaign.accentColors.map { Color(it).copy(alpha = 0.45f) }
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = campaign.title,
            color = HomePrimaryText,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = campaign.dateRange,
            color = HomeSecondaryText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = campaign.description,
            color = HomeSecondaryText,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = campaign.meta,
            color = HomeSecondaryText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onPrimaryClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HomeAccentSoft,
                    contentColor = HomeActionText
                )
            ) {
                Text(
                    text = campaign.primaryActionLabel,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onSecondaryClick,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = HomePrimaryText
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = campaign.secondaryActionLabel,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Map stat label -> icon: "Dang dien ra" = Flag, "Sap mo" = CalendarMonth, "Dia diem" = LocationOn
private fun iconForStatLabel(label: String): ImageVector = when {
    label.contains("dien ra", ignoreCase = true) -> Icons.Filled.Flag
    label.contains("sap", ignoreCase = true) -> Icons.Filled.CalendarMonth
    label.contains("dia diem", ignoreCase = true) -> Icons.Filled.LocationOn
    else -> Icons.Filled.Info
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
        Text(
            text = message,
            color = HomePrimaryText,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text(text = "Thu lai")
        }
    }
}
