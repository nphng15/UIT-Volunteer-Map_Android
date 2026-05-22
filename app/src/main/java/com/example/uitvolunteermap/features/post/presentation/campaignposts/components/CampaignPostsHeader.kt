package com.example.uitvolunteermap.features.post.presentation.campaignposts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.theme.Dimens
import com.example.uitvolunteermap.core.ui.theme.Shapes
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostsSummaryUiModel
import com.example.uitvolunteermap.features.post.presentation.campaignposts.CampaignPostsTeamUiModel

@Composable
internal fun PostsHeader(
    appName: String,
    campaignTitle: String,
    campaignSubtitle: String,
    summary: CampaignPostsSummaryUiModel,
    isRefreshing: Boolean,
    canCreatePost: Boolean,
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Spacing20, vertical = Dimens.Spacing16),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing14)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing10)
            ) {
                CircleIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Quay lai",
                    onClick = onBackClick
                )
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing2)) {
                    Text(
                        text = appName,
                        color = PostsScreenPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (isRefreshing) {
                            "Dang dong bo bai viet..."
                        } else {
                            "Bang tin chien dich"
                        },
                        color = PostsScreenSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            if (canCreatePost) {
                CreatePostButton(onClick = onCreateClick)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.Spacing6)) {
            Text(
                text = campaignTitle,
                color = PostsScreenPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = campaignSubtitle,
                color = PostsScreenSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing8)
        ) {
            SummaryPill(
                title = summary.totalPosts.toString(),
                subtitle = "Bai viet",
                modifier = Modifier.weight(1f)
            )
            SummaryPill(
                title = summary.totalTeams.toString(),
                subtitle = "Doi co post",
                modifier = Modifier.weight(1f)
            )
            SummaryPill(
                title = summary.latestUpdate,
                subtitle = "Cap nhat",
                modifier = Modifier.weight(1f)
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
            label = "Tat ca",
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
            .background(if (selected) PostsScreenAccentSoft else PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.RadiusPill))
            .clickable(onClick = onClick)
            .padding(horizontal = Dimens.Spacing12, vertical = Dimens.Spacing8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.Spacing6),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = "$count",
            color = if (selected) PostsScreenAccent else PostsScreenSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SummaryPill(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Shapes.Radius18))
            .background(PostsScreenPanel)
            .border(1.dp, PostsScreenBorder, RoundedCornerShape(Shapes.Radius18))
            .padding(horizontal = Dimens.Spacing10, vertical = Dimens.Spacing10),
        verticalArrangement = Arrangement.spacedBy(Dimens.Spacing4)
    ) {
        Text(
            text = title,
            color = PostsScreenPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            color = PostsScreenSecondary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
