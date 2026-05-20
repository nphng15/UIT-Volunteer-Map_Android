package com.example.uitvolunteermap.features.campaign.presentation.team.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uitvolunteermap.core.ui.VolunteerTopBar
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamBorder
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamMuted
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamPrimaryAction
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamSurface
import com.example.uitvolunteermap.features.campaign.presentation.team.components.TeamDetailTokens.TeamTopBackground

@Composable
internal fun TeamHeader(
    isGuest: Boolean,
    onBackClick: () -> Unit
) {
    VolunteerTopBar(
        onBack = onBackClick,
        containerColor = TeamTopBackground,
        center = {
            Text(
                text = "UIT TÌNH NGUYỆN • ĐỘI HÌNH",
                color = TeamMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    )
}
