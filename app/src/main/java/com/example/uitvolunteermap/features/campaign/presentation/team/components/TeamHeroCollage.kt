package com.example.uitvolunteermap.features.campaign.presentation.team.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.example.uitvolunteermap.BuildConfig
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.team.TeamHeroCardUiModel
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamBorder
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamMuted
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamPrimaryAction
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSurface
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSurfaceSoft
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSurfaceVariant

@Composable
internal fun TeamHeroCollage(
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
                imageUrl = it.imageUrl,
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
                imageUrl = it.imageUrl,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(width = 140.dp, height = 142.dp),
                isPrimary = true
            )
        }

        rightCard?.let {
            PlaceholderHeroCard(
                label = it.label,
                imageUrl = it.imageUrl,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp)
                    .size(width = 110.dp, height = 112.dp),
                isPrimary = false
            )
        }

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
    imageUrl: String?,
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
        if (!imageUrl.isNullOrBlank()) {
            Image(
                painter = rememberAsyncImagePainter(resolveBackendImageUrl(imageUrl)),
                contentDescription = label,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = label,
                color = if (isPrimary) TeamPrimaryAction else TeamMuted,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
private fun resolveBackendImageUrl(raw: String): String {
    if (raw.startsWith("http://", ignoreCase = true) || raw.startsWith("https://", ignoreCase = true)) {
        return raw
    }
    val base = BuildConfig.BASE_URL.removeSuffix("/").removeSuffix("/api")
    return "$base/${raw.trimStart('/')}"
}
