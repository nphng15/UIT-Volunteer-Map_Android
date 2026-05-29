package com.example.uitvolunteermap.features.campaign.presentation.team.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.features.campaign.presentation.team.TeamLeaderUiModel
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamBorder
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamPrimary
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamPrimaryAction
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSecondary
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSurface
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSurfaceVariant

@Composable
internal fun TeamLeadersSection(
    leaders: List<TeamLeaderUiModel>,
    onLeaderClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "Ban chỉ huy",
            color = TeamPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (leaders.isEmpty()) {
                Text(
                    text = "Chưa có thông tin ban chỉ huy.",
                    color = TeamSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                leaders.forEach { leader ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onLeaderClick(leader.id) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(86.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(TeamSurfaceVariant, TeamSurface)
                                    )
                                )
                                .border(1.dp, TeamBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = leader.initials,
                                color = TeamPrimaryAction,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = leader.role,
                            color = TeamPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = leader.name,
                            color = TeamSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
