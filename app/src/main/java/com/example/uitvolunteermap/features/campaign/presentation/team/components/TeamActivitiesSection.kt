package com.example.uitvolunteermap.features.campaign.presentation.team.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.app.testing.VolunteerFlowTestTags
import com.example.uitvolunteermap.features.campaign.presentation.team.TeamActivityUiModel
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamAccent
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamAccentPressed
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamBorder
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamInverse
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamPrimary
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSecondary
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSurface
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSurfaceVariant

@Composable
internal fun TeamActivitiesSection(
    activities: List<TeamActivityUiModel>,
    showAddButton: Boolean,
    onAddClick: () -> Unit,
    onActivityClick: (Int) -> Unit
) {
    val displayActivities = if (showAddButton) activities
    else activities.filter { !it.isAddButton }

    val firstRow = displayActivities.take(3)
    val secondRow = displayActivities.drop(3).take(3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Hoạt động",
            color = TeamPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        if (displayActivities.isEmpty()) {
            Text(
                text = "Chưa có hoạt động nào được ghi nhận.",
                color = TeamSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else {
            ActivityRow(
                activities = firstRow,
                onAddClick = onAddClick,
                onActivityClick = onActivityClick
            )
            if (secondRow.isNotEmpty()) {
                ActivityRow(
                    activities = secondRow,
                    onAddClick = onAddClick,
                    onActivityClick = onActivityClick
                )
            }
        }
    }
}

@Composable
private fun ActivityRow(
    activities: List<TeamActivityUiModel>,
    onAddClick: () -> Unit,
    onActivityClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        activities.forEach { activity ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(108.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (activity.isAddButton) {
                            TeamSurfaceVariant
                        } else {
                            TeamSurface
                        },
                        RoundedCornerShape(18.dp)
                    )
                    .border(
                        width = if (activity.isAddButton) 0.dp else 1.dp,
                        color = if (activity.isAddButton) TeamSurfaceVariant else TeamBorder,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        if (activity.isAddButton) onAddClick() else onActivityClick(activity.id)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (activity.isAddButton) {
                    Box(
                        modifier = Modifier
                            .testTag(VolunteerFlowTestTags.TeamFormationAddActivity)
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(TeamAccent, TeamAccentPressed)
                                )
                            )
                            .border(1.dp, TeamAccentPressed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activity.label,
                            color = TeamInverse,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                } else {
                    Text(
                        text = activity.label,
                        color = TeamPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
