package com.example.uitvolunteermap.features.campaign.presentation.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.detail.CampaignMapOverviewUiModel
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.CardShape
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenAccent
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenAccentPressed
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenBorder
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenDivider
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenHighlight
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenPrimary
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSecondary
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSurface
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenSurfaceVariant
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextInverse
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextMuted
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextPrimary
import com.example.uitvolunteermap.features.campaign.presentation.detail.components.CampaignDetailTokens.ScreenTextSecondary

@Composable
internal fun CampaignMapSection(
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
