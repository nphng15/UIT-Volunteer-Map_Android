package com.example.uitvolunteermap.features.post.presentation.campaignposts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostsTeamUiModel

@Composable
internal fun PostsHeader(
    appName: String,
    campaignTitle: String,
    canCreatePost: Boolean,
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    val highlightedTitle = rememberHighlightedHeadline(
        headline = if (campaignTitle.isBlank()) "Bảng tin chiến dịch" else campaignTitle
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Spacing20, vertical = Dimens.Spacing14),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing12)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Quay lại",
                onClick = onBackClick
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Shapes.RadiusPill))
                    .background(PostsScreenPanel)
                    .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.RadiusPill))
                    .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing4)
            ) {
                Text(
                    text = appName.uppercase(),
                    color = PostsScreenSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (canCreatePost) {
                CreatePostButton(onClick = onCreateClick)
            } else {
                Box(modifier = Modifier.size(40.dp))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6)) {
            Text(
                text = highlightedTitle,
                color = PostsScreenPrimary,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun TeamFilterRow(
    teams: List<CampaignPostsTeamUiModel>,
    selectedTeamId: Int?,
    onAllSelected: () -> Unit,
    onTeamSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Dimens.Spacing20),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
    ) {
        FilterChip(
            label = "Tất cả",
            count = teams.sumOf { it.postCount },
            selected = selectedTeamId == null,
            onClick = onAllSelected
        )
        teams.forEach { team ->
            FilterChip(
                label = team.label,
                count = team.postCount,
                selected = selectedTeamId == team.id,
                onClick = { onTeamSelected(team.id) }
            )
        }
    }
}

@Composable
internal fun FilterChip(
    label: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Shapes.RadiusPill))
            .background(if (selected) PostsScreenAccent else PostsScreenPanel)
            .border(
                1.dp,
                if (selected) PostsScreenAccent.copy(alpha = 0.18f) else PostsScreenBorder,
                RoundedCornerShape(Shapes.RadiusPill)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (selected) PostsScreenPanel else PostsScreenPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "$count",
            color = if (selected) PostsScreenPanel else PostsScreenAccent,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun rememberHighlightedHeadline(headline: String): AnnotatedString {
    return buildAnnotatedString {
        val words = headline.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.size <= 1) {
            append(headline)
            return@buildAnnotatedString
        }
        val highlightStart = headline.lastIndexOf(words.last())
        val highlightEnd = highlightStart + words.last().length
        append(headline.substring(0, highlightStart))
        withStyle(
            style = SpanStyle(background = PostsScreenWarning.copy(alpha = 0.38f))
        ) {
            append(headline.substring(highlightStart, highlightEnd))
        }
        append(headline.substring(highlightEnd))
    }
}
