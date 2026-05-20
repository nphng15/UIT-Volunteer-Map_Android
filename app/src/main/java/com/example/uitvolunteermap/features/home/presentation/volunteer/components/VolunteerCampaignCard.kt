package com.example.uitvolunteermap.features.home.presentation.volunteer.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.R
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.features.home.presentation.volunteer.VolunteerCampaignUiModel
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.CardShape
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.PillShape
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenBorder
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenSurface
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenSurfaceRaised
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenSurfaceVariant
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextMuted
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextPrimary
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.ScreenTextSecondary
import com.example.uitvolunteermap.features.home.presentation.volunteer.components.VolunteerHomeTokens.SmallCardShape

@Composable
internal fun CampaignSectionHeader(
    onSeeAll: () -> Unit = {}
) {
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
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onSeeAll)
        )
    }
}

@Composable
internal fun VolunteerCampaignCard(
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

private fun List<Long>.toGradientColors(fallback: List<Color>): List<Color> {
    return if (isNotEmpty()) {
        map { Color(it) }
    } else {
        fallback
    }
}
