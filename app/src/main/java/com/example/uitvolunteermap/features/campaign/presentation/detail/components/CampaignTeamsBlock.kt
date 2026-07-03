package com.example.uitvolunteermap.features.campaign.presentation.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.uitvolunteermap.BuildConfig
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.R
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.features.campaign.presentation.detail.CampaignDetailTeamUiModel
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenBorder
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenHighlight
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenPrimary
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSurface
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSurfaceVariant
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextInverse
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextMuted
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextPrimary

@Composable
internal fun CampaignTeamsBlock(
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
        if (!team.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = resolveBackendImageUrl(team.imageUrl),
                contentDescription = "Ảnh đội hình ${team.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(
                    id = team.previewImageResId.takeIf { it != 0 } ?: R.drawable.muahexanh1
                ),
                contentDescription = "Ảnh đội hình ${team.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
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

private fun teamCardColors(index: Int): Triple<Color, Color, Color> {
    return when (index % 3) {
        0 -> Triple(ScreenTextPrimary, ScreenTextInverse, ScreenTextInverse)
        1 -> Triple(ScreenHighlight, ScreenTextPrimary, ScreenTextPrimary)
        else -> Triple(ScreenSurface, ScreenTextPrimary, ScreenPrimary)
    }
}
private fun resolveBackendImageUrl(raw: String): String {
    if (raw.startsWith("http://", ignoreCase = true) || raw.startsWith("https://", ignoreCase = true)) {
        return raw
    }
    val base = BuildConfig.BASE_URL.removeSuffix("/").removeSuffix("/api")
    return "$base/${raw.trimStart('/')}"
}
